package com.org.server.graph.dto;

import com.org.server.graph.ChangeType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.Properties;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;


@Getter
public class NodeCreateDto extends NodeDto {

    private NodeType nodeType;
    private String parentId;
    private String rootName;
    private Map<String, Properties> propertiesList;
    private Long projectId;


    @Builder
    public NodeCreateDto(String nodeId, ChangeType changeType
            , String parentId, Map<String,Properties> propertiesList,
                         NodeType nodeType,String rootName,Long projectId) {
        super(nodeId, changeType);
        this.parentId=parentId;
        this.propertiesList=propertiesList;
        this.nodeType=nodeType;
        this.rootName=rootName;
        this.projectId=projectId;
    }
}
