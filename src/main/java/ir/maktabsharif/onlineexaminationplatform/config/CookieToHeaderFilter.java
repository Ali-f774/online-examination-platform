package ir.maktabsharif.onlineexaminationplatform.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class CookieToHeaderFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getHeader("Authorization") == null && request.getCookies() != null){
            for (Cookie cookie: request.getCookies()){
                if ("jwt".equals(cookie.getName())){
                    String token = cookie.getValue();
                    request = new HttpServletRequestWrapper(request){
                        @Override
                        public String getHeader(String name){
                            if ("Authorization".equalsIgnoreCase(name)){
                                return "Bearer " + token;
                            }
                            return super.getHeader(name);
                        }
                    };
                    break;
                }
            }
        }
            filterChain.doFilter(request, response);

    }
}
