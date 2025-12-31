package com.org.server.redis;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.org.server.member.domain.Member;
import com.org.server.project.domain.Project;
import com.org.server.support.RedisIntegrlTestEnv;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.domain.TicketMetaDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;


public class RedisTest  extends RedisIntegrlTestEnv {

    Member m;
    Project p;
    Ticket t;


    @BeforeEach
    void setting() throws JsonProcessingException {
        m=createMember(0L);
        p=createProject("Test","Test");
        t=createTicket(m,p,"test", Master.ELSE);
        redisIntegralService.settingRefreshTokenMemberInfo(m.getId(),objectMapper.writeValueAsString(m),"dsff");
        redisIntegralService.checkSessionKeyExist(m.getId().toString());
        redisIntegralService.setTicketKey(p.getId().toString(),m.getId().toString());
    }

    @Test
    @DisplayName("회원 삭제시 모든 정보가 삭제되는지 체크")
    void testDelAllMemberData(){
        redisIntegralService.integralDelMemberInfo(
                m.getId().toString(), List.of(new TicketMetaDto(p.getId(),0L)));
        Assertions.assertThat(redisIntegralService.checkSessionKeyExist(m.getId().toString()))
                .isFalse();
        Assertions.assertThat(redisIntegralService.CheckMemberExist(m.getId())).isFalse();
        Assertions.assertThat(redisIntegralService.checkTicketKey(t.getProjectId().toString(),m.getId().toString())).isFalse();
    }


}
