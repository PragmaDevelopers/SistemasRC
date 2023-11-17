package com.api.sistema_rc.model;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Kanban {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(length = 255,nullable = false)
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String toJson(JsonObject obj){
        obj.addProperty("id",getId());
        obj.addProperty("title",getTitle());
        return obj.toString();
    }
}
