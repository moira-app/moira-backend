package com.org.server.meet.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class MeetDateDto {

    private Long meetId;
    private String meetName;
    private String startTime;


    public void updateDate(String startTime){
        this.startTime=startTime;
    }

    @Builder
    public MeetDateDto(Long meetId,String meetName, String startTime) {
        this.meetId = meetId;
        this.meetName=meetName;
        this.startTime = startTime;
    }
}
