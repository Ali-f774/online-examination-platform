package ir.maktabsharif.onlineexaminationplatform.util;

import jakarta.servlet.http.Part;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class ImageUtil {



    public static String toImage(MultipartFile filepart){
        String base64Image = null;
        if (filepart != null && filepart.getSize() > 0) {
            try (
                    InputStream is = filepart.getInputStream();
            ) {
                byte[] imageBytes = is.readAllBytes();
                base64Image = Base64.getEncoder().encodeToString(imageBytes);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return base64Image;
    }


}
