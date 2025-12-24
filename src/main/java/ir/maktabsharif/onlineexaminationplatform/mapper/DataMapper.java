package ir.maktabsharif.onlineexaminationplatform.mapper;

import ir.maktabsharif.onlineexaminationplatform.dto.*;
import ir.maktabsharif.onlineexaminationplatform.model.*;
import ir.maktabsharif.onlineexaminationplatform.service.CourseService;
import ir.maktabsharif.onlineexaminationplatform.service.UserService;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class DataMapper {

    @Autowired
    protected CourseService courseService;

    @Autowired
    protected UserService userService;

    public abstract Professor registerDtoToProfessor(RegisterReq registerReq);

    public abstract Student registerDtoToStudent(RegisterReq registerReq);

    public abstract UserDto UserToDto(User user);

    public abstract CourseDto courseToDto(Course course);

    public abstract GeneralUserDto userToGeneralDto(User user);

    public abstract ExamDto examToExamDto(Exam exam);

    @Mapping(target = "course" , expression = "java(courseService.findById(dto.courseId()))")
    @Mapping(target = "professor" , expression = "java(((Professor) userService.findById(dto.professorId())))")
    public abstract Exam addDtoToEntity(ExamAddDto dto);


}
