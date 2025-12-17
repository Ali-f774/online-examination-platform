package ir.maktabsharif.onlineexaminationplatform.dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record LoginReq(
        @NotBlank(message = "{blank.username}")
        @Length(min = 3,message = "{length.username}")
        String username,
        @NotBlank(message = "{blank.password}")
        @Length(min = 3,message = "{length.password}")
        String password
)
{
    public LoginReq{
        if (username != null)
            username = username.trim();
        if (password != null)
            password = password.trim();
    }
}
