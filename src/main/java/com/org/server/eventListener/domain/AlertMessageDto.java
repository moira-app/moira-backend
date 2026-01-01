package com.org.server.eventListener.domain;

import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.TicketInfoDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import javax.swing.*;
import java.util.List;
import java.util.Map;

@Builder
public record AlertMessageDto(
        @Schema(description = "알림이 어떤 종류의 알림인지를 보여줍니다.")
        AlertKey alertKey,
        Long projectId,
        @Schema(description = "key:value꼴로 저장된 데이터를 바탕으로 알림의 데이터를 client사이드에 적용합니다.")
        Map<String,Object> data

) {
        public static class AliasNotification{
                @Schema(example = "MEMBERALIAS")
                public AlertKey alertKey;
                public Long memberId;
                public String alias;
        }

        public static class MemberInNotification{

                @Schema(example = "MEMBERIN")
                public AlertKey alertKey=AlertKey.MEMBERIN;
                public Long memberId;
                public String alias;
                public Master master;
        }

        public static class MemberOutNotification{

                @Schema(example = "MEMBEROUT")
                public AlertKey alertKey=AlertKey.MEMBEROUT;
                public Long memberId;
        }

        public static class MemberListNotification{

                @Schema(example = "MEMBERLIST")
                public AlertKey alertKey=AlertKey.MEMBERLIST;
                public Long memberId;
                public List<TicketInfoDto> memberList;
        }

        public static class MasterChangeNotification{

                @Schema(example = "MASTERCHANGE")
                public AlertKey alertKey=AlertKey.MASTERCHANGE;
                public Long memberId;
        }

        public static class ProjectDelNotification{
                @Schema(example = "PROJECTDEL")
                public AlertKey alertKey=AlertKey.PROJECTDEL;
                public Long projectId;
        }

        public static class MeetDelNotification{
                @Schema(example = "MEETDEL")
                public AlertKey alertKey=AlertKey.MEETDEL;
                public Long meetId;
        }

        public static class CreateMeetNotification{
                @Schema(example = "CREATEDEL")
                public AlertKey alertKey=AlertKey.CREATEMEET;
                public Long meetId;
                public String meetName;
                public String startTime;
        }

        public static class ImgChangeNotification{
                @Schema(example = "IMAGECHANGE")
                public AlertKey alertKey=AlertKey.IMAGECHANGE;
                public String getUrl;
                @Schema(description = "memberId,projectId중 에서 1개만 존재함.")
                public Long memberId;
                public Long projectId;
        }



}
