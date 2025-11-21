package com.org.server.graph.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Getter
@NoArgsConstructor
public class SimpleDto {
    @Field("_id")
    private String id;
    public SimpleDto(String id) {
        this.id = id;
    }
}
