package com.example.student.service;

import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface StudentService {

    StudentResponseDTO createStudent(StudentRequestDTO request);

    List<StudentResponseDTO> getAllStudents();

    Page<StudentResponseDTO> getAllStudentsPaginated(Pageable pageable);

    StudentResponseDTO getStudentById(Long id);

    StudentResponseDTO updateStudent(Long id, StudentRequestDTO request);

    void deleteStudent(Long id);

    List<StudentResponseDTO> getStudentsByCourse(String course);

    Page<StudentResponseDTO> getStudentsByCoursePaginated(String course, Pageable pageable);

    List<StudentResponseDTO> searchStudentsByName(String name);

    Page<StudentResponseDTO> searchStudentsByNamePaginated(String name, Pageable pageable);

    Page<StudentResponseDTO> getStudentsWithFilters(String course, Integer minMarks, Integer maxMarks, String name, Pageable pageable);
}