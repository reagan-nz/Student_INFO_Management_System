package com.java.service;

import com.java.common.PageResult;
import com.java.entity.Subject;
import com.java.mapper.SubjectMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectMapper subjectMapper) {
        this.subjectMapper = subjectMapper;
    }

    public PageResult<Subject> list(String subjectName, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Subject> rows = subjectMapper.list(subjectName, offset, pageSize);
        long total = subjectMapper.count(subjectName);
        return new PageResult<>(rows, total);
    }

    public List<Subject> findAll() {
        return subjectMapper.findAll();
    }

    public Subject findById(Long id) {
        return subjectMapper.findById(id);
    }

    public void add(Subject subject) {
        subjectMapper.insert(subject);
    }

    public void update(Subject subject) {
        subjectMapper.update(subject);
    }

    public void deleteById(Long id) {
        subjectMapper.deleteById(id);
    }
}
