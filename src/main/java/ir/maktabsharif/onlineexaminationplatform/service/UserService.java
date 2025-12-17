package ir.maktabsharif.onlineexaminationplatform.service;

import ir.maktabsharif.onlineexaminationplatform.dto.EditDto;
import ir.maktabsharif.onlineexaminationplatform.dto.SearchDto;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService extends CrudService<User>{

    User findByUsername(String username);

    List<@NonNull User> findAllActiveProfessors();

    List<@NonNull User> findAllActiveStudents();

    Page<@NonNull User> findAllPendingUsers(Pageable pageable);

    User update(User user,EditDto dto);
    Page<@NonNull User> findAllBySearch(SearchDto dto,Pageable pageable);

    List<User> findAllStudentsNotRegisteredInCourse(Long courseId);

}
