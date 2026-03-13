package com.java.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Grade {

    private Long id;
    private Long studentId;
    private Long subjectId;
    private BigDecimal score;
    private String semester;

    /** Populated by JOIN queries */
    private String studentName;
    private String studentNo;
    private String subjectName;
    private BigDecimal credit;
}
