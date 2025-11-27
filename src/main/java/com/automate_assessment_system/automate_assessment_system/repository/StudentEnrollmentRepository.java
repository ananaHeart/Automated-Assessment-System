
package com.automate_assessment_system.automate_assessment_system.repository;

import com.automate_assessment_system.automate_assessment_system.model.StudentEnrollment;
import com.automate_assessment_system.automate_assessment_system.model.Student;
import com.automate_assessment_system.automate_assessment_system.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentEnrollmentRepository extends JpaRepository<StudentEnrollment, Integer> {
	long countBySection_SectionId(Integer sectionId);

	boolean existsByStudentAndSectionAndSchoolYear(Student student, Section section, String schoolYear);
}
