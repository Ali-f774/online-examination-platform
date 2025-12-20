package ir.maktabsharif.onlineexaminationplatform.dto;

import ir.maktabsharif.onlineexaminationplatform.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record RegisterReq(
      @NotBlank(message = "{blank.username}")
      @Length(min = 3,message = "{length.username}")
      String username,

      @NotBlank(message = "{blank.password}")
      @Length(min = 3,message = "{length.password}")
      String password,

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


      Role role,

      Boolean isEnable


) {

    public RegisterReq{
        if (username != null)
            username = username.trim();
        if (password != null)
            password = password.trim();
        if (email != null)
            email = email.trim();
        if (nationalCode != null)
            nationalCode = nationalCode.trim();
        if (firstName != null)
            firstName = firstName.trim();
        if (lastName != null)
            lastName = lastName.trim();
    }
}
