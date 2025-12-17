package ir.maktabsharif.onlineexaminationplatform.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendEmail(String toEmail,String name,boolean isApproved){

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message);
            helper.setFrom("alifaraji7747@gmail.com","OEP");
            helper.setTo(toEmail);
            helper.setSubject("Welcome ".concat(name));
            String body = """
                    <h1>Hi %s</h1>
                    <h1>Welcome To Online Examination Website</h1>
                    """.formatted(name);
            body += (isApproved)
                    ? "<h2 style=\"color: green\">Your Registration Request Is Approved By Manager And Your Account Is Activated Now</h2>"
                    : "<h2 style=\"color: red\">Your Registration Request Is Rejected By Manager</h2>";
            body += """
                    <hr>
                    <h1>سلام %s</h1>
                    <h1>به سایت برگزاری امتحانات آنلاین خوش آمدی</h1>
                    """.formatted(name);
            body += (isApproved)
                    ? "<h2 style=\"color: green\">درخواست ثبت نام شما توسط مدیر تائید شد و اکنون حساب کاربری شما فعال است</h2>"
                    : "<h2 style=\"color: red\">درخواست ثبت نام شما توسط مدیر رد شد</h2>";
            helper.setText(body,true);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
