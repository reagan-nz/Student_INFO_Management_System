package com.java.controller;

import com.java.common.PageResult;
import com.java.common.Result;
import com.java.entity.Student;
import com.java.service.StudentService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping
    public Result<PageResult<Student>> list(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(required = false) Long classId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(studentService.list(name, classId, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public Result<Student> getById(@PathVariable Long id) {
        return Result.success(studentService.findById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Student student) {
        studentService.add(student);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Student student) {
        student.setId(id);
        studentService.update(student);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        studentService.deleteById(id);
        return Result.success();
    }
}
