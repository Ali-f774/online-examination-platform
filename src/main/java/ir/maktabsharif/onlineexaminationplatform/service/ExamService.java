package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.model.Exam;

import java.util.List;

public interface ExamService extends CrudService<Exam>{

    List<Exam> findAllByCourseIdAndProfessorId(Long courseId,Long ProfessorId);
    Boolean validProfessor(Long id,Long professorId);
}
