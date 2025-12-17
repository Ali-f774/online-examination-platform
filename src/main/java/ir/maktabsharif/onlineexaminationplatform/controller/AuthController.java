package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

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
    public String authorize(Principal principal){

       if (principal == null)
            return "redirect:/login";

       User user = service.findByUsername(principal.getName());
       return switch (user.getRole()){
            case "MANAGER" -> "manager-dashboard";

            case "PROFESSOR" -> "professor-dashboard";

            case "STUDENT" -> "student-dashboard";

            default -> "redirect:/login";
       };
    }

    @GetMapping("/register")
    public String register(){
        return "register";
    }



}
