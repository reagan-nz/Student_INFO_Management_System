package com.java.service;

import com.java.common.PageResult;
import com.java.entity.Student;
import com.java.mapper.StudentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {

    private final StudentMapper studentMapper;

    public StudentService(StudentMapper studentMapper) {
        this.studentMapper = studentMapper;
    }

    public PageResult<Student> list(String name, Long classId, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<Student> rows = studentMapper.list(name, classId, offset, pageSize);
        long total = studentMapper.count(name, classId);
        return new PageResult<>(rows, total);
    }

    public Student findById(Long id) {
        return studentMapper.findById(id);
    }

    public void add(Student student) {
        studentMapper.insert(student);
    }

    public void update(Student student) {
        studentMapper.update(student);
    }

    public void deleteById(Long id) {
        studentMapper.deleteById(id);
    }
}
