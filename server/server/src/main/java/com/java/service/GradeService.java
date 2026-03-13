package com.java.service;

import com.java.common.PageResult;
import com.java.entity.Grade;
import com.java.mapper.GradeMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class GradeService {

    private final GradeMapper gradeMapper;

    public GradeService(GradeMapper gradeMapper) {
        this.gradeMapper = gradeMapper;
    }

    public PageResult<Grade> list(Long studentId, Long subjectId, String semester,
                                  int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Grade> rows = gradeMapper.list(studentId, subjectId, semester, offset, pageSize);
        long total = gradeMapper.count(studentId, subjectId, semester);
        return new PageResult<>(rows, total);
    }

    public Grade findById(Long id) {
        return gradeMapper.findById(id);
    }

    public void add(Grade grade) {
        gradeMapper.insert(grade);
    }

    public void update(Grade grade) {
        gradeMapper.update(grade);
    }

    public void deleteById(Long id) {
        gradeMapper.deleteById(id);
    }

    public BigDecimal getStudentGpa(Long studentId) {
        return gradeMapper.calcGpaByStudentId(studentId);
    }

    public BigDecimal getOverallAverage() {
        return gradeMapper.calcOverallAverage();
    }

    public List<Map<String, Object>> getScoreDistribution() {
        return gradeMapper.scoreDistribution();
    }

    public List<Map<String, Object>> getTopStudents(int limit) {
        return gradeMapper.topStudents(limit);
    }

    public List<Map<String, Object>> getAvgScorePerSubject() {
        return gradeMapper.avgScorePerSubject();
    }

    public List<String> getAllSemesters() {
        return gradeMapper.findAllSemesters();
    }
}
