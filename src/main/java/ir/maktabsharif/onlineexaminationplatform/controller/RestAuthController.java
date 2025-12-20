package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.EditDto;
import ir.maktabsharif.onlineexaminationplatform.dto.LoginReq;
import ir.maktabsharif.onlineexaminationplatform.dto.RegisterReq;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class RestAuthController {

    private final DataMapper mapper;
    private final AuthenticationManager manager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final UserService service;
    private final CourseService courseService;
    private final PasswordEncoder encoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq loginReq, HttpServletResponse response){
        manager.authenticate(
                new UsernamePasswordAuthenticationToken(loginReq.username(),loginReq.password())
        );
        final UserDetails userDetails = userDetailsService.loadUserByUsername(loginReq.username());
        final String token = jwtUtil.generateToken(userDetails.getUsername());
        Cookie cookie = new Cookie("jwt",token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60);
        response.addCookie(cookie);

        return ResponseEntity.ok("Login SuccessFull");
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq registerReq){
        User user = null;
        if (registerReq.role().equals(Role.PROFESSOR))
            user = mapper.registerDtoToProfessor(registerReq);
        if (registerReq.role().equals(Role.STUDENT))
            user = mapper.registerDtoToStudent(registerReq);
        user.setPassword(encoder.encode(user.getPassword()));
        service.addOrUpdate(user);
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
