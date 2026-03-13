package com.java.mapper;

import com.java.entity.Enrollment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {

    List<Enrollment> listByStudentId(@Param("studentId") Long studentId,
                                     @Param("semester") String semester);

    void insert(Enrollment enrollment);

    void deleteById(@Param("id") Long id);
}
