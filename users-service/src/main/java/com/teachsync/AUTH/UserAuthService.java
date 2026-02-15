package com.teachsync.AUTH;

import com.teachsync.domain.Role;
import com.teachsync.domain.User;
import com.teachsync.dto.auth.UserRegisterDto;
import com.teachsync.repository.UserRepository;
import com.teachsync.utils.PasswordUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;

@Service
public class UserAuthService {

    private  final UserRepository userRepository;

    public UserAuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void register(UserRegisterDto userRegisterDto){
        User user = new User();
        if(userRepository.findUserByEmail(userRegisterDto.getEmail()) != null){
            throw new RuntimeException("Email already registered");
        }
        if (StringUtils.hasText(userRegisterDto.getName())){
            user.setName(userRegisterDto.getName());
        }
        if (StringUtils.hasText(userRegisterDto.getEmail())){
            user.setEmail(userRegisterDto.getEmail());
        }
        if (StringUtils.hasText(userRegisterDto.getSurname())){
            user.setSurname(userRegisterDto.getSurname());
        }
        if (StringUtils.hasText(userRegisterDto.getPassword())){
            user.setPassword(PasswordUtils.hash(userRegisterDto.getPassword()));
        }
        user.setRegisteredAt(LocalDate.now());
        user.setRole(Role.ADMIN);
        userRepository.save(user);
    }

    public User authenticate(String email, String rawPassword){
        User user = userRepository.findUserByEmail(email);
        if(user == null ){
            throw new RuntimeException("User not found");
        }
        if(!PasswordUtils.verify(rawPassword, user.getPassword())){
            throw new RuntimeException("Invalid password");
        }
        return user;
    }

}
