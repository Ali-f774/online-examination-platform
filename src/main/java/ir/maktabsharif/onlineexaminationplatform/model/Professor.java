package ir.maktabsharif.onlineexaminationplatform.model;


import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@DiscriminatorValue("Professor")
public class Professor extends User {

    @OneToMany(mappedBy = "professor")
    private Set<Course> courses;

    @OneToMany(mappedBy = "professor",cascade = CascadeType.REMOVE)
    private Set<Exam> exams;

}
