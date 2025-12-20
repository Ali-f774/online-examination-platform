package ir.maktabsharif.onlineexaminationplatform.repository;

import ir.maktabsharif.onlineexaminationplatform.model.Role;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    List<@NonNull User> findAllByRoleAndIsEnable(Role role, Boolean isEnable);
    Page<@NonNull User> findAllByIsEnableIsFalse(Pageable pageable);

    @Query(value = """
        select * from users_table where type = 'Student' and is_enable and id not in
        (select students_id from course_students where courses_id = :courseId)
        """,nativeQuery = true)
    List<User> findAllStudentsNotRegisteredInCourse(@Param("courseId") Long courseId);
}
