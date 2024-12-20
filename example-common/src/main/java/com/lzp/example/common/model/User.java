package com.lzp.example.common.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class User implements Serializable {

    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
