package com.org.server.support;


import com.org.server.certification.CertificationTest;
import com.org.server.certification.service.CertificationService;
import com.org.server.meet.domain.Meet;
import com.org.server.meet.repository.MeetRepository;
import com.org.server.meet.service.MeetService;
import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.project.service.ProjectService;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.repository.TicketRepository;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.jwt.JwtUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.thymeleaf.spring6.SpringTemplateEngine;

@ActiveProfiles("test")
@SpringBootTest
public class IntegralTestEnv {


    //repository
    @Autowired
    protected MemberRepository memberRepository;

    @Autowired
    protected TicketRepository ticketRepository;

    @Autowired
    protected MeetRepository meetRepository;

    @Autowired
    protected ProjectRepository projectRepository;


    //service

    @Autowired
    protected MemberServiceImpl memberService;
    @MockitoBean
    protected SecurityMemberReadService securityMemberReadService;

    @Autowired
    protected CertificationService certificationService;
    @MockitoBean
    protected RedisUserInfoService redisUserInfoService;
    @Autowired
    protected MeetService meetService;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected TicketService ticketService;

    @Autowired
    protected JwtUtil jwtUtil;

    //else
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JavaMailSender javaMailSender;
    @MockitoBean
    protected SpringTemplateEngine springTemplateEngine;
    @AfterEach
    void deleteAll(){

        ticketRepository.deleteAllInBatch();
        meetRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        memberRepository.deleteAllInBatch();
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
    protected Ticket createTicket(Member m, Project p,String alias){
        Ticket t=Ticket.builder()
                .member(m)
                .project(p)
                .alias(alias)
                .build();
        t=ticketRepository.save(t);
        return t;
    }
    protected Project createProject(String title){
        Project p=new Project(title);
        return projectRepository.save(p);
    }


}
