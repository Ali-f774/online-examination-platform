package ir.maktabsharif.onlineexaminationplatform.service.impl;

import ir.maktabsharif.onlineexaminationplatform.model.Course;
import ir.maktabsharif.onlineexaminationplatform.repository.CourseRepository;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseRepository repository;

    @Override
    public Course addOrUpdate(@NotNull Course course) {
        return repository.save(course);
    }

    @Override
    public Course findById(@Min(1) Long id) {
        return repository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Course> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(@Min(1) Long id) {
        repository.delete(findById(id));
    }

    @Override
    public Page<@NonNull Course> findAllCourses(@NotNull Pageable pageable) {
        return repository.findAll(pageable);
    }
}
