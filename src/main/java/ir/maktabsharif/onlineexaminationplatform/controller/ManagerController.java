package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.*;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.Course;
import ir.maktabsharif.onlineexaminationplatform.model.Professor;
import ir.maktabsharif.onlineexaminationplatform.model.Student;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.EmailService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.DateUtils;
import ir.maktabsharif.onlineexaminationplatform.util.ImageUtil;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.validation.Valid;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerController {

    private final UserService userService;
    private final DataMapper mapper;
    private final CourseService courseService;
    private final EmailService emailService;

    @GetMapping("/pending-users")
    @PreAuthorize("hasRole('MANAGER')")
    public String pendingUsers(Pageable pageable, Model model){
        SearchDto dto = new SearchDto("", "", "", "");
        List<UserDto> list = userService.findAllBySearch(dto,pageable).get().map(mapper::UserToDto).toList();
        model.addAttribute("dto",dto);
        model.addAttribute("users",list);
        return "pending-users";
    }

    @GetMapping("/all-users")
    @PreAuthorize("hasRole('MANAGER')")
    public String allUsers(Pageable pageable, Model model){
        SearchDto dto = new SearchDto("", "", "", "");
        List<UserDto> list = userService.findAllUsersBySearch(dto,pageable).get().map(mapper::UserToDto).toList();
        model.addAttribute("dto",dto);
        model.addAttribute("users",list);
        return "all-users";
    }
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/search/all")
    public String searchAll(@Valid @ModelAttribute SearchDto dto,Pageable pageable,Model model){
        List<UserDto> list = userService.findAllUsersBySearch(dto,pageable).stream().map(mapper::UserToDto).toList();
        model.addAttribute("dto",dto);
        model.addAttribute("users",list);
        return "all-users";
    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/courses")
    public String courses(Model model){
        List<CourseDto> list = courseService.findAll()
                .stream()
                .map(course -> {
                    CourseDto dto = mapper.courseToDto(course);
                    if ("fa".equals(LocaleContextHolder.getLocale().getLanguage())) {
                        return new CourseDto(
                                dto.id(),
                                dto.title(),
                                DateUtils.convertDateToPersian(dto.startDate()),
                                DateUtils.convertDateToPersian(dto.endDate()),
                                dto.image()
                        );
                    }
                    return dto;
                })
                .toList();

        model.addAttribute("courses",list);
        return "courses";
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/add-course")
    public String addCourses(){
        return "add-course";
    }


    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/add-course")
    public String addCoursesPost(@ModelAttribute AddCourseDto dto){
        Course course = Course.builder()
                .startDate(DateUtils.convertDateToEnglishLocalDate(dto.startDate()))
                .endDate(DateUtils.convertDateToEnglishLocalDate(dto.endDate()))
                .title(dto.title())
                .image(ImageUtil.toImage(dto.image()))
                .build();
        courseService.addOrUpdate(course);
        return "redirect:/manager/courses";
    }

    @Transactional
    @GetMapping("/edit")
    @PreAuthorize("hasRole('MANAGER')")
    public String
    editUser(@RequestParam Long id,Model model){
        model.addAttribute("user",mapper.UserToDto(userService.findById(id)));
        return "edit-user";
    }
    @Transactional
    @GetMapping("/edit-all")
    @PreAuthorize("hasRole('MANAGER')")
    public String editUserAll(@RequestParam Long id,Model model){
        model.addAttribute("user",mapper.UserToDto(userService.findById(id)));
        return "edit-all-user";
    }

    @Transactional
    @GetMapping("/approve")
    @PreAuthorize("hasRole('MANAGER')")
    public String approveUser(@RequestParam Long id){
        User user = userService.findById(id);
        user.setIsEnable(true);
        userService.addOrUpdate(user);
        emailService.sendEmail(user.getEmail(),user.getFirstName()+" "+user.getLastName(),true);
        return "redirect:/manager/pending-users";
    }

    @Transactional
    @GetMapping("/reject")
    @PreAuthorize("hasRole('MANAGER')")
    public String rejectUser(@RequestParam Long id){
        User user = userService.findById(id);
        userService.deleteById(id);
        emailService.sendEmail(user.getEmail(),user.getFirstName()+" "+user.getLastName(),false);
        return "redirect:/manager/pending-users";
    }
    @Transactional
    @GetMapping("/remove")
    @PreAuthorize("hasRole('MANAGER')")
    public String rejectAllUser(@RequestParam Long id){
        userService.deleteById(id);
        return "redirect:/manager/all-users";
    }


    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/search")
    public String search(@Valid @ModelAttribute SearchDto dto,Pageable pageable,Model model){
        List<UserDto> list = userService.findAllBySearch(dto,pageable).stream().map(mapper::UserToDto).toList();
        model.addAttribute("dto",dto);
        model.addAttribute("users",list);
        return "pending-users";
    }

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/course-details")
    public String courseDetails(@RequestParam Long id,Model model){
        Course course = courseService.findById(id);
        CourseDto courseDto = mapper.courseToDto(course);
        List<@NonNull User> professors = userService.findAllActiveProfessors();
        List<@NonNull GeneralUserDto> availableStudents = userService.findAllStudentsNotRegisteredInCourse(course.getId())
                .stream().map(mapper::userToGeneralDto).toList();
        List<@NonNull GeneralUserDto> students = course.getStudents()
                .stream().map(mapper::userToGeneralDto).toList();
        GeneralUserDto dto = mapper.userToGeneralDto(course.getProfessor());
        model.addAttribute("course",courseDto);
        model.addAttribute("professors",professors);
        model.addAttribute("students",students);
        model.addAttribute("availableStudents",availableStudents);
        model.addAttribute("professor",dto);
        return "course-details";
    }


    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/edit-course")
    public String editCourse(@ModelAttribute EditCourseDto dto){
        Course course = courseService.findById(dto.id());
        course.setTitle(dto.title());
        course.setStartDate(DateUtils.convertDateToEnglishLocalDate(dto.startDate()));
        course.setEndDate(DateUtils.convertDateToEnglishLocalDate(dto.endDate()));
        if (!dto.image().isEmpty())
            course.setImage(ImageUtil.toImage(dto.image()));
        courseService.addOrUpdate(course);
        return "redirect:/manager/course-details?id="+course.getId();
    }


    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/course/remove-professor")
    public String removeProfessor(@RequestParam Long id){
        Course course = courseService.findById(id);
        Professor professor = course.getProfessor();
        if (professor == null) throw new IllegalArgumentException("course dont have professor to remove");
        course.setProfessor(null);
        professor.getCourses().remove(course);
        courseService.addOrUpdate(course);
        userService.addOrUpdate(professor);
        return "redirect:/manager/course-details?id="+course.getId();
    }

    public record CourseStudent(Long courseId,Long studentId){}

    @Transactional
    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/course/remove-student")
    public String removeStudent(@ModelAttribute CourseStudent dto){
        Course course = courseService.findById(dto.courseId);
        User user = userService.findById(dto.studentId);
        if (user instanceof Student student){
            course.getStudents().remove(student);
            student.getCourses().remove(course);
            courseService.addOrUpdate(course);
            userService.addOrUpdate(student);
            return "redirect:/manager/course-details?id="+course.getId();
        }
        throw new IllegalArgumentException("this user is not student");
    }

}
