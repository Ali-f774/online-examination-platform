package ir.maktabsharif.onlineexaminationplatform.util;
import com.github.mfathi91.time.PersianDate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    private static String toEnglish(String persianString) {

        return persianString
                .replace('۰', '0')
                .replace('۱', '1')
                .replace('۲', '2')
                .replace('۳', '3')
                .replace('۴', '4')
                .replace('۵', '5')
                .replace('۶', '6')
                .replace('۷', '7')
                .replace('۸', '8')
                .replace('۹', '9');
    }
    private static String toPersian(String englishString) {


        return englishString
                .replace('0','۰')
                .replace('1','۱')
                .replace('2','۲')
                .replace('3','۳')
                .replace('4','۴')
                .replace('5','۵')
                .replace('6','۶')
                .replace('7','۷')
                .replace('8','۸')
                .replace('9','۹')
                .replace('-','/');
    }

    public static LocalDate convertDateToEnglish(String jalaliDate){
        if (jalaliDate == null || jalaliDate.trim().isEmpty())
            throw new IllegalArgumentException("Invalid JalaliDate");

        jalaliDate = toEnglish(jalaliDate);

        String[] split = jalaliDate.split("/");
        if (split.length < 3)
            throw new IllegalArgumentException("Invalid JalaliDate");

        int year = Integer.parseInt(split[0]);
        int month = Integer.parseInt(split[1]);
        int day = Integer.parseInt(split[2]);
        PersianDate persianDate = PersianDate.of(year,month,day);

        return persianDate.toGregorian();
    }
    public static String convertDateToPersian(String englishDate){
        if (englishDate == null)
            throw new IllegalArgumentException("Invalid LocalDate for convert");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.parse(englishDate,formatter);
        String persianDate = PersianDate.fromGregorian(localDate).toString();
        return toPersian(persianDate);
    }
    public static LocalDate convertDateToEnglishLocalDate(String englishDate){
        if (englishDate == null)
            throw new IllegalArgumentException("Invalid LocalDate for convert");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        return LocalDate.parse(englishDate,formatter);
    }


}

