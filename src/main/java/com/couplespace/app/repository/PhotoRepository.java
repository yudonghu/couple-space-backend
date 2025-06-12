package com.couplespace.app.repository;

import com.couplespace.app.entity.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    // 根据用户ID查找照片
    List<Photo> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 查找所有照片，按时间倒序
    List<Photo> findAllByOrderByCreatedAtDesc();

    // 根据性别查找照片
    List<Photo> findByUserGenderOrderByCreatedAtDesc(String userGender);

    // 根据时间范围查找照片
    List<Photo> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    // 根据文件类型查找照片
    List<Photo> findByContentTypeContainingOrderByCreatedAtDesc(String contentType);

    // 统计用户照片数量
    long countByUserId(Long userId);

    // 统计用户照片总大小 - 使用自定义查询
    @Query("SELECT COALESCE(SUM(p.fileSize), 0) FROM Photo p WHERE p.userId = :userId")
    Long getTotalFileSizeByUserId(@Param("userId") Long userId);

    // 根据文件名查找照片
    Photo findByFileName(String fileName);
}