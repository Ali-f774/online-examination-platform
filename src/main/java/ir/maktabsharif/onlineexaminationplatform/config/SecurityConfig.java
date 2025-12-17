package ir.maktabsharif.onlineexaminationplatform.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter filter;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http){
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/api/v1/edit","/manager/add-course","/manager/edit-course","/api/v1/manager/course/add-professor","/api/v1/manager/course/add-student","/api/v1/manager/course/remove"))
        .formLogin(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/register").permitAll()
                .requestMatchers("/api/v1/login").permitAll()
                .requestMatchers("/login").permitAll()
                .requestMatchers("/register").permitAll()
                .requestMatchers("/manager/edit").hasRole("MANAGER")
                .requestMatchers("/home").permitAll()
                .requestMatchers("/css/**","/js/**","/images/**").permitAll()
                .anyRequest().authenticated()
        )
                .exceptionHandling(exc -> exc
                        .authenticationEntryPoint(entryPoint())
                )

        .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }


    @Bean
    public AuthenticationEntryPoint entryPoint(){
        return (req,res,ex) -> {
            res.sendRedirect("/home");
        };
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
        return authenticationConfiguration.getAuthenticationManager();
    }

}
