package ir.maktabsharif.onlineexaminationplatform.model;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("Professor")
public class Professor extends User{

    @OneToMany(mappedBy = "professor")
    private Set<Course> courses;
}
