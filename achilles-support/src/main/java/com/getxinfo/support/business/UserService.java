package com.getxinfo.support.business;

import com.getxinfo.support.dataaccess.User;
import com.getxinfo.support.dataaccess.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findByTelphone(String telphone) {
        return userRepository.findByTelphone(telphone);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

}
