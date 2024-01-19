package com.api.sistema_rc.model;

import jakarta.persistence.*;

@Entity
@Table(name = "client_templates")
public class ClientTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 255,nullable = false)
    private String name;
    @Column(nullable = false)
    private boolean value;
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

    public boolean isValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }
}
