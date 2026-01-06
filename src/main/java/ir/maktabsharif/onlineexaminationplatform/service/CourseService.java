package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.model.Course;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService extends CrudService<Course>{

    Page<@NonNull Course> findAllCourses(Pageable pageable);
    Boolean validProfessor(Long id,Long professorId);
    Boolean validStudent(Long id,Long studentId);
}
