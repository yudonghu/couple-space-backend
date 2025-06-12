package com.couplespace.app.repository;

import com.couplespace.app.entity.Anniversary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AnniversaryRepository extends JpaRepository<Anniversary, Long> {

    // 查找所有纪念日，按日期排序
    List<Anniversary> findAllByOrderByAnniversaryDateAsc();

    // 根据类型查找纪念日
    List<Anniversary> findByAnniversaryTypeOrderByAnniversaryDateAsc(String anniversaryType);

    // 根据创建者查找纪念日
    List<Anniversary> findByCreatedByOrderByAnniversaryDateAsc(Long createdBy);

    // 根据日期范围查找纪念日
    List<Anniversary> findByAnniversaryDateBetweenOrderByAnniversaryDateAsc(LocalDate startDate, LocalDate endDate);

    // 查找今天之后的纪念日（未来的）
    List<Anniversary> findByAnniversaryDateAfterOrderByAnniversaryDateAsc(LocalDate date);

    // 查找特定日期的纪念日
    List<Anniversary> findByAnniversaryDate(LocalDate date);
}