package com.couplespace.app.service;

import com.couplespace.app.entity.User;
import com.couplespace.app.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(String username, String password, String gender) {
        // 检查用户名是否已存在
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查该性别是否已有用户（确保只有一男一女）
        if (userRepository.countByGender(gender) > 0) {
            throw new RuntimeException("该性别用户已存在，情侣空间只允许一男一女");
        }

        // 验证性别
        if (!gender.equals("MALE") && !gender.equals("FEMALE")) {
            throw new RuntimeException("性别必须是MALE或FEMALE");
        }

        // 创建新用户
        User user = new User(username, password, gender);
        return userRepository.save(user);
    }

    public User loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isEmpty()) {
            throw new RuntimeException("用户不存在");
        }

        User user = userOpt.get();
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("密码错误");
        }

        return user;
    }

    public long getUserCount() {
        return userRepository.count();
    }
}