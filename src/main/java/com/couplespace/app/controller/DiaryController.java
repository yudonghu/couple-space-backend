package com.couplespace.app.controller;

import com.couplespace.app.entity.Diary;
import com.couplespace.app.service.DiaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/diaries")
@CrossOrigin(origins = "*")
public class DiaryController {

    @Autowired
    private DiaryService diaryService;

    @PostMapping("/create")
    public ResponseEntity<?> createDiary(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = request.get("title").toString();
            String content = request.get("content").toString();

            Diary diary = diaryService.createDiary(userId, title, content);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "日记创建成功",
                    "diary", Map.of(
                            "id", diary.getId(),
                            "title", diary.getTitle(),
                            "content", diary.getContent(),
                            "userId", diary.getUserId(),
                            "userGender", diary.getUserGender(),
                            "createdAt", diary.getCreatedAt(),
                            "updatedAt", diary.getUpdatedAt()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/update/{diaryId}")
    public ResponseEntity<?> updateDiary(@PathVariable Long diaryId, @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String title = request.get("title").toString();
            String content = request.get("content").toString();

            Diary diary = diaryService.updateDiary(diaryId, userId, title, content);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "日记更新成功",
                    "diary", Map.of(
                            "id", diary.getId(),
                            "title", diary.getTitle(),
                            "content", diary.getContent(),
                            "userId", diary.getUserId(),
                            "userGender", diary.getUserGender(),
                            "createdAt", diary.getCreatedAt(),
                            "updatedAt", diary.getUpdatedAt()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @DeleteMapping("/delete/{diaryId}")
    public ResponseEntity<?> deleteDiary(@PathVariable Long diaryId, @RequestParam Long userId) {
        try {
            diaryService.deleteDiary(diaryId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "日记删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllDiaries() {
        try {
            List<Diary> diaries = diaryService.getAllDiaries();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "diaries", diaries,
                    "count", diaries.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserDiaries(@PathVariable Long userId) {
        try {
            List<Diary> diaries = diaryService.getUserDiaries(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "diaries", diaries,
                    "count", diaries.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<?> getDiariesByGender(@PathVariable String gender) {
        try {
            List<Diary> diaries = diaryService.getDiariesByGender(gender);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "diaries", diaries,
                    "count", diaries.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchDiaries(@RequestParam String keyword) {
        try {
            List<Diary> diaries = diaryService.searchDiaries(keyword);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "diaries", diaries,
                    "count", diaries.size(),
                    "keyword", keyword
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }
}