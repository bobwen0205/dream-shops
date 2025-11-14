package com.bob.dreamshops.service.user;

import com.bob.dreamshops.dto.UserDto;
import com.bob.dreamshops.model.User;
import com.bob.dreamshops.request.CreateUserRequest;
import com.bob.dreamshops.request.UpdateUserRequest;

public interface IUserService {
    User getUserById(Long userId);

    User createUser(CreateUserRequest request);

    User updateUser(UpdateUserRequest request, Long userId);

    void deleteUser(Long userId);

    UserDto convertToDto(User user);
}
