package ir.maktabsharif.onlineexaminationplatform.dto.question;

import ir.maktabsharif.onlineexaminationplatform.model.QuestionType;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.List;

public record AddQuestionDTO(
    String id,
    @NotBlank
    @Length(min = 3,max = 10,message = "{length.title.question}")
    String title,
    @NotBlank
    @Length(min = 12,message = "{length.question.question}")
    String question,
    QuestionType type,
    List<String> choices,
    String correctChoice,
    Long courseId,
    Long professorId,
    Double grade,
    Long examId,
    Boolean save
) {
}
