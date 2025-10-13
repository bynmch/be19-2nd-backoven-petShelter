package com.backoven.catdogshelter.domain.notice.command.application.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class NoticeLikeToggleRequest {
    public enum ActorType { USER, HEAD }
    private NoticeLikeToggleRequest.ActorType actorType;
    private Integer userId;
    private Integer headId;
}
