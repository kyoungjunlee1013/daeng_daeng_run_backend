package com.daengdaengrun.daengdaengrunbackend.user.dto;

import com.daengdaengrun.daengdaengrunbackend.user.entity.User;
import lombok.Getter;

@Getter
public class UserResponseDto {
    private final Long id;
    private final String email;
    private final String nickname;
    private final String profileImageUrl;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.nickname = user.getNickname();
        this.profileImageUrl = user.getProfileImageUrl();
    }

}
