package com.java.mapper;

import com.java.entity.ClassInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ClassInfoMapper {

    List<ClassInfo> list(@Param("className") String className,
                         @Param("offset") int offset,
                         @Param("pageSize") int pageSize);

    long count(@Param("className") String className);

    List<ClassInfo> findAll();

    ClassInfo findById(@Param("id") Long id);

    void insert(ClassInfo classInfo);

    void update(ClassInfo classInfo);

    void deleteById(@Param("id") Long id);
}
