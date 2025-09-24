package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetDto {

    private String meetName;
    private String startTime;
    private String endTime;

    public MeetDto(String meetName,String startTime,String endTime) {
        this.meetName=meetName;
        this.startTime = startTime;
        this.endTime=endTime;
    }
}
