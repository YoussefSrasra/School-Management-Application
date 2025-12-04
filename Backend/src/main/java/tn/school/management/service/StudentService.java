package tn.school.management.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tn.school.management.dto.StudentDto;
import tn.school.management.entity.Level;

public interface StudentService {
    
    StudentDto createStudent(StudentDto studentDto);
    
    StudentDto getStudentById(Long id);
    
    Page<StudentDto> getAllStudents(Pageable pageable);
    
    StudentDto updateStudent(Long id, StudentDto studentDto);
    
    void deleteStudent(Long id);
    
    Page<StudentDto> searchStudents(String username, Pageable pageable);
    
    Page<StudentDto> filterStudentsByLevel(Level level, Pageable pageable);
}