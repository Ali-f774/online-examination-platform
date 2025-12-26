package ir.maktabsharif.onlineexaminationplatform;

import ir.maktabsharif.onlineexaminationplatform.exception.DoubleExamException;
import ir.maktabsharif.onlineexaminationplatform.exception.DoubleUsernameException;
import ir.maktabsharif.onlineexaminationplatform.util.I18NUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    private final I18NUtils i18NUtils;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> badRequest(MethodArgumentNotValidException e){
        String message = "";
        for (ObjectError error : e.getBindingResult().getAllErrors()) {
            if (error instanceof FieldError){
                message += error.getDefaultMessage() + "\n";
            }
        }
        return Map.of("message",message);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> invalidCredentials(BadCredentialsException e){

        return Map.of("message",i18NUtils.getMessage("invalid.credentials"));
    }

    @ExceptionHandler(DisabledException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Map<String, String> disabledAccount(DisabledException e){
        return Map.of("message",i18NUtils.getMessage("disable.account"));
    }
    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> doubleData(DataIntegrityViolationException e){
        String message = "";
        String rootCase = (e.getRootCause() != null) ? e.getRootCause().getMessage() : e.getMessage();
        if (rootCase.contains("users_table_email_key"))
            message = i18NUtils.getMessage("double.email");
        if (rootCase.contains("users_table_national_code_key"))
            message = i18NUtils.getMessage("double.national.code");
        if (rootCase.contains("users_table_username_key"))
            message = i18NUtils.getMessage("double.username");


        return Map.of("message",message);
    }
    @ExceptionHandler(DoubleUsernameException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String,String> doubleUsername(){
        return Map.of("message",i18NUtils.getMessage("double.username"));
    }

    @ExceptionHandler(DoubleExamException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> doubleExam(DoubleExamException e){
        return Map.of("message",i18NUtils.getMessage("double.exam"));
    }
}
