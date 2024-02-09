package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.PdfEditorTemplate;
import com.api.sistema_rc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfEditorTemplateRepository extends JpaRepository<PdfEditorTemplate,Integer> {
    @Query(value = "SELECT * FROM pdf_editor_templates WHERE name ILIKE :name%",nativeQuery = true)
    List<PdfEditorTemplate> findAllByName(@Param("name") String name);
}
