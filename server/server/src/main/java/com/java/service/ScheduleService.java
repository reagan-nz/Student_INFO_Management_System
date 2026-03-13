package com.java.service;

import com.java.entity.Enrollment;
import com.java.entity.ScheduleSlot;
import com.java.mapper.EnrollmentMapper;
import com.java.mapper.ScheduleMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleMapper scheduleMapper;
    private final EnrollmentMapper enrollmentMapper;

    public ScheduleService(ScheduleMapper scheduleMapper, EnrollmentMapper enrollmentMapper) {
        this.scheduleMapper = scheduleMapper;
        this.enrollmentMapper = enrollmentMapper;
    }

    public List<ScheduleSlot> listSlots(String semester) {
        return scheduleMapper.listSlots(semester);
    }

    public List<ScheduleSlot> listSlotsByStudentId(Long studentId, String semester) {
        return scheduleMapper.listSlotsByStudentId(studentId, semester);
    }

    public void addSlot(ScheduleSlot slot) {
        scheduleMapper.insert(slot);
    }

    public void updateSlot(ScheduleSlot slot) {
        scheduleMapper.update(slot);
    }

    public void deleteSlot(Long id) {
        scheduleMapper.deleteById(id);
    }

    public List<String> findAllSemesters() {
        return scheduleMapper.findAllSemesters();
    }

    public List<Enrollment> listEnrollments(Long studentId, String semester) {
        return enrollmentMapper.listByStudentId(studentId, semester);
    }

    public void enroll(Enrollment enrollment) {
        enrollmentMapper.insert(enrollment);
    }

    public void dropEnrollment(Long id) {
        enrollmentMapper.deleteById(id);
    }
}
