package com.java.controller;

import com.java.common.Result;
import com.java.entity.Enrollment;
import com.java.entity.ScheduleSlot;
import com.java.service.ScheduleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedule")
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public Result<List<ScheduleSlot>> list(@RequestParam(defaultValue = "") String semester) {
        return Result.success(scheduleService.listSlots(semester));
    }

    @GetMapping("/student/{studentId}")
    public Result<List<ScheduleSlot>> byStudent(@PathVariable Long studentId,
                                                 @RequestParam(defaultValue = "") String semester) {
        return Result.success(scheduleService.listSlotsByStudentId(studentId, semester));
    }

    @PostMapping
    public Result<?> add(@RequestBody ScheduleSlot slot) {
        scheduleService.addSlot(slot);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ScheduleSlot slot) {
        slot.setId(id);
        scheduleService.updateSlot(slot);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        scheduleService.deleteSlot(id);
        return Result.success();
    }

    @GetMapping("/semesters")
    public Result<List<String>> semesters() {
        return Result.success(scheduleService.findAllSemesters());
    }

    @GetMapping("/enrollments")
    public Result<List<Enrollment>> enrollments(@RequestParam Long studentId,
                                                 @RequestParam(defaultValue = "") String semester) {
        return Result.success(scheduleService.listEnrollments(studentId, semester));
    }

    @PostMapping("/enrollments")
    public Result<?> enroll(@RequestBody Enrollment enrollment) {
        scheduleService.enroll(enrollment);
        return Result.success();
    }

    @DeleteMapping("/enrollments/{id}")
    public Result<?> drop(@PathVariable Long id) {
        scheduleService.dropEnrollment(id);
        return Result.success();
    }
}
