package com.example.d3.cardstack;

import java.io.Serializable;

public class Score implements Serializable {

    private Long id;
    private String name;
    private String time;

    public Score(Long id, String name, String time) {
        this.id = id;
        this.name = name;
        this.time = time;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String ext) {
        this.time = time;
    }
}