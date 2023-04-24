package com.team.puddy.domain.user.dto.response;

import lombok.Builder;

public record ResponseUserInfoDto (String nickname,
                                   String imagePath,
                                   boolean hasPet,
                                   boolean hasExpertInfo) {

    @Builder
    public ResponseUserInfoDto {
    }
}
