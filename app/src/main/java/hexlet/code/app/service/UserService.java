package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;
import hexlet.code.app.exception.ResourceNotFoundException;
import hexlet.code.app.mapper.UserMapper;
import hexlet.code.app.repository.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public UserDTO findById(Long id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = " + id + " not found"));
        return userMapper.map(user);
    }

    public List<UserDTO> findAll() {
        return userRepository.findAll().stream().map(userMapper::map).toList();
    }

    @Transactional
    public UserDTO createUser(UserCreateDTO userCreateDTO) {
        var user = userMapper.map(userCreateDTO);
        user.setPassword(passwordEncoder.encode(userCreateDTO.getPassword()));
        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public UserDTO updateUser(Long id, @Valid UserUpdateDTO userUpdateDTO) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id = " + id + " not found"));
        userMapper.update(userUpdateDTO, user);
        user.setPassword(passwordEncoder.encode(userUpdateDTO.getPassword()));
        return userMapper.map(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
