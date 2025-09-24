package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetDto {
    private String startTime;
    private String endTime;

    public MeetDto(String startTime,String endTime) {
        this.startTime = startTime;
        this.endTime=endTime;
    }
}
