package ir.maktabsharif.onlineexaminationplatform.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class DetailsUserDto implements Serializable {
    private String username;
    private String password;
    private List<String> authorities;
    private Boolean isEnable;

    public DetailsUserDto(){}
}
