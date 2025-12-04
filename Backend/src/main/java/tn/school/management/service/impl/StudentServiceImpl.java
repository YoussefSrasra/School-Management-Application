package tn.school.management.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.school.management.dto.StudentDto;
import tn.school.management.entity.Level;
import tn.school.management.entity.Student;
import tn.school.management.exception.ResourceNotFoundException;
import tn.school.management.mapper.StudentMapper;
import tn.school.management.repository.StudentRepository;
import tn.school.management.service.StudentService;
import tn.school.management.util.CsvHelper;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final StudentMapper studentMapper;

    @Override
    public StudentDto createStudent(StudentDto studentDto) {
        if (studentRepository.existsByUsername(studentDto.getUsername())) {
            throw new IllegalArgumentException("Username already exists");
        }
        Student student = studentMapper.toEntity(studentDto);
        Student savedStudent = studentRepository.save(student);
        return studentMapper.toDto(savedStudent);
    }

    @Override
    public StudentDto getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return studentMapper.toDto(student);
    }

    @Override
    public Page<StudentDto> getAllStudents(Pageable pageable) {
        return studentRepository.findAll(pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public StudentDto updateStudent(Long id, StudentDto studentDto) {
        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        existingStudent.setUsername(studentDto.getUsername());
        existingStudent.setLevel(studentDto.getLevel());

        Student updatedStudent = studentRepository.save(existingStudent);
        return studentMapper.toDto(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public Page<StudentDto> searchStudents(String username, Pageable pageable) {
        return studentRepository.findByUsernameContaining(username, pageable)
                .map(studentMapper::toDto);
    }

    @Override
    public Page<StudentDto> filterStudentsByLevel(Level level, Pageable pageable) {
        return studentRepository.findByLevel(level, pageable)
                .map(studentMapper::toDto);
    }


    @Override
    public InputStream exportStudents() {
        List<Student> students = studentRepository.findAll();
        List<StudentDto> dtos = students.stream().map(studentMapper::toDto).collect(Collectors.toList());

        return CsvHelper.studentsToCsv(dtos);
    }

    @Override
    public void importStudents(MultipartFile file) {
        if (!CsvHelper.hasCSVFormat(file)) {
            throw new IllegalArgumentException("Please upload a csv file!");
        }
        try {
            List<Student> students = CsvHelper.csvToStudents(file.getInputStream());

            List<Student> newStudents = students.stream()
                    .filter(s -> !studentRepository.existsByUsername(s.getUsername()))
                    .collect(Collectors.toList());

            studentRepository.saveAll(newStudents);
        } catch (IOException e) {
            throw new RuntimeException("Fail to store csv data: " + e.getMessage());
        }
    }
}