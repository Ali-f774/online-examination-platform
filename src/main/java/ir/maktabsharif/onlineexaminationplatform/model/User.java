package ir.maktabsharif.onlineexaminationplatform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(name = "users_table")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "type")
public class User extends BaseModel {

    @NotBlank
    @Column(unique = true,nullable = false)
    private String username;

    @NotBlank
    @Column(nullable = false)
    private String password;

    @Email(message = "Invalid Email Address")
    @Column(unique = true,nullable = false)
    private String email;

    @Column(name = "is_enable",nullable = false)
    private Boolean isEnable;

    @NotBlank
    @Column(name = "national_code",unique = true,nullable = false)
    private String nationalCode;

    @NotBlank
    @Column(name = "first_name",nullable = false)
    private String firstName;

    @NotBlank
    @Column(name = "last_name",nullable = false)
    private String lastName;

    private String role;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> authorities;


}
