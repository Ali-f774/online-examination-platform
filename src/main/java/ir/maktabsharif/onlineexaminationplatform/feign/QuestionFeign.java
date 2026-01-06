package ir.maktabsharif.onlineexaminationplatform.feign;

import ir.maktabsharif.onlineexaminationplatform.dto.answer.AddAnswerDTO;
import ir.maktabsharif.onlineexaminationplatform.dto.question.AddQuestionDTO;
import ir.maktabsharif.onlineexaminationplatform.dto.question.GeneralQuestionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient("questions-microservice")
public interface QuestionFeign {

    @GetMapping("/api/v1/questions/general")
    List<GeneralQuestionDTO> courseQuestionBank(@RequestParam Long courseId,@RequestParam Long professorId);

    @PostMapping("/api/v1/questions")
    ResponseEntity<String> addQuestion(@RequestBody AddQuestionDTO dto);


    @DeleteMapping("/api/v1/questions")
    String deleteQuestion(@RequestParam String id);


    @GetMapping("/api/v1/questions")
    AddQuestionDTO findById(@RequestParam String id);

    @GetMapping("/api/v1/questions/exam")
    List<AddQuestionDTO> findByExamId(@RequestParam Long examId);

    @GetMapping("/api/v1/questions/not-used")
    List<AddQuestionDTO> findNotUsed(@RequestParam Long courseId,@RequestParam Long professorId);

    @GetMapping("/api/v1/questions/exist")
    Boolean existQuestion(@RequestParam Long courseId,@RequestParam Long professorId,@RequestParam String title);

    @PostMapping("/api/v1/answers")
    String addOrUpdateAnswer(@RequestBody AddAnswerDTO dto);

    @GetMapping("/api/v1/answers/student")
    AddAnswerDTO getStudentAnswer(@RequestParam Long studentId, @RequestParam String questionId);

    @GetMapping("/api/v1/answers/exam")
    List<AddAnswerDTO> getExamAnswers(@RequestParam Long studentId, @RequestParam Long examId);
}
