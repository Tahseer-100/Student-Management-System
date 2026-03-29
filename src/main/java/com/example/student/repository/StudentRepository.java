package com.example.student.repository;

import com.example.student.model.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByEmail(String email);

    List<Student> findByCourse(String course);

    List<Student> findByFirstNameContainingIgnoreCase(String firstName);

    Page<Student> findAll(Pageable pageable);

    Page<Student> findByCourse(String course, Pageable pageable);

    Page<Student> findByFirstNameContainingIgnoreCase(String firstName, Pageable pageable);

    @Query("SELECT s FROM Student s WHERE " +
            "(:course IS NULL OR s.course = :course) AND " +
            "(:minMarks IS NULL OR s.marks >= :minMarks) AND " +
            "(:maxMarks IS NULL OR s.marks <= :maxMarks) AND " +
            "(:name IS NULL OR LOWER(s.firstName) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Student> findWithFilters(@Param("course") String course,
                                  @Param("minMarks") Integer minMarks,
                                  @Param("maxMarks") Integer maxMarks,
                                  @Param("name") String name,
                                  Pageable pageable);
}