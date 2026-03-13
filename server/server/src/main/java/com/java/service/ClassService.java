package com.java.service;

import com.java.common.PageResult;
import com.java.entity.ClassInfo;
import com.java.mapper.ClassInfoMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClassService {

    private final ClassInfoMapper classInfoMapper;

    public ClassService(ClassInfoMapper classInfoMapper) {
        this.classInfoMapper = classInfoMapper;
    }

    public PageResult<ClassInfo> list(String className, int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<ClassInfo> rows = classInfoMapper.list(className, offset, pageSize);
        long total = classInfoMapper.count(className);
        return new PageResult<>(rows, total);
    }

    public List<ClassInfo> findAll() {
        return classInfoMapper.findAll();
    }

    public ClassInfo findById(Long id) {
        return classInfoMapper.findById(id);
    }

    public void add(ClassInfo classInfo) {
        classInfoMapper.insert(classInfo);
    }

    public void update(ClassInfo classInfo) {
        classInfoMapper.update(classInfo);
    }

    public void deleteById(Long id) {
        classInfoMapper.deleteById(id);
    }
}
