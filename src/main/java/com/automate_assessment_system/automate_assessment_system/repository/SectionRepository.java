package com.automate_assessment_system.automate_assessment_system.repository;

import com.automate_assessment_system.automate_assessment_system.model.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface SectionRepository extends JpaRepository<Section, Integer> {
    // This custom method allows us to find a section by its name
    Optional<Section> findBySectionName(String sectionName);
}