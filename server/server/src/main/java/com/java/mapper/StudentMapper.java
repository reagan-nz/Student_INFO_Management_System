package com.java.mapper;

import com.java.entity.Student;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentMapper {

    List<Student> list(@Param("name") String name,
                       @Param("classId") Long classId,
                       @Param("offset") int offset,
                       @Param("pageSize") int pageSize);

    long count(@Param("name") String name,
               @Param("classId") Long classId);

    Student findById(@Param("id") Long id);

    void insert(Student student);

    void update(Student student);

    void deleteById(@Param("id") Long id);
}
