package com.org.server.websocket.controller;


import com.fasterxml.jackson.databind.DatabindException;
import com.org.server.chat.domain.ChatEvent;
import com.org.server.chat.domain.ChatMessageDto;
import com.org.server.chat.domain.ChatType;
import com.org.server.eventListener.domain.AlertKey;
import com.org.server.graph.GraphActionType;
import com.org.server.graph.NodeType;
import com.org.server.graph.domain.PropertiesDto;
import com.org.server.graph.dto.NodeCreateDto;
import com.org.server.graph.dto.NodeDelDto;
import com.org.server.graph.dto.PropertyChangeDto;
import com.org.server.graph.dto.StructureChangeDto;
import com.org.server.eventListener.domain.AlertMessageDto;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.TicketInfoDto;
import com.org.server.websocket.domain.EventEnvelope;
import com.org.server.websocket.domain.WebRtcDataType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;


@Controller
@Tag(name = "소켓 데이터 교환에대한 api 문서",description = "crdt,시그널링,채팅에 관한 문서입니다.경로는 구독해야되는 경로입니다." +
        "특별한 기재사항이 없다면 data 영역에 값을 key:value로 넣어주시면됩니다.")
public class SocketGuideController {


    @Operation(summary = "시그널링", description = "시그널링 과정에 대해서 설명해 놓았습니다. 아래 클래스에 명기된 프로퍼티들은 필수값입니다." +
            " 그외의 ice 데이터등 교환시에 필요한것 들은 " +
            "같은 경로로 보내주시면되며,EventEnvelope의 data에 추가로 넣어주시면됩니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송은 보낸 eventenvelope이 그대로 전송됩니다."),
    })
    @PostMapping("/topic/meet/{meetId}")
    public EventEnvelope singnlingdata(@RequestBody SingalingDataClass singalingDataClass){
        return EventEnvelope.builder().build();
    }
    @Operation(summary = "채팅", description = "채팅 전송에대한 설명입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 성공"),
    })
    @PostMapping("/topic/chatroom-{projectId}-{chatType}-{roomId}")
    public ChatMessageDto chattingData(@RequestBody ChattingDataClass chattingDataClass){
        return ChatMessageDto.builder().build();
    }
    @Operation(summary = "알림에 대한 설명", description = "해당 프로젝트에서 특정 인원이 새로이 합류,퇴장 혹은 별칭의 변경등 해당 프로젝트에" +
            "속한 인원들이 알아야되는 알림 발생에 대한 설명입니다." +
            "구독 경로는 기존의 chatting과 똑같이 따라가되, project의 채팅방쪽으로 전송되는 알림입니다.")
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "alertmessage 기본꼴",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.class))
            ),
            @ApiResponse(
                    responseCode = "201",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.MemberOutNotification.class))
            ),
            @ApiResponse(
                    responseCode = "202",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.\"",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.ImgChangeNotification.class))
            ),
            @ApiResponse(
                    responseCode = "203",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.MemberListNotification.class))
            ),
            @ApiResponse(
                    responseCode = "204",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.MeetDelNotification.class))
            ),
            @ApiResponse(
                    responseCode = "205",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.CreateMeetNotification.class)
            )),
            @ApiResponse(
                    responseCode = "206",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.MasterChangeNotification.class)
                    )),
            @ApiResponse(
                    responseCode = "207",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.ProjectDelNotification.class))),
            @ApiResponse(
                    responseCode = "208",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.MemberInNotification.class))),
            @ApiResponse(
                    responseCode = "209",
                    description = "data영역에 alertkey를 제외한 데이터가 key:value로 들어갑니다.",
                    // useReturnTypeSchema 대신 content를 직접 명시
                    content = @Content(schema = @Schema(implementation = AlertMessageDto.AliasNotification.class)))
    })
    @PutMapping("/topic/chatroom-{projectId}-{chatType}-{roomId}")
    public AlertMessageDto memberOut(){
        return AlertMessageDto.builder().build();}

    @Operation(summary = "화이트 보드 편집시 기본 데이터", description = "화이트 보드 동시 편집시 모든 요청에 대해서 필요한 데이터입니다." +
            "노드 삭제,구조 수정 요청, 구독 경로는 basicnodeclass를 따라갑니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전송 성공"),}
    )
    @PostMapping("/topic/crdt/{projectId}")
    public void baisnodeclass(@RequestBody BasicNdoeClass basicNdoeClass){
    }
    @Operation(summary = "화이트 보드 노드생성 요청", description = "baisnodeclass에서 이어지는 node create에 대한 내용입니다." )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 성공"),
    })
    @PostMapping("/nodecreate")
    public NodeCreateDto createNode(@RequestBody NodeCreateClass nodeCreateClass){
        return NodeCreateDto.builder().build();
    }
    @Operation(summary = "화이트 보드 노드 속성 수정", description = "basicnodeclass에서 이어지는 node property 수정요청에 대한 내용입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 성공"),
    })
    @PostMapping("/propertyChange")
    public PropertyChangeDto propertymodify(@RequestBody NodePropertyClass nodePropertyClass){
        return PropertyChangeDto.builder().build();
    }

    @Operation(summary = "화이트 보드 노드 삭제", description = "basicnodeclass에서 이어지는 노드 삭제 요청에 대한 내용입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 성공"),
    })
    @PostMapping("/nodeDel")
    public NodeDelDto nodeDel(@RequestBody BasicNdoeClass basicNdoeClass){
        return NodeDelDto.builder().build();
    }
    @Operation(summary = "화이트 보드 노드 트리 구조 수정", description = "basicnodeclass에서 이어지는 노드 트리 구조의 수정 요청에 대한 내용입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "전송 성공"),
    })
    @PostMapping("/nodeTreeStructure")
    public StructureChangeDto nodeStructure(@RequestBody BasicNdoeClass basicNdoeClass){
        return StructureChangeDto.builder().build();
    }
    @Getter
    private class SingalingDataClass{
        @Schema(description =" crdt.webrtc 를넣어주세요. EventEnvelope의 type에 해당됩니다.")
        private String type;
        @Schema(description = "입장하는 사람은 offer를, 기존에 있던 인원이 입장하는 사람에대한 응답을 보낼시엔 ANSWER를 넣어주세요")
        private WebRtcDataType webRtcDataType;
        @Schema(description = "보낸이의 id값입니다. 서버에서 처리합니다.")
        private String senderId;
        @Schema(description = "받는이의 회원 id값. 클라이언트에서 answer를 보낼시에 넣어주십시오.")
        private String targetId;
        @Schema(description = "회의 id값,클라이언트에서 넣어주세요.")
        private String meetId;
    }
    @Getter
    private class ChattingDataClass{
        @Schema(description ="chat.message 를넣어주세요. EventEnvelope의 type에 해당됩니다.")
        private String type;
        @Schema(description ="채팅룸 id값입니다."+
                "EventEnvelope의 data에 넣어주세요")
        private Long roomId;
        @Schema(description ="프로젝트에 속한 채팅방이면 PROJECT,회의에 속한 채팅방이면 MEET를 넣어주세요" +
                "EventEnvelope의 data에 넣어주세요")
        private ChatType chatType;
        @Schema(description = "단순 전송,삭제,수정을 표시하는 값입니다. READ는 전송시에 서버에서 넣어서 보내는 값입니다."+
                "EventEnvelope의 data에 넣어주세요")
        private ChatEvent chatEvent;
        @Schema(description = "보내는 이의 memberid값입니다."+
                "EventEnvelope의 data에 넣어주세요")
        private Long senderId;
        @Schema(description = "채팅 내용입니다."+
                "EventEnvelope의 data에 넣어주세요")
        private String content;
        @Schema(description = "채팅방이 속한(회의든 프로젝트든) 프로젝트 id값입니다."+
                "EventEnvelope의 data에 넣어주세요")
        private Long projectId;
        @Schema(description = "채팅 id값입니다. 삭제,수정 시에는 첨부 해주시고, 처음 발행하는 채팅이면 서버에서 넣어서 응답을 보냅니다"
                + "EventEnvelope의 data에 넣어주세요")
        private String chatId;

    }
    @Getter
    private class NodeCreateClass{
        @Schema(description = "create시 nodetype을 의미합니다. 일반 노드면 element를 해주시면됩니다.")
        private NodeType nodeType;
        @Schema(description = "생성하는 루트 노드의 이름. 즉 화이트 보드의 이름입니다.")
        private String rootName;
        @Schema(description = "생성하는 노드의 속성값. key값으로 이름을 value로 PropertisDto를 주시면됩니다.")
        private Map<String, PropertiesDto> properties;
    }
    @Getter
    private class NodePropertyClass{
        @Schema(description = "수정 하려는 property 값의 이름")
        private String name;
        @Schema(description = "수정 하려는 property 값")
        private String value;
        @Schema(example = "yyyy-MM-dd HH:mm:ss",description = "수정 하려는 property의 수정 날짜.")
        private String updateDate;
    }

    @Getter
    private class BasicNdoeClass{
        @Schema(description ="crdt.action 를넣어주세요. EventEnvelope의 type에 해당됩니다.")
        private String type;
        @Schema(description = "생성시엔 create, 삭제시인 delete, 속성 수정시엔 property, 트리구조 수정시엔 structure 입니다.")
        private GraphActionType actionType;
        @Schema(description = "모든 노드의 조상이되는 rootNode의 id값입니다. root노드를 create시엔 빈값을 주시면됩니다.")
        private String rootId;
        @Schema(description = "부모 노드의 id값입니다. rootnode의 경우에는 빈값을 주면됩니다.")
        private String parentId;
        @Schema(description = "클라이언트에서 생성하는 요청에대한 unique한 id값입니다. 나중에 undo로그용으로 만들어둔것.")
        private String requestId;
        @Schema(description = "소켓 전송경로를 위한 projectid값.")
        private Long projectId;
        @Schema(description = "노드의 id값입니다. 노드 생성시에는 서버에서 이값을 넣어줍니다.")
        private String nodeId;
    }



}
