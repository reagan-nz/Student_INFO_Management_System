package com.java.controller;

import com.java.common.Result;
import com.java.mapper.ClassInfoMapper;
import com.java.mapper.StudentMapper;
import com.java.mapper.SubjectMapper;
import com.java.service.GradeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final StudentMapper studentMapper;
    private final ClassInfoMapper classInfoMapper;
    private final SubjectMapper subjectMapper;
    private final GradeService gradeService;

    public DashboardController(StudentMapper studentMapper, ClassInfoMapper classInfoMapper,
                               SubjectMapper subjectMapper, GradeService gradeService) {
        this.studentMapper = studentMapper;
        this.classInfoMapper = classInfoMapper;
        this.subjectMapper = subjectMapper;
        this.gradeService = gradeService;
    }

    @GetMapping
    public Result<Map<String, Object>> stats() {
        Map<String, Object> data = new HashMap<>();
        data.put("studentCount", studentMapper.count(null, null));
        data.put("classCount", classInfoMapper.count(null));
        data.put("subjectCount", subjectMapper.count(null));
        data.put("overallAverage", gradeService.getOverallAverage());
        data.put("scoreDistribution", gradeService.getScoreDistribution());
        data.put("topStudents", gradeService.getTopStudents(5));
        data.put("avgScorePerSubject", gradeService.getAvgScorePerSubject());
        return Result.success(data);
    }
}
