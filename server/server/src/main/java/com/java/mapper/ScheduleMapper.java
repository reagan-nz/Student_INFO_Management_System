package com.java.mapper;

import com.java.entity.ScheduleSlot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ScheduleMapper {

    List<ScheduleSlot> listSlots(@Param("semester") String semester);

    List<ScheduleSlot> listSlotsByStudentId(@Param("studentId") Long studentId,
                                            @Param("semester") String semester);

    ScheduleSlot findById(@Param("id") Long id);

    void insert(ScheduleSlot slot);

    void update(ScheduleSlot slot);

    void deleteById(@Param("id") Long id);

    List<String> findAllSemesters();
}
