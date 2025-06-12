package com.couplespace.app.service;

import com.couplespace.app.entity.Diary;
import com.couplespace.app.entity.User;
import com.couplespace.app.repository.DiaryRepository;
import com.couplespace.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DiaryService {

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private UserRepository userRepository;

    public Diary createDiary(Long userId, String title, String content) {
        // 验证用户是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();

        // 验证标题和内容
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("日记标题不能为空");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new RuntimeException("日记内容不能为空");
        }

        // 创建新日记
        Diary diary = new Diary(title.trim(), content.trim(), userId, user.getGender());
        return diaryRepository.save(diary);
    }

    public Diary updateDiary(Long diaryId, Long userId, String title, String content) {
        Optional<Diary> diaryOpt = diaryRepository.findById(diaryId);
        if (diaryOpt.isEmpty()) {
            throw new RuntimeException("日记不存在");
        }

        Diary diary = diaryOpt.get();

        // 验证是否是日记的作者
        if (!diary.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改他人日记");
        }

        // 更新内容
        if (title != null && !title.trim().isEmpty()) {
            diary.setTitle(title.trim());
        }
        if (content != null && !content.trim().isEmpty()) {
            diary.setContent(content.trim());
        }

        return diaryRepository.save(diary);
    }

    public void deleteDiary(Long diaryId, Long userId) {
        Optional<Diary> diaryOpt = diaryRepository.findById(diaryId);
        if (diaryOpt.isEmpty()) {
            throw new RuntimeException("日记不存在");
        }

        Diary diary = diaryOpt.get();

        // 验证是否是日记的作者
        if (!diary.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除他人日记");
        }

        diaryRepository.delete(diary);
    }

    public List<Diary> getAllDiaries() {
        return diaryRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Diary> getUserDiaries(Long userId) {
        return diaryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Diary> getDiariesByGender(String gender) {
        return diaryRepository.findByUserGenderOrderByCreatedAtDesc(gender);
    }

    public List<Diary> searchDiaries(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllDiaries();
        }
        return diaryRepository.searchByKeyword(keyword.trim());
    }

    public Optional<Diary> getDiaryById(Long diaryId) {
        return diaryRepository.findById(diaryId);
    }

    public long getUserDiaryCount(Long userId) {
        return diaryRepository.countByUserId(userId);
    }
}