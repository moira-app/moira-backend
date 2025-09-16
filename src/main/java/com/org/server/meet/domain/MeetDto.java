package com.org.server.meet.domain;


import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetDto {

    private Long projectId;
    private String startTime;

    public MeetDto(Long projectId, String startTime) {
        this.projectId = projectId;
        this.startTime = startTime;
    }
}
