package ir.maktabsharif.onlineexaminationplatform.repository;

import ir.maktabsharif.onlineexaminationplatform.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course,Long> {

    Boolean existsByIdAndProfessor_Id(Long id,Long professorId);

    Boolean existsByIdAndStudents_Id(Long id, Long studentsId);

}
