package com.example.student.controller;

import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import com.example.student.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PostMapping
    public ResponseEntity<StudentResponseDTO> createStudent(@Valid @RequestBody StudentRequestDTO request) {
        StudentResponseDTO createdStudent = studentService.createStudent(request);
        return new ResponseEntity<>(createdStudent, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<StudentResponseDTO>> getAllStudents() {
        List<StudentResponseDTO> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/paginated")
    public ResponseEntity<Page<StudentResponseDTO>> getAllStudentsPaginated(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StudentResponseDTO> students = studentService.getAllStudentsPaginated(pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> getStudentById(@PathVariable Long id) {
        StudentResponseDTO student = studentService.getStudentById(id);
        return ResponseEntity.ok(student);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StudentResponseDTO> updateStudent(@PathVariable Long id,
                                                            @Valid @RequestBody StudentRequestDTO request) {
        StudentResponseDTO updatedStudent = studentService.updateStudent(id, request);
        return ResponseEntity.ok(updatedStudent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        studentService.deleteStudent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/course/{course}")
    public ResponseEntity<List<StudentResponseDTO>> getStudentsByCourse(@PathVariable String course) {
        List<StudentResponseDTO> students = studentService.getStudentsByCourse(course);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/course/{course}/paginated")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsByCoursePaginated(
            @PathVariable String course,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StudentResponseDTO> students = studentService.getStudentsByCoursePaginated(course, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search")
    public ResponseEntity<List<StudentResponseDTO>> searchStudentsByName(@RequestParam String name) {
        List<StudentResponseDTO> students = studentService.searchStudentsByName(name);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/search/paginated")
    public ResponseEntity<Page<StudentResponseDTO>> searchStudentsByNamePaginated(
            @RequestParam String name,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StudentResponseDTO> students = studentService.searchStudentsByNamePaginated(name, pageable);
        return ResponseEntity.ok(students);
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<StudentResponseDTO>> getStudentsWithFilters(
            @RequestParam(required = false) String course,
            @RequestParam(required = false) Integer minMarks,
            @RequestParam(required = false) Integer maxMarks,
            @RequestParam(required = false) String name,
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<StudentResponseDTO> students = studentService.getStudentsWithFilters(course, minMarks, maxMarks, name, pageable);
        return ResponseEntity.ok(students);
    }
}