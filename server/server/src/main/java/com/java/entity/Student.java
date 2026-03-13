package com.java.entity;

import lombok.Data;

@Data
public class Student {

    private Long id;
    private String studentNo;
    private String name;
    private String gender;
    private Integer age;
    private String phone;
    private Long classId;
    private String address;

    /** Populated by JOIN query, not a DB column */
    private String className;
}
