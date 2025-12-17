package ir.maktabsharif.onlineexaminationplatform.util;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import static org.springframework.context.i18n.LocaleContextHolder.getLocale;
@Component
@RequiredArgsConstructor
public class I18NUtils {

    private final MessageSource messageSource;

    public String getMessage(String key,Object... args){
        return messageSource.getMessage(key,args,getLocale());
    }
    public String getMessage(String key){
        return messageSource.getMessage(key,null,getLocale());
    }

}
