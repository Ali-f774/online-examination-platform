package ir.maktabsharif.onlineexaminationplatform.repository;

import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam,Long> {

    List<Exam> findAllByCourse_IdAndProfessor_Id(Long courseId, Long professorId);

    Boolean existsByCourse_IdAndTitleAndProfessor_Id(Long courseId,String title,Long professorId);

    Boolean existsByIdAndProfessor_Id(Long id,Long professorId);

    @Query("select e from Exam e where e.course.id = :courseId and :studentId not in (select s.id from e.students s)")
    List<Exam> findAllOpenStudentCourses(@Param("courseId") Long courseId,@Param("studentId") Long studentId);
}
