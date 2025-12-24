package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.CourseDto;
import ir.maktabsharif.onlineexaminationplatform.dto.ExamDto;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.Course;
import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import ir.maktabsharif.onlineexaminationplatform.model.Professor;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final UserService userService;
    private final DataMapper mapper;
    private final ExamService examService;
    private final CourseService courseService;

    @Transactional
    @GetMapping("/courses")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String courses(Principal principal, Model model, Pageable pageable){
        User user = userService.findByUsername(principal.getName());
        List<Course> courses;
        if (user instanceof Professor professor){
            courses = new ArrayList<>(professor.getCourses());
        }else
            throw new AccessDeniedException("Access Denied");

        List<CourseDto> list = courses
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
        return "professor-courses";
    }

    @GetMapping("/course-exams")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String courseExams(@RequestParam Long id,Model model,Principal principal){
        Course course = courseService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !professor.getId().equals(course.getProfessor().getId()))
            throw new AccessDeniedException("Access Denied");
        List<ExamDto> list = examService
                .findAllByCourseIdAndProfessorId(id, professor.getId())
                .stream()
                .map(mapper::examToExamDto)
                .toList();
        model.addAttribute("exams",list);
        model.addAttribute("course",mapper.courseToDto(course));
        return "course-exams";
    }

    @Transactional
    @GetMapping("/add-exam")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String addExam(@RequestParam Long id,Model model,Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !courseService.validProfessor(id, professor.getId()))
            throw new AccessDeniedException("Access Denied");
        model.addAttribute("professorId",professor.getId());
        model.addAttribute("courseId",id);
        return "add-exam";
    }

    @Transactional
    @GetMapping("/edit-exam")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String editExam(@RequestParam Long id,Model model,Principal principal){
        Exam exam = examService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(id,professor.getId()))
            throw new AccessDeniedException("Access Denied");
        model.addAttribute("exam",mapper.examToExamDto(exam));
        model.addAttribute("courseId",exam.getCourse().getId());
        return "edit-exam";

    }

    @Transactional
    @GetMapping("/exam-details")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String examDetails(@RequestParam Long id,Model model,Principal principal){
        //TODO
        return "exam-details";
    }

}
