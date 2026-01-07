package ir.maktabsharif.onlineexaminationplatform.dto.answer;

public record AddAnswerDTO(
        String id,
        Long studentId,
        String answer,
        String questionId,
        Double grade
) {
}
