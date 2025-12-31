package com.org.server.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.server.eventListener.listener.AlertEventListener;
import com.org.server.eventListener.listener.RedisEventListener;
import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.redis.service.RedisIntegralService;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.repository.TicketRepository;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public class RedisIntegrlTestEnv {




    @Autowired
    protected RedisIntegralService redisIntegralService;
    @Autowired
    protected MemberRepository memberRepository;
    @Autowired
    protected TicketRepository ticketRepository;
    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired

    protected ObjectMapper objectMapper;


    @AfterEach
    void deleteAll(){
        memberRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
    }


    protected Member createMember(Long idx){

        Member member=Member.builder()
                .email("test@"+idx+"test.com")
                .nickName("test"+idx)
                .password(passwordEncoder.encode("1234"))
                .memberType(MemberType.LOCAL)
                .build();
        member=memberRepository.save(member);
        return member;
    }
    protected Ticket createTicket(Member m, Project p, String alias, Master master){
        Ticket t=Ticket.builder()
                .memberId(m.getId())
                .projectId(p.getId())
                .alias(alias)
                .master(master)
                .build();
        t=ticketRepository.save(t);
        return t;
    }
    protected Project createProject(String title, String projectUrl){
        Project p=new Project(title,projectUrl);
        return projectRepository.save(p);
    }


}
