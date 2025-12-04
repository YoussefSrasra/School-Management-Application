package tn.school.management.mapper;

    import org.mapstruct.Mapper;
    import tn.school.management.dto.StudentDto;
    import tn.school.management.entity.Student;

    @Mapper(componentModel = "spring")
    public interface StudentMapper {

        Student toEntity(StudentDto studentDto);

        StudentDto toDto(Student student);
    }