package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.model.StudentExam;

public interface StudentExamService {

    StudentExam addOrUpdate(StudentExam studentExam);

    StudentExam findByStudentIdAndExamId(Long studentId,Long examId);

}
