package com.java.controller;

import com.java.common.PageResult;
import com.java.common.Result;
import com.java.entity.ClassInfo;
import com.java.service.ClassService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/classes")
public class ClassController {

    private final ClassService classService;

    public ClassController(ClassService classService) {
        this.classService = classService;
    }

    @GetMapping
    public Result<PageResult<ClassInfo>> list(
            @RequestParam(defaultValue = "") String className,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return Result.success(classService.list(className, pageNum, pageSize));
    }

    @GetMapping("/all")
    public Result<List<ClassInfo>> all() {
        return Result.success(classService.findAll());
    }

    @GetMapping("/{id}")
    public Result<ClassInfo> getById(@PathVariable Long id) {
        return Result.success(classService.findById(id));
    }

    @PostMapping
    public Result<?> add(@RequestBody ClassInfo classInfo) {
        classService.add(classInfo);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ClassInfo classInfo) {
        classInfo.setId(id);
        classService.update(classInfo);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        classService.deleteById(id);
        return Result.success();
    }
}
