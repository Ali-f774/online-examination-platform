package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.answer.AddAnswerDTO;
import ir.maktabsharif.onlineexaminationplatform.feign.QuestionFeign;
import ir.maktabsharif.onlineexaminationplatform.model.Course;
import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import ir.maktabsharif.onlineexaminationplatform.model.Student;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/student")
@Validated
public class RestStudentController {

    private final QuestionFeign answerFeign;
    private final ExamService examService;
    private final CourseService courseService;
    private final UserService userService;


    @GetMapping("/student")
    public ResponseEntity<AddAnswerDTO> getStudentAnswer(@RequestParam Long studentId,@RequestParam String questionId){
        AddAnswerDTO studentAnswer = answerFeign.getStudentAnswer(studentId, questionId);
        return ResponseEntity.ok(Objects.requireNonNullElseGet(studentAnswer, () -> new AddAnswerDTO("", null, "", "")));
    }
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public String addOrUpdateAnswer(@RequestBody AddAnswerDTO dto){
        answerFeign.addOrUpdateAnswer(dto);
        return "Successfully";
    }

    @Transactional
    @PostMapping("/register")
    @PreAuthorize("hasRole('STUDENT')")
    @ResponseStatus(HttpStatus.OK)
    public String registerAnswers(@RequestBody JsonNode node){
        Long id = node.get("id").asLong();
        Long studentId = node.get("studentId").asLong();
        Exam exam = examService.findById(id);
        Course course = exam.getCourse();
        User user = userService.findById(studentId);
        if (!(user instanceof Student student) ||
                !courseService.validStudent(course.getId(),student.getId())
                || exam.getStudents().stream().anyMatch(s -> s.getId().equals(student.getId()))
        )
            throw new AccessDeniedException("Access Denied");
        exam.getStudents().add(student);
        student.getExams().add(exam);
        userService.addOrUpdate(student);
        examService.addOrUpdate(exam);
        return "Successfully";
    }


}
