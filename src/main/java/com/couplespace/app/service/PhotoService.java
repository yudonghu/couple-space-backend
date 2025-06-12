package com.couplespace.app.service;

import com.couplespace.app.entity.Photo;
import com.couplespace.app.entity.User;
import com.couplespace.app.repository.PhotoRepository;
import com.couplespace.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${app.photo.upload.dir}")
    private String uploadDir;

    private static final List<String> ALLOWED_CONTENT_TYPES = List.of(
            "image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp"
    );

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    public Photo uploadPhoto(MultipartFile file, Long userId, String description) throws IOException {
        // 验证用户是否存在
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();

        // 验证文件
        validateFile(file);

        // 创建上传目录
        createUploadDirectory();

        // 生成唯一文件名
        String fileName = generateUniqueFileName(file.getOriginalFilename());

        // 保存文件
        Path filePath = Paths.get(uploadDir, fileName);
        Files.copy(file.getInputStream(), filePath);

        // 创建照片记录
        Photo photo = new Photo(
                file.getOriginalFilename(),
                fileName,
                filePath.toString(),
                file.getSize(),
                file.getContentType(),
                userId,
                user.getGender()
        );

        if (description != null && !description.trim().isEmpty()) {
            photo.setDescription(description.trim());
        }

        return photoRepository.save(photo);
    }

    public void deletePhoto(Long photoId, Long userId) throws IOException {
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) {
            throw new RuntimeException("照片不存在");
        }

        Photo photo = photoOpt.get();

        // 验证是否是照片的上传者
        if (!photo.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除他人照片");
        }

        // 删除文件
        Path filePath = Paths.get(photo.getFilePath());
        if (Files.exists(filePath)) {
            Files.delete(filePath);
        }

        // 删除数据库记录
        photoRepository.delete(photo);
    }

    public Photo updatePhotoDescription(Long photoId, Long userId, String description) {
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) {
            throw new RuntimeException("照片不存在");
        }

        Photo photo = photoOpt.get();

        // 验证是否是照片的上传者
        if (!photo.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改他人照片");
        }

        photo.setDescription(description != null ? description.trim() : null);
        return photoRepository.save(photo);
    }

    public List<Photo> getAllPhotos() {
        return photoRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Photo> getUserPhotos(Long userId) {
        return photoRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public List<Photo> getPhotosByGender(String gender) {
        return photoRepository.findByUserGenderOrderByCreatedAtDesc(gender);
    }

    public Optional<Photo> getPhotoById(Long photoId) {
        return photoRepository.findById(photoId);
    }

    public byte[] getPhotoFile(Long photoId) throws IOException {
        Optional<Photo> photoOpt = photoRepository.findById(photoId);
        if (photoOpt.isEmpty()) {
            throw new RuntimeException("照片不存在");
        }

        Photo photo = photoOpt.get();
        Path filePath = Paths.get(photo.getFilePath());

        if (!Files.exists(filePath)) {
            throw new RuntimeException("照片文件不存在");
        }

        return Files.readAllBytes(filePath);
    }

    public long getUserPhotoCount(Long userId) {
        return photoRepository.countByUserId(userId);
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("请选择要上传的文件");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        if (!ALLOWED_CONTENT_TYPES.contains(file.getContentType())) {
            throw new RuntimeException("只支持上传图片文件 (JPEG, PNG, GIF, WebP)");
        }
    }

    private void createUploadDirectory() throws IOException {
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
    }

    private String generateUniqueFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        return timestamp + "_" + uuid + extension;
    }
}