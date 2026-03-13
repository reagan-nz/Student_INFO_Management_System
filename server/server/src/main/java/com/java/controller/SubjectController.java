package com.java.controller;

import com.java.common.PageResult;
import com.java.common.Result;
import com.java.entity.Subject;
import com.java.service.SubjectService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public Result<PageResult<Subject>> list(
            @RequestParam(defaultValue = "") String subjectName,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(subjectService.list(subjectName, pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<Subject>> all() {
        return Result.success(subjectService.findAll());
    }

    @GetMapping("/{id}")
    public Result<Subject> getById(@PathVariable Long id) {
        return Result.success(subjectService.findById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody Subject subject) {
        subjectService.add(subject);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Subject subject) {
        subject.setId(id);
        subjectService.update(subject);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        subjectService.deleteById(id);
        return Result.success();
    }
}
