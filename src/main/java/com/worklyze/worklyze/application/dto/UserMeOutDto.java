package com.worklyze.worklyze.application.dto;

import com.worklyze.worklyze.domain.entity.User;
import com.worklyze.worklyze.shared.annotation.AutoMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@AutoMap(User.class)
public class UserMeOutDto {
    private UUID id;
    private String name;
    private String surname;
    private String username;
    private String email;
}
