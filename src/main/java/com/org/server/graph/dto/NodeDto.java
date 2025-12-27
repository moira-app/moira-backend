package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.swing.*;


@Getter
@NoArgsConstructor
public abstract class NodeDto {


    @Schema(description = "client에서 생성한 request에대한 unique 아이디값입니다.")
    private String requestId;
    private String nodeId;
    private String rootId;
    @Schema(description = "client에서 지정한 request의 속성입니다.")
    private GraphActionType graphActionType;
    @Schema(description = "서버에서 노드에 대한 작업 진행시(삭제 or 수정등) 작업에 대한 거부 여부를 표현한것." +
            "true값이면 ok, false면 client에서 거절하면됩니다.")
    private Boolean checkPass=true;


    public NodeDto(String nodeId,GraphActionType graphActionType,String rootId,String requestId) {
        this.nodeId = nodeId;
        this.graphActionType=graphActionType;
        this.rootId=rootId;
        this.requestId=requestId;
    }
    public void updateNodeId(String nodeId){
        this.nodeId=nodeId;
    }

    public void updateCheckPass(){
        this.checkPass=!this.checkPass;
    }
}
