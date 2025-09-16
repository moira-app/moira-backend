package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetConnectDto {
    private String meetUrl;
    private String alias;

    public MeetConnectDto(String meetUrl, String alias) {
        this.meetUrl = meetUrl;
        this.alias = alias;
    }
}
