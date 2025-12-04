package tn.school.management.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.school.management.dto.StudentDto;
import tn.school.management.entity.Level;
import tn.school.management.service.StudentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import java.util.Collections;
import java.util.Map;
import java.io.InputStream;
import org.springframework.web.multipart.MultipartFile;





@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
@Tag(name = "Student Management", description = "CRUD operations for Students")
@SecurityRequirement(name = "bearerAuth") // Tells Swagger this controller is protected
public class StudentController {

    private final StudentService studentService;

    @PostMapping
    @Operation(summary = "Create a new student")
    public ResponseEntity<StudentDto> createStudent(@Valid @RequestBody StudentDto studentDto) {
        return new ResponseEntity<>(studentService.createStudent(studentDto), HttpStatus.CREATED);
    }

    @GetMapping
    @Operation(summary = "Get all students with pagination")
    public ResponseEntity<Page<StudentDto>> getAllStudents(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(studentService.getAllStudents(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a student by ID")
    public ResponseEntity<StudentDto> getStudentById(@PathVariable Long id) {
        return ResponseEntity.ok(studentService.getStudentById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing student")
    public ResponseEntity<StudentDto> updateStudent(@PathVariable Long id, @Valid @RequestBody StudentDto studentDto) {
        return ResponseEntity.ok(studentService.updateStudent(id, studentDto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a student")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search students by username")
    public ResponseEntity<Page<StudentDto>> searchStudents(
            @RequestParam String username,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(studentService.searchStudents(username, pageable));
    }

    @GetMapping("/filter")
    @Operation(summary = "Filter students by Level")
    public ResponseEntity<Page<StudentDto>> filterStudents(
            @RequestParam Level level,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        return ResponseEntity.ok(studentService.filterStudentsByLevel(level, pageable));
    }


    @GetMapping("/export")
    @Operation(summary = "Export all students to CSV")
    public ResponseEntity<org.springframework.core.io.Resource> exportStudents() {
        String filename = "students.csv";
        InputStreamResource file = new InputStreamResource(studentService.exportStudents());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("application/csv"))
                .body(file);
    }

    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Import students from CSV")
    public ResponseEntity<Map<String, String>> importStudents(@RequestParam("file") MultipartFile file) {
        studentService.importStudents(file);
        return ResponseEntity.ok(Collections.singletonMap("message", "File imported successfully!"));
    }
}