package com.org.server.graph.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Properties{
    private String value;
    private LocalDateTime modifyDate;
    public Properties(String value,LocalDateTime modifyDate){
        this.value = value;
        this.modifyDate=modifyDate;
    }

    public void updateValue(String value){
        this.value=value;
    }
    public void updateModifyDate(LocalDateTime modifyDate){
        this.modifyDate=modifyDate;
    }
}