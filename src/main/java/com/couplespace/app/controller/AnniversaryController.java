package com.couplespace.app.controller;

import com.couplespace.app.entity.Anniversary;
import com.couplespace.app.service.AnniversaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anniversaries")
@CrossOrigin(origins = "*")
public class AnniversaryController {

    @Autowired
    private AnniversaryService anniversaryService;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @PostMapping("/create")
    public ResponseEntity<?> createAnniversary(@RequestBody Map<String, Object> request) {
        try {
            String title = request.get("title").toString();
            String description = request.get("description") != null ? request.get("description").toString() : null;
            String dateStr = request.get("anniversaryDate").toString();
            String anniversaryType = request.get("anniversaryType").toString();
            Boolean isRecurring = request.get("isRecurring") != null ?
                    Boolean.valueOf(request.get("isRecurring").toString()) : false;
            Long createdBy = Long.valueOf(request.get("createdBy").toString());

            LocalDate anniversaryDate = LocalDate.parse(dateStr, DATE_FORMATTER);

            Anniversary anniversary = anniversaryService.createAnniversary(
                    title, description, anniversaryDate, anniversaryType, isRecurring, createdBy
            );

            Map<String, Object> anniversaryMap = anniversaryToMap(anniversary);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "纪念日创建成功",
                    "anniversary", anniversaryMap
            ));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "日期格式错误，请使用 yyyy-MM-dd 格式"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/update/{anniversaryId}")
    public ResponseEntity<?> updateAnniversary(@PathVariable Long anniversaryId, @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = request.get("title") != null ? request.get("title").toString() : null;
            String description = request.get("description") != null ? request.get("description").toString() : null;
            LocalDate anniversaryDate = null;
            if (request.get("anniversaryDate") != null) {
                anniversaryDate = LocalDate.parse(request.get("anniversaryDate").toString(), DATE_FORMATTER);
            }
            String anniversaryType = request.get("anniversaryType") != null ? request.get("anniversaryType").toString() : null;
            Boolean isRecurring = request.get("isRecurring") != null ?
                    Boolean.valueOf(request.get("isRecurring").toString()) : null;

            Anniversary anniversary = anniversaryService.updateAnniversary(
                    anniversaryId, userId, title, description, anniversaryDate, anniversaryType, isRecurring
            );

            Map<String, Object> anniversaryMap = anniversaryToMap(anniversary);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "纪念日更新成功",
                    "anniversary", anniversaryMap
            ));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "日期格式错误，请使用 yyyy-MM-dd 格式"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{anniversaryId}")
    public ResponseEntity<?> deleteAnniversary(@PathVariable Long anniversaryId, @RequestParam Long userId) {
        try {
            anniversaryService.deleteAnniversary(anniversaryId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "纪念日删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAnniversaries() {
        try {
            List<Anniversary> anniversaries = anniversaryService.getAllAnniversaries();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "anniversaries", anniversaries.stream().map(this::anniversaryToMap).toList(),
                    "count", anniversaries.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserAnniversaries(@PathVariable Long userId) {
        try {
            List<Anniversary> anniversaries = anniversaryService.getUserAnniversaries(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "anniversaries", anniversaries.stream().map(this::anniversaryToMap).toList(),
                    "count", anniversaries.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<?> getAnniversariesByType(@PathVariable String type) {
        try {
            List<Anniversary> anniversaries = anniversaryService.getAnniversariesByType(type);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "anniversaries", anniversaries.stream().map(this::anniversaryToMap).toList(),
                    "count", anniversaries.size(),
                    "type", type
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingAnniversaries() {
        try {
            List<Anniversary> anniversaries = anniversaryService.getUpcomingAnniversaries();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "anniversaries", anniversaries.stream().map(this::anniversaryToMap).toList(),
                    "count", anniversaries.size(),
                    "message", "接下来30天内的纪念日"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/today")
    public ResponseEntity<?> getTodaysAnniversaries() {
        try {
            List<Anniversary> anniversaries = anniversaryService.getTodaysAnniversaries();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "anniversaries", anniversaries.stream().map(this::anniversaryToMap).toList(),
                    "count", anniversaries.size(),
                    "message", "今天的纪念日"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/stats/{anniversaryId}")
    public ResponseEntity<?> getAnniversaryStats(@PathVariable Long anniversaryId) {
        try {
            Map<String, Object> stats = anniversaryService.calculateAnniversaryStats(anniversaryId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/relationship-stats")
    public ResponseEntity<?> getRelationshipStats() {
        try {
            Map<String, Object> stats = anniversaryService.getRelationshipStats();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "stats", stats
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 辅助方法：将Anniversary转换为Map
    private Map<String, Object> anniversaryToMap(Anniversary anniversary) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", anniversary.getId());
        map.put("title", anniversary.getTitle());
        map.put("description", anniversary.getDescription() != null ? anniversary.getDescription() : "");
        map.put("anniversaryDate", anniversary.getAnniversaryDate().format(DATE_FORMATTER));
        map.put("anniversaryType", anniversary.getAnniversaryType());
        map.put("isRecurring", anniversary.getIsRecurring());
        map.put("createdBy", anniversary.getCreatedBy());
        map.put("createdAt", anniversary.getCreatedAt());
        map.put("updatedAt", anniversary.getUpdatedAt());
        return map;
    }
}