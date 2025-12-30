package com.org.server.graph.domain;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class PropertiesDto {
    private String value;
    @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "수정 시간.")
    private String updateDate;
    public PropertiesDto(String value,String updateDate){
        this.value = value;
        this.updateDate=updateDate;
    }
}
