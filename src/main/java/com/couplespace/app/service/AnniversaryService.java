package com.couplespace.app.service;

import com.couplespace.app.entity.Anniversary;
import com.couplespace.app.repository.AnniversaryRepository;
import com.couplespace.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AnniversaryService {

    @Autowired
    private AnniversaryRepository anniversaryRepository;

    @Autowired
    private UserRepository userRepository;

    public Anniversary createAnniversary(String title, String description, LocalDate anniversaryDate,
                                         String anniversaryType, Boolean isRecurring, Long createdBy) {
        // 验证用户是否存在
        if (!userRepository.existsById(createdBy)) {
            throw new RuntimeException("用户不存在");
        }

        // 验证必填字段
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("纪念日标题不能为空");
        }

        if (anniversaryDate == null) {
            throw new RuntimeException("纪念日日期不能为空");
        }

        // 验证纪念日类型
        if (!isValidAnniversaryType(anniversaryType)) {
            throw new RuntimeException("纪念日类型无效，支持：TOGETHER, BIRTHDAY_MALE, BIRTHDAY_FEMALE, CUSTOM");
        }

        Anniversary anniversary = new Anniversary(
                title.trim(),
                description != null ? description.trim() : null,
                anniversaryDate,
                anniversaryType,
                isRecurring != null ? isRecurring : false,
                createdBy
        );

        return anniversaryRepository.save(anniversary);
    }

    public Anniversary updateAnniversary(Long anniversaryId, Long userId, String title,
                                         String description, LocalDate anniversaryDate,
                                         String anniversaryType, Boolean isRecurring) {
        Optional<Anniversary> anniversaryOpt = anniversaryRepository.findById(anniversaryId);
        if (anniversaryOpt.isEmpty()) {
            throw new RuntimeException("纪念日不存在");
        }

        Anniversary anniversary = anniversaryOpt.get();

        // 验证是否是创建者
        if (!anniversary.getCreatedBy().equals(userId)) {
            throw new RuntimeException("无权限修改他人创建的纪念日");
        }

        // 更新字段
        if (title != null && !title.trim().isEmpty()) {
            anniversary.setTitle(title.trim());
        }
        if (description != null) {
            anniversary.setDescription(description.trim());
        }
        if (anniversaryDate != null) {
            anniversary.setAnniversaryDate(anniversaryDate);
        }
        if (anniversaryType != null && isValidAnniversaryType(anniversaryType)) {
            anniversary.setAnniversaryType(anniversaryType);
        }
        if (isRecurring != null) {
            anniversary.setIsRecurring(isRecurring);
        }

        return anniversaryRepository.save(anniversary);
    }

    public void deleteAnniversary(Long anniversaryId, Long userId) {
        Optional<Anniversary> anniversaryOpt = anniversaryRepository.findById(anniversaryId);
        if (anniversaryOpt.isEmpty()) {
            throw new RuntimeException("纪念日不存在");
        }

        Anniversary anniversary = anniversaryOpt.get();

        // 验证是否是创建者
        if (!anniversary.getCreatedBy().equals(userId)) {
            throw new RuntimeException("无权限删除他人创建的纪念日");
        }

        anniversaryRepository.delete(anniversary);
    }

    public List<Anniversary> getAllAnniversaries() {
        return anniversaryRepository.findAllByOrderByAnniversaryDateAsc();
    }

    public List<Anniversary> getUserAnniversaries(Long userId) {
        return anniversaryRepository.findByCreatedByOrderByAnniversaryDateAsc(userId);
    }

    public List<Anniversary> getAnniversariesByType(String type) {
        return anniversaryRepository.findByAnniversaryTypeOrderByAnniversaryDateAsc(type);
    }

    public List<Anniversary> getUpcomingAnniversaries() {
        LocalDate today = LocalDate.now();
        LocalDate thirtyDaysLater = today.plusDays(30);
        return anniversaryRepository.findByAnniversaryDateBetweenOrderByAnniversaryDateAsc(today, thirtyDaysLater);
    }

    public List<Anniversary> getTodaysAnniversaries() {
        LocalDate today = LocalDate.now();
        return anniversaryRepository.findByAnniversaryDate(today);
    }

    public List<Anniversary> getTogetherAnniversaries() {
        return anniversaryRepository.findByAnniversaryTypeOrderByAnniversaryDateAsc("TOGETHER");
    }

    public Map<String, Object> calculateAnniversaryStats(Long anniversaryId) {
        Optional<Anniversary> anniversaryOpt = anniversaryRepository.findById(anniversaryId);
        if (anniversaryOpt.isEmpty()) {
            throw new RuntimeException("纪念日不存在");
        }

        Anniversary anniversary = anniversaryOpt.get();
        LocalDate today = LocalDate.now();
        LocalDate anniversaryDate = anniversary.getAnniversaryDate();

        Map<String, Object> stats = new HashMap<>();

        if (anniversary.getIsRecurring()) {
            // 计算下一个纪念日
            LocalDate nextAnniversary = getNextRecurringDate(anniversaryDate, today);
            long daysUntilNext = ChronoUnit.DAYS.between(today, nextAnniversary);

            // 计算已经过了多少年
            int yearsPassed = today.getYear() - anniversaryDate.getYear();
            if (today.getDayOfYear() < anniversaryDate.getDayOfYear()) {
                yearsPassed--;
            }

            stats.put("type", "recurring");
            stats.put("yearsPassed", yearsPassed);
            stats.put("nextAnniversary", nextAnniversary);
            stats.put("daysUntilNext", daysUntilNext);
            stats.put("totalDays", ChronoUnit.DAYS.between(anniversaryDate, today));

            if (anniversary.getAnniversaryType().equals("TOGETHER")) {
                stats.put("message", String.format("你们已经在一起 %d 年了！", yearsPassed));
                stats.put("nextMessage", String.format("距离你们在一起 %d 周年还有 %d 天", yearsPassed + 1, daysUntilNext));
            }

        } else {
            // 一次性纪念日
            if (anniversaryDate.isAfter(today)) {
                // 未来的日期 - 倒计时
                long daysUntil = ChronoUnit.DAYS.between(today, anniversaryDate);
                stats.put("type", "countdown");
                stats.put("daysUntil", daysUntil);
                stats.put("message", String.format("距离 %s 还有 %d 天", anniversary.getTitle(), daysUntil));
            } else {
                // 过去的日期 - 计数
                long daysSince = ChronoUnit.DAYS.between(anniversaryDate, today);
                stats.put("type", "countup");
                stats.put("daysSince", daysSince);
                stats.put("message", String.format("距离 %s 已经过去了 %d 天", anniversary.getTitle(), daysSince));
            }
        }

        return stats;
    }

    public Map<String, Object> getRelationshipStats() {
        // 获取"在一起"的纪念日
        List<Anniversary> togetherAnniversaries = getTogetherAnniversaries();

        Map<String, Object> stats = new HashMap<>();

        if (togetherAnniversaries.isEmpty()) {
            stats.put("hasRelationship", false);
            stats.put("message", "还没有设置在一起的纪念日");
            return stats;
        }

        Anniversary together = togetherAnniversaries.get(0); // 取第一个
        LocalDate startDate = together.getAnniversaryDate();
        LocalDate today = LocalDate.now();

        long totalDays = ChronoUnit.DAYS.between(startDate, today);
        Period period = Period.between(startDate, today);

        // 计算下一个周年纪念日
        LocalDate nextAnniversary = getNextRecurringDate(startDate, today);
        long daysUntilNextAnniversary = ChronoUnit.DAYS.between(today, nextAnniversary);

        stats.put("hasRelationship", true);
        stats.put("startDate", startDate);
        stats.put("totalDays", totalDays);
        stats.put("years", period.getYears());
        stats.put("months", period.getMonths());
        stats.put("days", period.getDays());
        stats.put("nextAnniversary", nextAnniversary);
        stats.put("daysUntilNextAnniversary", daysUntilNextAnniversary);
        stats.put("message", String.format("你们已经在一起 %d 年 %d 个月 %d 天了",
                period.getYears(), period.getMonths(), period.getDays()));

        return stats;
    }

    private boolean isValidAnniversaryType(String type) {
        return type != null && (
                type.equals("TOGETHER") ||
                        type.equals("BIRTHDAY_MALE") ||
                        type.equals("BIRTHDAY_FEMALE") ||
                        type.equals("CUSTOM")
        );
    }

    private LocalDate getNextRecurringDate(LocalDate originalDate, LocalDate fromDate) {
        LocalDate nextDate = originalDate.withYear(fromDate.getYear());

        if (nextDate.isBefore(fromDate) || nextDate.isEqual(fromDate)) {
            nextDate = nextDate.plusYears(1);
        }

        return nextDate;
    }
}