package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.EditDto;
import ir.maktabsharif.onlineexaminationplatform.dto.LoginReq;
import ir.maktabsharif.onlineexaminationplatform.dto.RegisterReq;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.KeycloakAdminService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class RestAuthController {

    private final DataMapper mapper;
    private final UserService service;
    private final CourseService courseService;
    private final KeycloakAdminService keycloakAdminService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginReq, HttpServletResponse response){
        RestTemplate restTemplate = new RestTemplate();
        User user;
        try {
            user = service.findByUsername(loginReq.username());
        }catch (UsernameNotFoundException e){
            throw new BadCredentialsException("");
        }
        if (!user.getIsEnable())
            throw new DisabledException("");

        MultiValueMap<String,String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "spring-backend");
        form.add("username", loginReq.username());
        form.add("password", loginReq.password());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<?> entity = new HttpEntity<>(form,headers);

    try {
        ResponseEntity<Map> kcResponse = restTemplate.postForEntity(
                "http://localhost:9090/realms/OEP/protocol/openid-connect/token",
                entity,
                Map.class
        );

        String token = (String) kcResponse.getBody().get("access_token");

        Cookie cookie = new Cookie("jwt", token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok("Login SuccessFull");
    }catch (HttpClientErrorException e){
        throw new BadCredentialsException("");
    }
    }

    @Transactional
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq registerReq){
        User user = null;
        if (registerReq.role().equals(Role.PROFESSOR))
            user = mapper.registerDtoToProfessor(registerReq);
        if (registerReq.role().equals(Role.STUDENT))
            user = mapper.registerDtoToStudent(registerReq);
        if (user == null)
            throw new AccessDeniedException("");
        User savedUser = service.addOrUpdate(user);
        String keycloakId = keycloakAdminService.createUser(registerReq);
        keycloakAdminService.updateClientRole(keycloakId,registerReq.role().toString(),registerReq.role().toString());
        savedUser.setKeycloakId(keycloakId);
        service.addOrUpdate(savedUser);
        return ResponseEntity.ok("Successful Registration");
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/edit")
    public ResponseEntity<?> edit(@Valid @RequestBody EditDto dto){
        User user = service.findById(dto.id());
        service.update(user,dto);
        return ResponseEntity.ok("Successful Registration");
    }

    public record ProfessorCourse(Long courseId,Long professorId){}
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/course/add-professor")
    public ResponseEntity<?> addProfessor(@RequestBody ProfessorCourse dto){
        User user = service.findById(dto.professorId);
        Course course = courseService.findById(dto.courseId);
        if (user instanceof Professor professor){
            course.setProfessor(professor);
            professor.getCourses().add(course);
            service.addOrUpdate(professor);
            courseService.addOrUpdate(course);
            return ResponseEntity.ok("successfully add");
        }
        return ResponseEntity.status(400).body("user is not a professor");
    }
    public record StudentCourse(Long courseId,Long studentId){}
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/course/add-student")
    public ResponseEntity<?> addStudent(@RequestBody StudentCourse dto) {
        User user = service.findById(dto.studentId);
        Course course = courseService.findById(dto.courseId);
        if (user instanceof Student student) {
            course.getStudents().add(student);
            student.getCourses().add(course);
            service.addOrUpdate(student);
            courseService.addOrUpdate(course);
            return ResponseEntity.ok("successfully add");
        }
        return ResponseEntity.status(400).body("user is not a student");
    }

    public record CourseId(Long courseId){}
    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/manager/course/remove")
    public ResponseEntity<?> removeCourse(@RequestBody CourseId dto) {
        Course course = courseService.findById(dto.courseId);
        courseService.deleteById(course.getId());
        return ResponseEntity.ok("successfully deleted");
    }

}
