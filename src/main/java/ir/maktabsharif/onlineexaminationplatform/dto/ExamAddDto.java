package ir.maktabsharif.onlineexaminationplatform.dto;


import jakarta.validation.constraints.Min;
import org.hibernate.validator.constraints.Length;

public record ExamAddDto(

        @Length(min = 3,max = 20,message = "{invalid.title}")
        String title,
        @Length(min = 3,max = 300,message = "{invalid.description}")
        String description,
        @Min(value = 1,message = "{invalid.time}")
        Integer time,
        Long courseId,
        Long professorId
) {

    public ExamAddDto{
        if (title != null)
            title = title.trim();
        if (description != null)
            description = description.trim();
    }
}
