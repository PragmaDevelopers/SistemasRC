package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pdf_editor_templates")
public class PdfEditorTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 255,nullable = false)
    private String name;
    @Lob
    @Column(nullable = false)
    private String template;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}