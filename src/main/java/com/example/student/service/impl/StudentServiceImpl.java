package com.example.student.service.impl;

import com.example.student.dto.StudentRequestDTO;
import com.example.student.dto.StudentResponseDTO;
import com.example.student.model.Student;
import com.example.student.exception.DuplicateEmailException;
import com.example.student.exception.ResourceNotFoundException;
import com.example.student.repository.StudentRepository;
import com.example.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentResponseDTO createStudent(StudentRequestDTO request) {
        if (studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Student with email " + request.getEmail() + " already exists");
        }

        Student student = convertToEntity(request);
        Student savedStudent = studentRepository.save(student);
        return convertToResponseDTO(savedStudent);
    }

    @Override
    public List<StudentResponseDTO> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> getAllStudentsPaginated(Pageable pageable) {
        return studentRepository.findAll(pageable).map(this::convertToResponseDTO);
    }

    @Override
    public StudentResponseDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));
        return convertToResponseDTO(student);
    }

    @Override
    public StudentResponseDTO updateStudent(Long id, StudentRequestDTO request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        if (!student.getEmail().equals(request.getEmail()) &&
                studentRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException("Student with email " + request.getEmail() + " already exists");
        }

        updateEntityFromDTO(student, request);
        Student updatedStudent = studentRepository.save(student);
        return convertToResponseDTO(updatedStudent);
    }

    @Override
    public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResourceNotFoundException("Student not found with id: " + id);
        }
        studentRepository.deleteById(id);
    }

    @Override
    public List<StudentResponseDTO> getStudentsByCourse(String course) {
        return studentRepository.findByCourse(course).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> getStudentsByCoursePaginated(String course, Pageable pageable) {
        return studentRepository.findByCourse(course, pageable).map(this::convertToResponseDTO);
    }

    @Override
    public List<StudentResponseDTO> searchStudentsByName(String name) {
        return studentRepository.findByFirstNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<StudentResponseDTO> searchStudentsByNamePaginated(String name, Pageable pageable) {
        return studentRepository.findByFirstNameContainingIgnoreCase(name, pageable).map(this::convertToResponseDTO);
    }

    @Override
    public Page<StudentResponseDTO> getStudentsWithFilters(String course, Integer minMarks, Integer maxMarks, String name, Pageable pageable) {
        return studentRepository.findWithFilters(course, minMarks, maxMarks, name, pageable).map(this::convertToResponseDTO);
    }

    private Student convertToEntity(StudentRequestDTO request) {
        Student student = new Student();
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        student.setMarks(request.getMarks());
        return student;
    }

    private StudentResponseDTO convertToResponseDTO(Student student) {
        StudentResponseDTO response = new StudentResponseDTO();
        response.setId(student.getId());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setCourse(student.getCourse());
        response.setMarks(student.getMarks());
        response.setCreatedAt(student.getCreatedAt());
        return response;
    }

    private void updateEntityFromDTO(Student student, StudentRequestDTO request) {
        student.setFirstName(request.getFirstName());
        student.setLastName(request.getLastName());
        student.setEmail(request.getEmail());
        student.setCourse(request.getCourse());
        student.setMarks(request.getMarks());
    }
}