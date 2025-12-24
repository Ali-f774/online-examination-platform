package ir.maktabsharif.onlineexaminationplatform.repository;

import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExamRepository extends JpaRepository<Exam,Long> {

    List<Exam> findAllByCourse_IdAndProfessor_Id(Long courseId, Long professorId);
    Boolean existsByCourse_IdAndTitleAndProfessor_Id(Long courseId,String title,Long professorId);

    Boolean existsByIdAndProfessor_Id(Long id,Long professorId);
}
