package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.dto.DetailsUserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

    private final UserService service;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DetailsUserDto user = service.findDtoByUsername(username);
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getAuthorities().forEach(s -> authorities.add(new SimpleGrantedAuthority("ROLE_"+s)));
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getIsEnable(),
                true,
                true,
                true,
                authorities
        );
    }
}
