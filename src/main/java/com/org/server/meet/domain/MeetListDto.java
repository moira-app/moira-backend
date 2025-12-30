package com.org.server.meet.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor

public class MeetListDto {
    @NotNull
    @Schema(example ="yyyy-MM-dd 00:00:00" ,description = "검색 기준일.")
    private String time;


    public MeetListDto(String time) {
        this.time = time;
    }
}
