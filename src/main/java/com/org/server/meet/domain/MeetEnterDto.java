package com.org.server.meet.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MeetEnterDto {

    @NotNull(message = "회의 id를 넣어주세요")
    @Schema(description = "입장 회의 id값")
    private Long meetId;
    @NotNull(message = "입장 시작을 넣어주세요")
    @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "입장 시각")
    private String entranceTime;
    public MeetEnterDto(Long meetId, String entranceTime) {
        this.meetId = meetId;
        this.entranceTime = entranceTime;
    }
}
