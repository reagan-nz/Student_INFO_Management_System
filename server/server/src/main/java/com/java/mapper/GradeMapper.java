package com.java.mapper;

import com.java.entity.Grade;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Mapper
public interface GradeMapper {

    List<Grade> list(@Param("studentId") Long studentId,
                     @Param("subjectId") Long subjectId,
                     @Param("semester") String semester,
                     @Param("offset") int offset,
                     @Param("pageSize") int pageSize);

    long count(@Param("studentId") Long studentId,
               @Param("subjectId") Long subjectId,
               @Param("semester") String semester);

    Grade findById(@Param("id") Long id);

    void insert(Grade grade);

    void update(Grade grade);

    void deleteById(@Param("id") Long id);

    /** Weighted GPA for a single student: SUM(score * credit) / SUM(credit) */
    BigDecimal calcGpaByStudentId(@Param("studentId") Long studentId);

    /** Average score across all grades */
    BigDecimal calcOverallAverage();

    /** Count of grades in score ranges for distribution */
    List<Map<String, Object>> scoreDistribution();

    /** Top N students by weighted GPA */
    List<Map<String, Object>> topStudents(@Param("limit") int limit);

    /** Average score per subject */
    List<Map<String, Object>> avgScorePerSubject();

    /** Distinct semesters */
    List<String> findAllSemesters();
}
