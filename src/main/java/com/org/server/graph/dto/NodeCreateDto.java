package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import com.org.server.graph.domain.PropertiesDto;
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
    @Schema(description = "생성한 노드의 property값입니다. key는 string, value는 propertisDto입니다.")
    private Map<String, PropertiesDto> propertiesMap;
    @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "생성 시각")
    private String createDate;

    @Builder
    public NodeCreateDto(String nodeId,String parentId,String requestId,String rootId,
                         Map<String,PropertiesDto> propertiesMap,
                         NodeType nodeType,String rootName,GraphActionType graphActionType,String createDate) {
        super(nodeId,graphActionType,rootId,requestId);
        this.parentId=parentId;
        this.propertiesMap=propertiesMap;
        this.nodeType=nodeType;
        this.rootName=rootName;
        this.createDate=createDate;
    }
}
