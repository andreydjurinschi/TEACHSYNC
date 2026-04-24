package com.teachsync.repository;

import com.teachsync.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecializationsRepository extends JpaRepository<Category, Long> {

    @Modifying
    @Query(nativeQuery = true, value = "insert into teacher_specializations (teacher_id, category_id)" +
            "values (:teacher_id, :category_id)")
    void addSpecializationForUser(@Param("teacher_id") Long teacher_id, @Param("category_id") Long category_id);

    @Modifying
    @Query(nativeQuery = true, value = "delete from teacher_specializations " +
            "where teacher_id = :teacher_id and category_id = :category_id")
    void removeSpecializationForUser(@Param("teacher_id") Long teacher_id, @Param("category_id") Long category_id);
}
