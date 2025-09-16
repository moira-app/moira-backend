package com.org.server.meet.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetDateDto {


    private Long meetId;
    private Long projectId;
    private String title;
    private String date;


    public void updateDate(String date){
        this.date=date;
    }

    @Builder
    public MeetDateDto(Long meetId, Long projectId, String title, String date) {
        this.meetId = meetId;
        this.projectId = projectId;
        this.title = title;
        this.date = date;
    }
}
