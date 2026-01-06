package ir.maktabsharif.onlineexaminationplatform.service.impl;

import ir.maktabsharif.onlineexaminationplatform.model.StudentExam;
import ir.maktabsharif.onlineexaminationplatform.repository.StudentExamRepository;
import ir.maktabsharif.onlineexaminationplatform.service.StudentExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StudentExamServiceImpl implements StudentExamService {

    private final StudentExamRepository repository;

    @Override
    public StudentExam addOrUpdate(StudentExam studentExam) {
        return repository.save(studentExam);
    }

    @Override
    public StudentExam findByStudentIdAndExamId(Long studentId, Long examId) {
        return repository.findByStudentIdAndExamId(studentId, examId);
    }
}
