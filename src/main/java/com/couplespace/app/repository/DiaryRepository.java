package com.couplespace.app.repository;

import com.couplespace.app.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {

    // 根据用户ID查找日记
    List<Diary> findByUserIdOrderByCreatedAtDesc(Long userId);

    // 查找所有日记，按时间倒序
    List<Diary> findAllByOrderByCreatedAtDesc();

    // 根据性别查找日记
    List<Diary> findByUserGenderOrderByCreatedAtDesc(String userGender);

    // 根据时间范围查找日记
    List<Diary> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    // 搜索日记标题或内容
    @Query("SELECT d FROM Diary d WHERE d.title LIKE %:keyword% OR d.content LIKE %:keyword% ORDER BY d.createdAt DESC")
    List<Diary> searchByKeyword(String keyword);

    // 统计用户日记数量
    long countByUserId(Long userId);
}