package com.couplespace.app.service;

import org.springframework.stereotype.Service;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Service
public class TimezoneService {

    // 定义时区
    private static final ZoneId LOS_ANGELES_ZONE = ZoneId.of("America/Los_Angeles");
    private static final ZoneId SHANGHAI_ZONE = ZoneId.of("Asia/Shanghai");

    // 时间格式
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 获取当前两地时间
     */
    public Map<String, Object> getCurrentTimes() {
        LocalDateTime now = LocalDateTime.now();

        // 获取洛杉矶当前时间
        ZonedDateTime laTime = ZonedDateTime.now(LOS_ANGELES_ZONE);
        // 获取上海当前时间
        ZonedDateTime shanghaiTime = ZonedDateTime.now(SHANGHAI_ZONE);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("losAngeles", Map.of(
                "time", laTime.format(FORMATTER),
                "timezone", "America/Los_Angeles",
                "offset", laTime.getOffset().toString(),
                "displayName", "洛杉矶时间"
        ));
        result.put("shanghai", Map.of(
                "time", shanghaiTime.format(FORMATTER),
                "timezone", "Asia/Shanghai",
                "offset", shanghaiTime.getOffset().toString(),
                "displayName", "上海时间"
        ));
        result.put("timeDifference", calculateTimeDifference());

        return result;
    }

    /**
     * 洛杉矶时间转上海时间
     */
    public Map<String, Object> convertLAToShanghai(String laTimeStr) {
        try {
            LocalDateTime laLocalTime = LocalDateTime.parse(laTimeStr, FORMATTER);
            ZonedDateTime laZonedTime = laLocalTime.atZone(LOS_ANGELES_ZONE);
            ZonedDateTime shanghaiZonedTime = laZonedTime.withZoneSameInstant(SHANGHAI_ZONE);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("input", Map.of(
                    "time", laTimeStr,
                    "timezone", "洛杉矶时间"
            ));
            result.put("output", Map.of(
                    "time", shanghaiZonedTime.format(FORMATTER),
                    "timezone", "上海时间"
            ));
            result.put("timeDifference", calculateTimeDifference());

            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "时间格式错误，请使用格式：yyyy-MM-dd HH:mm:ss");
            return result;
        }
    }

    /**
     * 上海时间转洛杉矶时间
     */
    public Map<String, Object> convertShanghaiToLA(String shanghaiTimeStr) {
        try {
            LocalDateTime shanghaiLocalTime = LocalDateTime.parse(shanghaiTimeStr, FORMATTER);
            ZonedDateTime shanghaiZonedTime = shanghaiLocalTime.atZone(SHANGHAI_ZONE);
            ZonedDateTime laZonedTime = shanghaiZonedTime.withZoneSameInstant(LOS_ANGELES_ZONE);

            Map<String, Object> result = new HashMap<>();
            result.put("success", true);
            result.put("input", Map.of(
                    "time", shanghaiTimeStr,
                    "timezone", "上海时间"
            ));
            result.put("output", Map.of(
                    "time", laZonedTime.format(FORMATTER),
                    "timezone", "洛杉矶时间"
            ));
            result.put("timeDifference", calculateTimeDifference());

            return result;
        } catch (Exception e) {
            Map<String, Object> result = new HashMap<>();
            result.put("success", false);
            result.put("message", "时间格式错误，请使用格式：yyyy-MM-dd HH:mm:ss");
            return result;
        }
    }

    /**
     * 计算时差
     */
    private Map<String, Object> calculateTimeDifference() {
        ZonedDateTime laTime = ZonedDateTime.now(LOS_ANGELES_ZONE);
        ZonedDateTime shanghaiTime = ZonedDateTime.now(SHANGHAI_ZONE);

        // 计算时差（小时）
        long hoursDiff = Duration.between(laTime.toLocalDateTime(), shanghaiTime.toLocalDateTime()).toHours();

        Map<String, Object> diff = new HashMap<>();
        diff.put("hours", Math.abs(hoursDiff));
        diff.put("description", String.format("上海比洛杉矶快%d小时", Math.abs(hoursDiff)));

        // 检查是否跨日期
        if (laTime.toLocalDate().isBefore(shanghaiTime.toLocalDate())) {
            diff.put("note", "上海已经是第二天了");
        } else if (laTime.toLocalDate().isAfter(shanghaiTime.toLocalDate())) {
            diff.put("note", "洛杉矶已经是第二天了");
        } else {
            diff.put("note", "两地是同一天");
        }

        return diff;
    }

    /**
     * 获取适合约会的时间建议
     */
    public Map<String, Object> getSuitableCallTimes() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("suggestions", Map.of(
                "morning", Map.of(
                        "shanghai", "09:00-11:00",
                        "losAngeles", "18:00-20:00（前一天）",
                        "description", "上海早晨，洛杉矶晚上"
                ),
                "evening", Map.of(
                        "shanghai", "20:00-22:00",
                        "losAngeles", "05:00-07:00（当天）",
                        "description", "上海晚上，洛杉矶早晨"
                ),
                "lunchTime", Map.of(
                        "shanghai", "12:00-13:00",
                        "losAngeles", "21:00-22:00（前一天）",
                        "description", "上海午餐时间，洛杉矶晚上"
                )
        ));
        result.put("note", "建议选择双方都方便的时间段进行通话");

        return result;
    }
}