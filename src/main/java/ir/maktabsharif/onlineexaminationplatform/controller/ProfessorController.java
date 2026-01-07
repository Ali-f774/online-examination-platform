package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.CourseDto;
import ir.maktabsharif.onlineexaminationplatform.dto.ExamDto;
import ir.maktabsharif.onlineexaminationplatform.dto.UserDto;
import ir.maktabsharif.onlineexaminationplatform.dto.answer.AddAnswerDTO;
import ir.maktabsharif.onlineexaminationplatform.dto.question.AddQuestionDTO;
import ir.maktabsharif.onlineexaminationplatform.dto.question.GeneralQuestionDTO;
import ir.maktabsharif.onlineexaminationplatform.feign.QuestionFeign;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.StudentExamService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.DateUtils;
import ir.maktabsharif.onlineexaminationplatform.util.I18NUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Controller
@RequestMapping("/professor")
@RequiredArgsConstructor
public class ProfessorController {

    private final UserService userService;
    private final DataMapper mapper;
    private final ExamService examService;
    private final CourseService courseService;
    private final QuestionFeign questionFeign;
    private final StudentExamService studentExamService;
    private final I18NUtils i18NUtils;

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
    @GetMapping("/course-bank")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String courseQuestionsBank(@RequestParam Long id,Model model,Principal principal){
        Course course = courseService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !professor.getId().equals(course.getProfessor().getId()))
            throw new AccessDeniedException("Access Denied");
        List<GeneralQuestionDTO> list = questionFeign.courseQuestionBank(course.getId(), professor.getId());
        model.addAttribute("questions",list);
        model.addAttribute("course",mapper.courseToDto(course));
        return "course-bank";
    }

    @GetMapping("/exam-add-bank-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String addBankExam(@RequestParam String id,@RequestParam Long examId,Model model,Principal principal){
        Exam exam = examService.findById(examId);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(examId,professor.getId()))
            throw new AccessDeniedException("Access Denied");
        AddQuestionDTO question = questionFeign.findById(id);
        model.addAttribute("professorId",professor.getId());
        model.addAttribute("courseId",exam.getCourse().getId());
        model.addAttribute("examId",exam.getId());
        model.addAttribute("question",question);
        return "exam-add-bank-question";
    }
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
    @GetMapping("/add-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String addQuestion(@RequestParam Long id,Model model,Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !courseService.validProfessor(id, professor.getId()))
            throw new AccessDeniedException("Access Denied");
        model.addAttribute("professorId",professor.getId());
        model.addAttribute("courseId",id);
        return "add-question";
    }
    @GetMapping("/add-exam-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String addExamQuestion(@RequestParam Long id,Model model,Principal principal){
        Exam exam = examService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(id,professor.getId()))
            throw new AccessDeniedException("Access Denied");
        model.addAttribute("professorId",professor.getId());
        model.addAttribute("courseId",exam.getCourse().getId());
        model.addAttribute("examId",exam.getId());
        return "add-exam-question";
    }
    @GetMapping("/exam-edit-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String editExamQuestion(@RequestParam String id,@RequestParam Long examId,Model model,Principal principal){
        Exam exam = examService.findById(examId);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(examId,professor.getId()))
            throw new AccessDeniedException("Access Denied");
        AddQuestionDTO question = questionFeign.findById(id);
        model.addAttribute("professorId",professor.getId());
        model.addAttribute("courseId",exam.getCourse().getId());
        model.addAttribute("examId",exam.getId());
        model.addAttribute("question",question);
        return "edit-exam-question";
    }

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
        Exam exam = examService.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(id,professor.getId()))
            throw new AccessDeniedException("Access Denied");
        List<AddQuestionDTO> questions = questionFeign.findByExamId(id);
        List<AddQuestionDTO> notUsed = questionFeign.findNotUsed(exam.getCourse().getId(), professor.getId());
        Double totalGrade = questions.stream().mapToDouble(AddQuestionDTO::grade).sum();
        Map<Boolean, List<Student>> students = exam.getStudents().stream().collect(Collectors.partitioningBy(s ->
                studentExamService.findByStudentIdAndExamId(s.getId(), exam.getId()).getIsCorrection()
        ));
        List<UserDto> corrections = students.get(true).stream().map(mapper::UserToDto).toList();
        List<UserDto> notCorrections = students.get(false).stream().map(mapper::UserToDto).toList();
        model.addAttribute("questions",questions);
        model.addAttribute("bankQuestions",notUsed);
        model.addAttribute("totalGrade",totalGrade);
        model.addAttribute("course",exam.getCourse());
        model.addAttribute("exam",exam);
        model.addAttribute("notCorrections",notCorrections);
        model.addAttribute("corrections",corrections);
        return "exam-details";
    }


    @GetMapping("/remove-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String removeQuestion(@RequestParam String id,Principal principal){
        AddQuestionDTO byId = questionFeign.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !byId.professorId().equals(professor.getId()))
            throw new AccessDeniedException("Access Denied");
        questionFeign.deleteQuestion(id);
        return "redirect:/professor/course-bank?id="+byId.courseId();
    }

    @GetMapping("/exam-remove-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String removeExamQuestion(@RequestParam String id,Principal principal){
        AddQuestionDTO byId = questionFeign.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !byId.professorId().equals(professor.getId()))
            throw new AccessDeniedException("Access Denied");
        questionFeign.deleteQuestion(id);
        return "redirect:/professor/exam-details?id="+byId.examId();
    }

    @GetMapping("/edit-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String editQuestion(@RequestParam String id,Model model,Principal principal){
        AddQuestionDTO byId = questionFeign.findById(id);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) || !byId.professorId().equals(professor.getId()))
            throw new AccessDeniedException("Access Denied");
        model.addAttribute("dto",byId);
        return "edit-question";
    }

    @GetMapping("/exam-correction")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String examCorrection(@RequestParam Long id,@RequestParam Long examId,Principal principal,Model model){
        Exam exam = examService.findById(examId);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(exam.getId(),professor.getId()))
            throw new AccessDeniedException("Access Denied");
        User studentUser = userService.findById(id);
        if (!(studentUser instanceof Student student))
            throw new IllegalArgumentException("This User Is Not Student");
        List<Pair<AddQuestionDTO,AddAnswerDTO>> examAnswers = new ArrayList<>();
        questionFeign.getExamAnswers(student.getId(), exam.getId()).forEach(a -> {
            AddQuestionDTO question = questionFeign.findById(a.questionId());
            if (QuestionType.ESSAY.equals(question.type())){
                examAnswers.add(Pair.of(question,a));
            }
                });
        model.addAttribute("answers",examAnswers);
        return "exam-correction";
    }

    @GetMapping("/register-grade")
    @PreAuthorize("hasRole('PROFESSOR')")
    public String registerGrade(@RequestParam Long studentId,@RequestParam String questionId,@RequestParam Double grade,Principal principal){
        AddAnswerDTO answer = questionFeign.getStudentAnswer(studentId, questionId);
        AddQuestionDTO question = questionFeign.findById(questionId);
        Exam exam = examService.findById(question.examId());
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !courseService.validProfessor(exam.getCourse().getId(), professor.getId()) ||
                !examService.validProfessor(exam.getId(),professor.getId()))
            throw new AccessDeniedException("Access Denied");
        if (grade < 0 || grade > question.grade())
            return "redirect:/professor/exam-correction?id="+studentId+"&examId="+exam.getId()+"&err";
        questionFeign.addOrUpdateAnswer(new AddAnswerDTO(answer.id(),answer.studentId(), answer.answer(), answer.questionId(),grade));
        return "redirect:/professor/exam-correction?id="+studentId+"&examId="+exam.getId();
    }

}
