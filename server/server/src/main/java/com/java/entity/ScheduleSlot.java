package com.java.entity;

import lombok.Data;
import java.time.LocalTime;

@Data
public class ScheduleSlot {

    private Long id;
    private Long subjectId;
    private Integer dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
    private String semester;

    /** Populated by JOIN */
    private String subjectName;
}
