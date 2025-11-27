
package com.automate_assessment_system.automate_assessment_system.repository;

import com.automate_assessment_system.automate_assessment_system.model.SchoolClass;
import com.automate_assessment_system.automate_assessment_system.model.User;
import com.automate_assessment_system.automate_assessment_system.model.Subject;
import com.automate_assessment_system.automate_assessment_system.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SchoolClassRepository extends JpaRepository<SchoolClass, Integer> {
	Optional<SchoolClass> findByTeacherAndSubjectAndSectionNameAndSchoolYear(User teacher, Subject subject, String sectionName, String schoolYear);
}
