package com.automate_assessment_system.automate_assessment_system.repository;

import com.automate_assessment_system.automate_assessment_system.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Integer> {
    // This custom method allows us to find a subject by its name and grade level
    Optional<Subject> findBySubjectNameAndGradeLevel(String subjectName, int gradeLevel);
}