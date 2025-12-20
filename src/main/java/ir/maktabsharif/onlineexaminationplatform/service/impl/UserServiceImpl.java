package ir.maktabsharif.onlineexaminationplatform.service.impl;

import ir.maktabsharif.onlineexaminationplatform.dto.EditDto;
import ir.maktabsharif.onlineexaminationplatform.dto.SearchDto;
import ir.maktabsharif.onlineexaminationplatform.model.Professor;
import ir.maktabsharif.onlineexaminationplatform.model.Role;
import ir.maktabsharif.onlineexaminationplatform.model.Student;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import ir.maktabsharif.onlineexaminationplatform.repository.UserRepository;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import ir.maktabsharif.onlineexaminationplatform.util.UserSpecification;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public User findByUsername(@NotBlank String username) {
        return repository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("Username Not Found"));
    }

    @Override
    public User addOrUpdate(@NotNull User user) {
        return repository.save(user);
    }

    @Override
    public User findById(@Min(1L) Long id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("User with This Id Not Found"));
    }

    @Override
    public List<User> findAll() {
        return repository.findAll();
    }

    @Override
    public void deleteById(@Min(1L) Long id) {
        repository.delete(findById(id));
    }

    @Override
    public List<@NonNull User> findAllActiveProfessors() {
        return repository.findAllByRoleAndIsEnable(Role.PROFESSOR,true);
    }

    @Override
    public List<@NonNull User> findAllActiveStudents() {
        return repository.findAllByRoleAndIsEnable(Role.STUDENT,true);
    }

    @Override
    public Page<@NonNull User> findAllPendingUsers(@NotNull Pageable pageable) {
        return repository.findAllByIsEnableIsFalse(pageable);
    }

    @Override
    public User update(User user, EditDto dto) {
        User oldUser = findById(dto.id());
        if (user instanceof Professor && dto.role().equals(Role.STUDENT)){
            repository.delete(user);
            Student student = Student.builder()
                    .username(oldUser.getUsername()).
                    password(oldUser.getPassword()).
                    email(dto.email()).
                    isEnable(false).
                    nationalCode(dto.nationalCode()).
                    firstName(dto.firstName()).
                    lastName(dto.lastName()).
                    role(Role.STUDENT)
                    .build();
            return addOrUpdate(student);
        }
        if (user instanceof Student && dto.role().equals(Role.PROFESSOR)){
            repository.delete(user);
            Professor professor = Professor.builder()
                    .username(oldUser.getUsername()).
                    password(oldUser.getPassword()).
                    email(dto.email()).
                    isEnable(false).
                    nationalCode(dto.nationalCode()).
                    firstName(dto.firstName()).
                    lastName(dto.lastName()).
                    role(Role.PROFESSOR)
                    .build();
            return addOrUpdate(professor);
        }
        oldUser.setEmail(dto.email());
        oldUser.setNationalCode(dto.nationalCode());
        oldUser.setFirstName(dto.firstName());
        oldUser.setLastName(dto.lastName());
        return addOrUpdate(oldUser);
    }

    @Override
    public Page<@NonNull User> findAllBySearch(SearchDto dto, Pageable pageable) {
        return repository.findAll(UserSpecification.searchUsers(dto),pageable);
    }

    @Override
    public Page<@NonNull User> findAllUsersBySearch(SearchDto dto, Pageable pageable) {
        return repository.findAll(UserSpecification.searchAllUsers(dto),pageable);
    }

    @Override
    public List<User> findAllStudentsNotRegisteredInCourse(@Min(1) Long courseId) {
        return repository.findAllStudentsNotRegisteredInCourse(courseId);
    }
}
