package com.automate_assessment_system.automate_assessment_system.repository;

import com.automate_assessment_system.automate_assessment_system.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    // If using 'student' table, this is correct
    Optional<Student> findByFirstNameAndLastName(String firstName, String lastName);
}