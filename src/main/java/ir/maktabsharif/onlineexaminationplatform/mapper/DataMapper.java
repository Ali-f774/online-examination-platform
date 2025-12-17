package ir.maktabsharif.onlineexaminationplatform.mapper;

import ir.maktabsharif.onlineexaminationplatform.dto.CourseDto;
import ir.maktabsharif.onlineexaminationplatform.dto.GeneralUserDto;
import ir.maktabsharif.onlineexaminationplatform.dto.RegisterReq;
import ir.maktabsharif.onlineexaminationplatform.dto.UserDto;
import ir.maktabsharif.onlineexaminationplatform.model.Course;
import ir.maktabsharif.onlineexaminationplatform.model.Professor;
import ir.maktabsharif.onlineexaminationplatform.model.Student;
import ir.maktabsharif.onlineexaminationplatform.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public abstract class DataMapper {


    public abstract Professor registerDtoToProfessor(RegisterReq registerReq);

    public abstract Student registerDtoToStudent(RegisterReq registerReq);

    public abstract UserDto UserToDto(User u);

    public abstract CourseDto courseToDto(Course course);

    public abstract GeneralUserDto userToGeneralDto(User user);
}
