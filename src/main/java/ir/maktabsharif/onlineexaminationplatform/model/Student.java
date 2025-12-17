package ir.maktabsharif.onlineexaminationplatform.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("Student")
public class Student extends User{

    @ManyToMany(mappedBy = "students")
    private Set<Course> courses;

}
