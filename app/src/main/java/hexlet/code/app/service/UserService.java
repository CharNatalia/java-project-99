package hexlet.code.app.service;

import hexlet.code.app.dto.UserCreateDTO;
import hexlet.code.app.dto.UserDTO;
import hexlet.code.app.dto.UserUpdateDTO;

import java.util.List;

public interface UserService {
    UserDTO findById(Long id);

    List<UserDTO> findAll();

    UserDTO createUser(UserCreateDTO userCreateDTO);

    UserDTO updateUser(Long id, UserUpdateDTO userUpdateDTO);

    void deleteUser(Long id);
}
