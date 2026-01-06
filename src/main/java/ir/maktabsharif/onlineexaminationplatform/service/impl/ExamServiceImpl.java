package ir.maktabsharif.onlineexaminationplatform.service.impl;

import ir.maktabsharif.onlineexaminationplatform.exception.DoubleExamException;
import ir.maktabsharif.onlineexaminationplatform.model.Exam;
import ir.maktabsharif.onlineexaminationplatform.repository.ExamRepository;
import ir.maktabsharif.onlineexaminationplatform.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepository repository;

    @Transactional(propagation = Propagation.MANDATORY)
    @Override
    public Exam addOrUpdate(Exam exam) {
        if (exam.getId() == null && exam.getCourse() != null && repository.existsByCourse_IdAndTitleAndProfessor_Id(
                exam.getCourse().getId(),
                exam.getTitle(),
                exam.getProfessor().getId()
        ))
            throw new DoubleExamException();
        return repository.save(exam);
    }

    @Override
    public Exam findById(Long id) {
        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Exam> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    @Override
    public List<Exam> findAllByCourseIdAndProfessorId(Long courseId, Long ProfessorId) {
        return repository.findAllByCourse_IdAndProfessor_Id(courseId, ProfessorId);
    }

    @Override
    public Boolean validProfessor(Long id, Long professorId) {
        return repository.existsByIdAndProfessor_Id(id,professorId);
    }

    @Override
    public List<Exam> findAllOpenStudentExams(Long courseId, Long studentId) {
        return repository.findAllOpenStudentCourses(courseId, studentId);
    }
}
