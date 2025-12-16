package com.org.server.meet.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetConnectDto {


    @Schema(description = "해당 프로젝트에서 사용하는 별칭")
    private String alias;
    private String meetName;
    @Schema(description = "회의에서 생성된 채팅방 id")
    private Long roomId;

    public MeetConnectDto(String meetName,String alias,Long roomId) {
        this.alias = alias;
        this.meetName=meetName;
        this.roomId=roomId;
    }
}
