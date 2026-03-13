package com.java.controller;

import com.java.common.PageResult;
import com.java.common.Result;
import com.java.entity.Grade;
import com.java.service.GradeService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grades")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public Result<PageResult<Grade>> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(defaultValue = "") String semester,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(gradeService.list(studentId, subjectId, semester, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Grade> getById(@PathVariable Long id) {
        return Result.success(gradeService.findById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Grade grade) {
        gradeService.add(grade);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Grade grade) {
        grade.setId(id);
        gradeService.update(grade);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        gradeService.deleteById(id);
        return Result.success();
    }

    @GetMapping("/gpa/{studentId}")
    public Result<BigDecimal> studentGpa(@PathVariable Long studentId) {
        return Result.success(gradeService.getStudentGpa(studentId));
    }

    @GetMapping("/semesters")
    public Result<List<String>> semesters() {
        return Result.success(gradeService.getAllSemesters());
    }
}
