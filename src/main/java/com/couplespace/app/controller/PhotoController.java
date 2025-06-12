package com.couplespace.app.controller;

import com.couplespace.app.entity.Photo;
import com.couplespace.app.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/photos")
@CrossOrigin(origins = "*")
public class PhotoController {

    @Autowired
    private PhotoService photoService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPhoto(
            @RequestParam("file") MultipartFile file,
            @RequestParam("userId") Long userId,
            @RequestParam(value = "description", required = false) String description) {
        try {
            Photo photo = photoService.uploadPhoto(file, userId, description);

            Map<String, Object> photoMap = new HashMap<>();
            photoMap.put("id", photo.getId());
            photoMap.put("originalName", photo.getOriginalName());
            photoMap.put("fileName", photo.getFileName());
            photoMap.put("fileSize", photo.getFileSize());
            photoMap.put("contentType", photo.getContentType());
            photoMap.put("userId", photo.getUserId());
            photoMap.put("userGender", photo.getUserGender());
            photoMap.put("description", photo.getDescription() != null ? photo.getDescription() : "");
            photoMap.put("createdAt", photo.getCreatedAt());
            photoMap.put("downloadUrl", "/api/photos/download/" + photo.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "照片上传成功",
                    "photo", photoMap
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/download/{photoId}")
    public ResponseEntity<byte[]> downloadPhoto(@PathVariable Long photoId) {
        try {
            Optional<Photo> photoOpt = photoService.getPhotoById(photoId);
            if (photoOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Photo photo = photoOpt.get();
            byte[] photoData = photoService.getPhotoFile(photoId);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(photo.getContentType()));
            headers.setContentDispositionFormData("inline", photo.getOriginalName());
            headers.setContentLength(photoData.length);

            return new ResponseEntity<>(photoData, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/delete/{photoId}")
    public ResponseEntity<?> deletePhoto(@PathVariable Long photoId, @RequestParam Long userId) {
        try {
            photoService.deletePhoto(photoId, userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "照片删除成功"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PutMapping("/update/{photoId}")
    public ResponseEntity<?> updatePhotoDescription(
            @PathVariable Long photoId,
            @RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String description = request.get("description").toString();

            Photo photo = photoService.updatePhotoDescription(photoId, userId, description);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "照片描述更新成功",
                    "photo", Map.of(
                            "id", photo.getId(),
                            "description", photo.getDescription() != null ? photo.getDescription() : "",
                            "updatedAt", photo.getUpdatedAt()
                    )
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllPhotos() {
        try {
            List<Photo> photos = photoService.getAllPhotos();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "photos", photos.stream().map(this::photoToMap).toList(),
                    "count", photos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserPhotos(@PathVariable Long userId) {
        try {
            List<Photo> photos = photoService.getUserPhotos(userId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "photos", photos.stream().map(this::photoToMap).toList(),
                    "count", photos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/gender/{gender}")
    public ResponseEntity<?> getPhotosByGender(@PathVariable String gender) {
        try {
            List<Photo> photos = photoService.getPhotosByGender(gender);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "photos", photos.stream().map(this::photoToMapWithUser).toList(),
                    "count", photos.size()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/info/{photoId}")
    public ResponseEntity<?> getPhotoInfo(@PathVariable Long photoId) {
        try {
            Optional<Photo> photoOpt = photoService.getPhotoById(photoId);
            if (photoOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "照片不存在"
                ));
            }

            Photo photo = photoOpt.get();
            Map<String, Object> photoMap = new HashMap<>();
            photoMap.put("id", photo.getId());
            photoMap.put("originalName", photo.getOriginalName());
            photoMap.put("fileName", photo.getFileName());
            photoMap.put("fileSize", photo.getFileSize());
            photoMap.put("contentType", photo.getContentType());
            photoMap.put("userId", photo.getUserId());
            photoMap.put("userGender", photo.getUserGender());
            photoMap.put("description", photo.getDescription() != null ? photo.getDescription() : "");
            photoMap.put("createdAt", photo.getCreatedAt());
            photoMap.put("updatedAt", photo.getUpdatedAt());
            photoMap.put("downloadUrl", "/api/photos/download/" + photo.getId());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "photo", photoMap
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    // 辅助方法：将Photo转换为Map（不包含用户信息）
    private Map<String, Object> photoToMap(Photo photo) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", photo.getId());
        map.put("originalName", photo.getOriginalName());
        map.put("fileName", photo.getFileName());
        map.put("fileSize", photo.getFileSize());
        map.put("contentType", photo.getContentType());
        map.put("description", photo.getDescription() != null ? photo.getDescription() : "");
        map.put("createdAt", photo.getCreatedAt());
        map.put("downloadUrl", "/api/photos/download/" + photo.getId());
        return map;
    }

    // 辅助方法：将Photo转换为Map（包含用户信息）
    private Map<String, Object> photoToMapWithUser(Photo photo) {
        Map<String, Object> map = photoToMap(photo);
        map.put("userId", photo.getUserId());
        map.put("userGender", photo.getUserGender());
        return map;
    }
}