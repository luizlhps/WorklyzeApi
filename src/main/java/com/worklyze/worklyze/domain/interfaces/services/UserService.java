package com.worklyze.worklyze.domain.interfaces.services;

import com.worklyze.worklyze.application.dto.UserMeInDto;
import com.worklyze.worklyze.application.dto.UserMeOutDto;
import com.worklyze.worklyze.application.dto.task.*;
import com.worklyze.worklyze.domain.entity.Task;
import com.worklyze.worklyze.domain.entity.User;

import java.util.UUID;

public interface UserService extends BaseService<User, UUID> {
    UserMeOutDto getMe(UserMeInDto userMeInDto);
}
