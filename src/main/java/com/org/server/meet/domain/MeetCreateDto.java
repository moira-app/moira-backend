package com.org.server.meet.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MeetCreateDto {


    @Schema(description = "생성하고자 하는 회의의 이름")
    @NotNull(message = "이름은 있어야합니다.")
    private String meetName;
    @Schema(example="yyyy-MM-dd HH:mm:ss",description = "회의 시작 시각")
    @NotNull(message = "회의 시작 시각을 결정해주세요")
    private String startTime;
    @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "회의 종료시간")
    @NotNull(message = "회의 종료 시각을 결정해주세요")
    private String endTime;

    public MeetCreateDto(String meetName, String startTime, String endTime) {
        this.meetName=meetName;
        this.startTime = startTime;
        this.endTime=endTime;
    }
}
