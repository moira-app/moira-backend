package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetConnectDto {
    private String alias;
    private String meetName;

    public MeetConnectDto(String meetName,String alias) {
        this.alias = alias;
        this.meetName=meetName;
    }
}
