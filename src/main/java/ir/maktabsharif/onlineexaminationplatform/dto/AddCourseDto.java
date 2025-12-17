package ir.maktabsharif.onlineexaminationplatform.dto;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import org.springframework.web.multipart.MultipartFile;

@MultipartConfig(
        fileSizeThreshold = 1024 * 1024,
        maxFileSize = 1024 * 1024 * 10,
        maxRequestSize = 1024 * 1024 * 100
)
public record AddCourseDto(
        String title,

        String startDate,

        String endDate,

        MultipartFile image
) {
}
