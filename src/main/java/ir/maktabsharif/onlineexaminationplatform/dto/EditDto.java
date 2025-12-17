package ir.maktabsharif.onlineexaminationplatform.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record EditDto(

        Long id,


        @Email(message = "{email.invalid}")
        String email,

        @Pattern(regexp = "^\\d{10}$",message = "{national.code.invalid}")
        String nationalCode,

        @NotBlank(message = "{blank.first.name}")
        @Length(min = 3, message = "{length.first.name}")
        String firstName,

        @NotBlank (message = "{blank.last.name}")
        @Length(min = 3,message = "{length.last.name}")
        String lastName,

        @NotBlank
        String role

) {

    public EditDto{
        if (email != null)
            email = email.trim();
        if (nationalCode != null)
            nationalCode = nationalCode.trim();
        if (firstName != null)
            firstName = firstName.trim();
        if (lastName != null)
            lastName = lastName.trim();
        if (role != null)
            role = role.trim().toUpperCase();
    }
}
