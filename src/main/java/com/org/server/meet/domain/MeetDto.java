package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetDto {
    private String startTime;

    public MeetDto(String startTime) {
        this.startTime = startTime;
    }
}
