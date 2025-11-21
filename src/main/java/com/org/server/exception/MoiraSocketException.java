package com.org.server.exception;

import com.org.server.graph.dto.NodeDto;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
public class MoiraSocketException extends RuntimeException{

    private String message;
    private Long projectId;
    private NodeDto nodeDto;

    public MoiraSocketException(String message,Long projectId,NodeDto nodeDto) {
        super(message);
        this.projectId=projectId;
        this.nodeDto=nodeDto;
    }
}
