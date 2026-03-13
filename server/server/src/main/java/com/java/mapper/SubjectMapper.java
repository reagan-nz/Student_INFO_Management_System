package com.java.mapper;

import com.java.entity.Subject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SubjectMapper {

    List<Subject> list(@Param("subjectName") String subjectName,
                       @Param("offset") int offset,
                       @Param("pageSize") int pageSize);

    long count(@Param("subjectName") String subjectName);

    List<Subject> findAll();

    Subject findById(@Param("id") Long id);

    void insert(Subject subject);

    void update(Subject subject);

    void deleteById(@Param("id") Long id);
}
