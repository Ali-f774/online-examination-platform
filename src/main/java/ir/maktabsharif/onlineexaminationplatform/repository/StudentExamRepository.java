package ir.maktabsharif.onlineexaminationplatform.repository;

import ir.maktabsharif.onlineexaminationplatform.model.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentExamRepository extends JpaRepository<StudentExam,Long> {

    StudentExam findByStudentIdAndExamId(Long studentId, Long examId);
}
