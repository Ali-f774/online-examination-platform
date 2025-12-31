package ir.maktabsharif.onlineexaminationplatform.dto.question;

import ir.maktabsharif.onlineexaminationplatform.model.QuestionType;

public record GeneralQuestionDTO(
        String id,
        String title,
        String question,
        QuestionType type
) {
}
