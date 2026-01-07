package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.CourseDto;
import ir.maktabsharif.onlineexaminationplatform.dto.ExamDto;
import ir.maktabsharif.onlineexaminationplatform.dto.question.AddQuestionDTO;
import ir.maktabsharif.onlineexaminationplatform.feign.QuestionFeign;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.StudentExamService;
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
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentController {

    private final UserService userService;
    private final DataMapper mapper;
    private final ExamService examService;
    private final CourseService courseService;
    private final QuestionFeign questionFeign;
    private final StudentExamService studentExamService;

    @Transactional
    @GetMapping("/courses")
    @PreAuthorize("hasRole('STUDENT')")
    public String courses(Principal principal, Model model, Pageable pageable){
        User user = userService.findByUsername(principal.getName());
        List<Course> courses;
        if (user instanceof Student student){
            courses = new ArrayList<>(student.getCourses());
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
        return "student-courses";
    }

    @GetMapping("/course-exams")
    @PreAuthorize("hasRole('STUDENT')")
    public String courseExams(@RequestParam Long id, Model model, Principal principal){
        Course course = courseService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Student student) || !courseService.validStudent(course.getId(),student.getId()))
            throw new AccessDeniedException("Access Denied");
        List<ExamDto> list = examService
                .findAllOpenStudentExams(course.getId(),student.getId())
                .stream()
                .map(mapper::examToExamDto)
                .toList();
        model.addAttribute("exams",list);
        model.addAttribute("course",mapper.courseToDto(course));
        return "student-course-exams";
    }

    @GetMapping("/exam-start")
    @PreAuthorize("hasRole('STUDENT')")
    public String startExam(@RequestParam Long id, @RequestParam(defaultValue = "1") int number, Model model, Principal principal){
        Exam exam = examService.findById(id);
        Course course = exam.getCourse();
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Student student) ||
                !courseService.validStudent(course.getId(),student.getId())
                || exam.getStudents().stream().anyMatch(s -> s.getId().equals(student.getId()))
        )
            throw new AccessDeniedException("Access Denied");
        List<AddQuestionDTO> list = questionFeign.findByExamId(exam.getId());
        if (list.isEmpty() || number < 1 || number > list.size())
            throw new IllegalArgumentException("Invalid Question Number");
        AddQuestionDTO question = list.get(number-1);
        Long endTime;
        StudentExam studentExam = studentExamService.findByStudentIdAndExamId(student.getId(), exam.getId());
        if (studentExam == null){
            Instant now = Instant.now();
            endTime = now.plusSeconds(exam.getTime()*60).toEpochMilli();
            studentExamService.addOrUpdate(new StudentExam(student.getId(), exam.getId(),endTime,0.0,false));
        }else {
            endTime = studentExam.getEndTime();
        }
        model.addAttribute("question",question);
        model.addAttribute("questionNumber",number);
        model.addAttribute("endTime",endTime);
        model.addAttribute("questionsSize",list.size());
        model.addAttribute("studentId",student.getId());
        return "exam-start";
    }

}
