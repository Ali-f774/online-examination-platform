package ir.maktabsharif.onlineexaminationplatform.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {
        "title",
        "course_id",
        "professor_id"
}))
public class Exam extends BaseModel {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Integer time; //Minutes

    @ManyToOne
    private Course course;

    @ManyToOne
    private Professor professor;

    @ManyToMany
    private Set<Student> students;

}
