package tn.school.management.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.school.management.dto.StudentDto;
import tn.school.management.entity.Level;
import tn.school.management.entity.Student;
import tn.school.management.exception.ResourceNotFoundException;
import tn.school.management.mapper.StudentMapper;
import tn.school.management.repository.StudentRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceImplTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentServiceImpl studentService;

    @Test
    void shouldCreateStudentSuccessfully() {
        StudentDto inputDto = StudentDto.builder().username("TestUser").level(Level.SEPTIEME).build();
        Student studentEntity = Student.builder().id(1L).username("TestUser").level(Level.SEPTIEME).build();

        when(studentRepository.existsByUsername("TestUser")).thenReturn(false);
        when(studentMapper.toEntity(inputDto)).thenReturn(studentEntity);
        when(studentRepository.save(any(Student.class))).thenReturn(studentEntity);
        when(studentMapper.toDto(studentEntity)).thenReturn(inputDto);

        StudentDto result = studentService.createStudent(inputDto);

        assertNotNull(result);
        assertEquals("TestUser", result.getUsername());

        verify(studentRepository).save(any(Student.class));
    }

    @Test
    void shouldThrowErrorWhenUsernameExists() {
        StudentDto inputDto = StudentDto.builder().username("ExistingUser").build();
        when(studentRepository.existsByUsername("ExistingUser")).thenReturn(true);

        assertThrows(IllegalArgumentException.class, () -> {
            studentService.createStudent(inputDto);
        });

        verify(studentRepository, never()).save(any());
    }

    @Test
    void shouldGetStudentById() {
        Long studentId = 1L;
        Student student = Student.builder().id(studentId).username("Ali").level(Level.HUITIEME).build();
        StudentDto expectedDto = StudentDto.builder().username("Ali").level(Level.HUITIEME).build();

        when(studentRepository.findById(studentId)).thenReturn(Optional.of(student));
        when(studentMapper.toDto(student)).thenReturn(expectedDto);

        StudentDto result = studentService.getStudentById(studentId);

        assertEquals("Ali", result.getUsername());
    }

    @Test
    void shouldThrowErrorWhenStudentNotFound() {
        Long invalidId = 99L;
        when(studentRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            studentService.getStudentById(invalidId);
        });
    }
}