package com.org.server.graph.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class Properties{
    private String value;
    private String modifyDate;
    public Properties(String value,String modifyDate){
        this.value = value;
        this.modifyDate=modifyDate;
    }

    public void updateValue(String value){
        this.value=value;
    }
    public void updateModifyDate(String modifyDate){
        this.modifyDate=modifyDate;
    }
}