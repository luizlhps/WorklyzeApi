package com.worklyze.worklyze.adapter.web;

import com.worklyze.worklyze.application.dto.UserMeInDto;
import com.worklyze.worklyze.application.dto.UserMeOutDto;
import com.worklyze.worklyze.application.dto.task.TaskGetAllOutDto;
import com.worklyze.worklyze.domain.interfaces.services.UserService;
import com.worklyze.worklyze.infra.config.security.CustomUserPrincipal;
import com.worklyze.worklyze.shared.page.interfaces.PageResult;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


/*    @PutMapping("/{id}")
    public ResponseEntity<TaskSimpleUpdateOutDto> update(
            @PathVariable UUID id,
            @RequestBody TaskSimpleUpdateInDto dto,
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {

    }*/

    @GetMapping("/me")
    public ResponseEntity<UserMeOutDto> me(
            @AuthenticationPrincipal CustomUserPrincipal user
    ) {
        UserMeInDto dto = new UserMeInDto(user.getEmail());
        return ResponseEntity.ok(userService.getMe(dto));
    }

}
