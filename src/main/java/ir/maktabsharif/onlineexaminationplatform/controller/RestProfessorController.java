package ir.maktabsharif.onlineexaminationplatform.controller;

import ir.maktabsharif.onlineexaminationplatform.dto.ExamAddDto;
import ir.maktabsharif.onlineexaminationplatform.dto.ExamDto;
import ir.maktabsharif.onlineexaminationplatform.mapper.DataMapper;
import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import ir.maktabsharif.onlineexaminationplatform.model.Professor;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/professor")
@Validated
public class RestProfessorController {

    private final DataMapper mapper;
    private final ExamService examService;
    private final UserService userService;

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
}
