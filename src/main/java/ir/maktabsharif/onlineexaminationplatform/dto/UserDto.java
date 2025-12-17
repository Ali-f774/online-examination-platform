package ir.maktabsharif.onlineexaminationplatform.dto;

public record UserDto(
        Long id,
        String username,
        String email,
        String nationalCode,
        String firstName,
        String lastName,
        String role
) {
}
