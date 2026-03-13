package com.java.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class Subject {

    private Long id;
    private String subjectName;
    private BigDecimal credit;
    private String description;
}
