package com.api.sistema_rc.repository;

import com.api.sistema_rc.model.PdfTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PdfTemplateRepository extends JpaRepository<PdfTemplate,Integer> {
    @Query(value = "SELECT * FROM pdf_templates LIMIT 10 OFFSET :page",nativeQuery = true)
    List<PdfTemplate> findAll(@Param("page") Integer page);
    @Query(value = "SELECT * FROM pdf_templates WHERE name ILIKE :name% LIMIT 10 OFFSET :page",nativeQuery = true)
    List<PdfTemplate> findAllByName(@Param("name") String name,@Param("page") Integer page);
}
