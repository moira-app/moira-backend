package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetConnectDto {
    private String alias;
    private String meetName;
    private Long roomId;

    public MeetConnectDto(String meetName,String alias,Long roomId) {
        this.alias = alias;
        this.meetName=meetName;
        this.roomId=roomId;
    }
}
