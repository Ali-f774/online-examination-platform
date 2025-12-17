package ir.maktabsharif.onlineexaminationplatform.dto;

import org.springframework.web.multipart.MultipartFile;

public record EditCourseDto(
        Long id,

        String title,

        String startDate,

        String endDate,

        MultipartFile image
) {
}
