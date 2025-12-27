package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import com.org.server.websocket.domain.EventEnvelope;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;


@Getter
@NoArgsConstructor
public class NodeCreateDto extends NodeDto {


    @Schema(description = "생성한 노드 타입입니다.")
    private NodeType nodeType;
    @Schema(description = "생성한 노드의 부모 노드입니다. client에서 보내준 값을 넣습니다. rootNode는 해당값이 비어있습니다.")
    private String parentId;
    @Schema(description = "생성한 루트 노드의 이름입니다.")
    private String rootName;
    @Schema(description = "생성한 노드의 property값입니다.")
    private Map<String, Properties> propertiesList;

    @Builder
    public NodeCreateDto(String nodeId,String parentId,String requestId,String rootId,
                         Map<String,Properties> propertiesList,
                         NodeType nodeType,String rootName,GraphActionType graphActionType) {
        super(nodeId,graphActionType,rootId,requestId);
        this.parentId=parentId;
        this.propertiesList=propertiesList;
        this.nodeType=nodeType;
        this.rootName=rootName;
    }
}
