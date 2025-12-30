package com.org.server.graph.dto;

import com.org.server.graph.GraphActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class PropertyChangeDto extends NodeDto {

    private String name;
    private String value;

    @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "노드의 수정 날짜입니다. 혹여나 현재 클라이언트에 반영된 특정 노드의 특정 속성값의 수정 날짜가" +
            "해당 요청의 수정 날짜보다 이후의 것일경우 해당 요청은 무시하면됩니다.")
    private String updateDate;
    @Builder
    public PropertyChangeDto(String nodeId,String rootId,String requestId, String name, String value,
                             String updateDate, GraphActionType graphActionType) {
        super(nodeId,graphActionType,rootId,requestId);
        this.name=name;
        this.value=value;
        this.updateDate=updateDate;
    }
}
