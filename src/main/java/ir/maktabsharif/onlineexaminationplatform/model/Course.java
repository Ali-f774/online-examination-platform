package ir.maktabsharif.onlineexaminationplatform.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Course extends BaseModel{

    private String title;

    private LocalDate startDate;

    private LocalDate endDate;


    @Lob
    private String image;

    @ManyToOne
    private Professor professor;

    @ManyToMany
    private Set<Student> students;
}
