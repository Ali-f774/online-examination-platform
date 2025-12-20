package ir.maktabsharif.onlineexaminationplatform.dto;

import ir.maktabsharif.onlineexaminationplatform.model.Role;

public record UserDto(
        Long id,
        String username,
        String email,
        String nationalCode,
        String firstName,
        String lastName,
        String role,
        Boolean isEnable
) {
}
