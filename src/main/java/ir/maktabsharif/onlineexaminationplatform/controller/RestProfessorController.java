package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.ExamAddDto;
import ir.maktabsharif.onlineexaminationplatform.dto.ExamDto;
import ir.maktabsharif.onlineexaminationplatform.dto.question.AddQuestionDTO;
import ir.maktabsharif.onlineexaminationplatform.feign.QuestionFeign;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.StudentExamService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.I18NUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.JsonNode;

import java.security.Principal;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professor")
@Validated
public class RestProfessorController {

    private final DataMapper mapper;
    private final ExamService examService;
    private final UserService userService;
    private final QuestionFeign feign;
    private final I18NUtils messageUtil;
    private final StudentExamService studentExamService;

    @Transactional
    @PostMapping("/add-exam")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> addExam(@Valid @RequestBody ExamAddDto dto){
        Exam exam = mapper.addDtoToEntity(dto);
        examService.addOrUpdate(exam);
        return ResponseEntity.ok("Successfully Added");
    }

    @Transactional
    @PostMapping("/remove-exam")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> removeExam(@RequestBody JsonNode node, Principal principal){
        long examId = node.get("examId").asLong();
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !examService.validProfessor(examId,professor.getId()))
            throw new AccessDeniedException("Access Denied");

        examService.deleteById(examId);
        return ResponseEntity.ok("Successfully Deleted");
    }

    @Transactional
    @PostMapping("/edit-exam")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> editExam(@Valid @RequestBody ExamDto dto, Principal principal){
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !examService.validProfessor(dto.id(),professor.getId()))
            throw new AccessDeniedException("Access Denied");
        Exam exam = examService.findById(dto.id());
        exam.setTitle(dto.title());
        exam.setDescription(dto.description());
        exam.setTime(dto.time());
        examService.addOrUpdate(exam);
        return ResponseEntity.ok("Successfully Edited");
    }
    @Transactional
    @PostMapping("/add-question")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> addQuestion(@Valid @RequestBody AddQuestionDTO dto){
        if (dto.type() == QuestionType.MULTI_CHOICE) {
            if (dto.choices() == null || dto.choices().isEmpty() || dto.choices().stream().anyMatch(String::isBlank) || dto.choices().size() < 2)
                return ResponseEntity.status(400).body(Map.of("message", messageUtil.getMessage("choices.invalid")));
            if (dto.correctChoice() == null || dto.correctChoice().isEmpty())
                return ResponseEntity.status(400).body(Map.of("message", messageUtil.getMessage("correct.choice.invalid")));
        }
        if (dto.examId() != null && (dto.grade() == null || dto.grade() <= 0))
            return ResponseEntity.status(400).body(Map.of("message", messageUtil.getMessage("grade.invalid")));

        if (dto.save() != null && dto.save()){
            if (feign.existQuestion(dto.courseId(), dto.professorId(), dto.title()))
                return ResponseEntity.status(400).body(Map.of("message", messageUtil.getMessage("double.question.title")));
            AddQuestionDTO bankDto = new AddQuestionDTO(
                    null, dto.title(), dto.question(), dto.type(),dto.choices(),
                    dto.correctChoice(), dto.courseId(), dto.professorId(), null,null,null
            );
            feign.addQuestion(bankDto);
        }

        feign.addQuestion(dto);
        return ResponseEntity.ok("Successfully Added");
    }
    @PostMapping("/finally-register")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> finallyRegister(@RequestBody JsonNode node,Principal principal){
        Long studentId = node.get("studentId").asLong();
        Long examId = node.get("examId").asLong();
        Exam exam = examService.findById(examId);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !examService.validProfessor(exam.getId(),professor.getId()))
            throw new AccessDeniedException("Access Denied");
        StudentExam studentExam = studentExamService.findByStudentIdAndExamId(studentId, examId);
        if(feign.getExamAnswers(studentId,examId).stream().anyMatch(a -> a.grade() == null))
            return ResponseEntity.status(400).body(Map.of("message",messageUtil.getMessage("fill.all.grades")));
        studentExam.setIsCorrection(true);
        studentExamService.addOrUpdate(studentExam);
        return ResponseEntity.ok("Successfully");
    }

    @PostMapping("/change-to-not-corrected")
    @PreAuthorize("hasRole('PROFESSOR')")
    public ResponseEntity<?> changeToNotCorrected(@RequestBody JsonNode node,Principal principal){
        Long studentId = node.get("studentId").asLong();
        Long examId = node.get("examId").asLong();
        Exam exam = examService.findById(examId);
        User user = userService.findByUsername(principal.getName());
        if (!(user instanceof Professor professor) ||
                !examService.validProfessor(exam.getId(),professor.getId()))
            throw new AccessDeniedException("Access Denied");
        StudentExam studentExam = studentExamService.findByStudentIdAndExamId(studentId, examId);
        if(feign.getExamAnswers(studentId,examId).stream().anyMatch(a -> a.grade() == null))
            return ResponseEntity.status(400).body(Map.of("message",messageUtil.getMessage("fill.all.grades")));
        studentExam.setIsCorrection(false);
        studentExamService.addOrUpdate(studentExam);
        return ResponseEntity.ok("Successfully");
    }

}
