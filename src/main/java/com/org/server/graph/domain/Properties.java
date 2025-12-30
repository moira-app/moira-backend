package com.org.server.graph.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Properties{
    private String value;
    private LocalDateTime updateDate;
    public Properties(String value,LocalDateTime updateDate){
        this.value = value;
        this.updateDate=updateDate;
    }

    public void updateValue(String value){
        this.value=value;
    }
    public void updateModifyDate(LocalDateTime modifyDate){
        this.updateDate=updateDate;
    }
}