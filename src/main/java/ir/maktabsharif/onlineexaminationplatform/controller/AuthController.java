package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService service;

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @PreAuthorize("permitAll()")
    @GetMapping("/logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("jwt",null);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String authorize(Authentication authentication){

       if (authentication == null)
            return "redirect:/login";
        List<@Nullable String> authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        boolean roleManager = authorities.contains("ROLE_MANAGER");
        boolean roleProfessor = authorities.contains("ROLE_PROFESSOR");
        boolean roleStudent = authorities.contains("ROLE_STUDENT");
        if (roleManager)
            return "manager-dashboard";
        if (roleProfessor)
            return "professor-dashboard";
        if (roleStudent)
            return  "student-dashboard";

        throw new AccessDeniedException("Access Denied");
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }



}
