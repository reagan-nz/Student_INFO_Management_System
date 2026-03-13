package com.java.entity;

import lombok.Data;

@Data
public class Enrollment {

    private Long id;
    private Long studentId;
    private Long subjectId;
    private String semester;

    /** Populated by JOIN */
    private String studentName;
    private String subjectName;
}
