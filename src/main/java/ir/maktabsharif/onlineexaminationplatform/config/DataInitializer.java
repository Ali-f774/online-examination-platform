//package ir.maktabsharif.onlineexaminationplatform.config;
//
//import ir.maktabsharif.onlineexaminationplatform.model.Professor;
//import ir.maktabsharif.onlineexaminationplatform.model.Role;
//import ir.maktabsharif.onlineexaminationplatform.model.Student;
//import ir.maktabsharif.onlineexaminationplatform.model.User;
//import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
//import ir.maktabsharif.onlineexaminationplatform.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.stereotype.Component;
//
//import java.util.HashSet;
//import java.util.List;
//
//@Component
//@RequiredArgsConstructor
//public class DataInitializer implements CommandLineRunner {
//
//    private final UserService service;
//    private final CourseService courseService;
//    private final PasswordEncoder encoder;
//
//    @Override
//    public void run(String... args) throws Exception {
//        service.addOrUpdate(User.builder()
//                .username("Ali")
//                .password(encoder.encode("1234"))
//                .email("alifaraji773@gmail.com")
//                .firstName("Ali")
//                .lastName("Faraji")
//                .isEnable(true)
//                .nationalCode("0200374397")
//                .role(Role.MANAGER)
//                .build()
//        );
//        service.addOrUpdate(Professor.builder()
//                .username("sara")
//                .password(encoder.encode("1234"))
//                .email("sara773@gmail.com")
//                .firstName("Sara")
//                .lastName("Ahmadi")
//                .isEnable(false)
//                .nationalCode("0200374398")
//                .role(Role.PROFESSOR)
//                .build()
//        );
//        service.addOrUpdate(Student.builder()
//                .username("reza")
//                .password(encoder.encode("1234"))
//                .email("reza773@gmail.com")
//                .firstName("Reza")
//                .lastName("Jafari")
//                .isEnable(false)
//                .nationalCode("0200374399")
//                .role(Role.STUDENT)
//                .build()
//        );
//        Student ahmad = Student.builder()
//                .username("ahmad")
//                .password(encoder.encode("1234"))
//                .email("ahmad773@gmail.com")
//                .firstName("Ahmad")
//                .lastName("Taheri")
//                .isEnable(true)
//                .nationalCode("0200374400")
//                .role(Role.STUDENT)
//                .build();
//        service.addOrUpdate(ahmad);
//        Student asghar = Student.builder()
//                .username("asghar")
//                .password(encoder.encode("1234"))
//                .email("asghar773@gmail.com")
//                .firstName("Asghar")
//                .lastName("Asghari")
//                .isEnable(true)
//                .nationalCode("0200374401")
//                .role(Role.STUDENT)
//                .build();
//
//        service.addOrUpdate(asghar);
//        Student zahra = Student.builder()
//                .username("zahra")
//                .password(encoder.encode("1234"))
//                .email("zahra773@gmail.com")
//                .firstName("Zahra")
//                .lastName("Akbari")
//                .isEnable(true)
//                .nationalCode("0200374402")
//                .role(Role.STUDENT)
//                .build();
//
//        service.addOrUpdate(zahra);
//        Professor mehran = Professor.builder()
//                .username("mehran")
//                .password(encoder.encode("1234"))
//                .email("mehran773@gmail.com")
//                .firstName("Mehran")
//                .lastName("Khaki")
//                .isEnable(true)
//                .nationalCode("0200374444")
//                .role(Role.PROFESSOR)
//                .build();
//        service.addOrUpdate(mehran);
//
//
//
//    }
//}
