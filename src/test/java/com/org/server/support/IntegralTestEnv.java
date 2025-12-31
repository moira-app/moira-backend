package com.org.server.support;


import com.org.server.certification.repository.ProjectCertRepo;
import com.org.server.certification.service.CertificationService;
import com.org.server.certification.service.ProjectMeetEntranceService;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.repository.ChatMessageAdvanceRepository;
import com.org.server.chat.repository.ChatMessageRepository;
import com.org.server.chat.repository.ChatRoomRepository;
import com.org.server.chat.service.ChatMessageService;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.eventListener.listener.RedisEventListener;
import com.org.server.graph.repository.GraphRepository;
import com.org.server.graph.service.GraphService;
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
import com.org.server.redis.service.RedisIntegralService;
import com.org.server.s3.S3Service;
import com.org.server.scheduler.repository.SchedulerRepository;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.repository.AdvanceTicketRepository;
import com.org.server.ticket.repository.TicketRepository;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.jwt.JwtUtil;
import com.org.server.eventListener.listener.AlertEventListener;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
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
    protected ChatMessageRepository chatMessageRepository;
    @Autowired
    protected TicketRepository ticketRepository;

    @Autowired
    protected MeetRepository meetRepository;

    @Autowired
    protected ProjectRepository projectRepository;

    @Autowired
    protected GraphRepository graphRepository;



    @Autowired
    protected SchedulerRepository schedulerRepository;

    @Autowired
    protected ProjectCertRepo projectCertRepo;
    @Autowired
    protected S3Service s3Service;

    @Autowired
    protected GraphService graphService;

    @Autowired
    protected ChatMessageAdvanceRepository chatMessageAdvanceRepository;
    @Autowired
    protected ChatMessageService chatMessageService;

    @Autowired
    protected MemberServiceImpl memberService;
    @MockitoBean
    protected SecurityMemberReadService securityMemberReadService;
    @MockitoBean
    protected AlertEventListener alertEventListener;

    @MockitoBean
    protected RedisEventListener redisEventListener;
    @Autowired
    protected CertificationService certificationService;
    @MockitoBean
    protected RedisIntegralService redisIntegralService;
    @Autowired
    protected MeetService meetService;
    @Autowired
    protected ProjectService projectService;
    @Autowired
    protected TicketService ticketService;
    @Autowired
    protected ProjectMeetEntranceService projectCertService;

    @Autowired
    protected AdvanceTicketRepository advanceTicketRepository;

    @Autowired
    protected ChatRoomRepository chatRoomRepository;
    @Autowired
    protected ChatRoomService chatRoomService;

    //else
    @Autowired
    protected JwtUtil jwtUtil;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockitoBean
    protected JavaMailSender javaMailSender;
    @MockitoBean
    protected SpringTemplateEngine springTemplateEngine;


    @AfterEach
    void deleteAll(){
        graphRepository.deleteAll();
        memberRepository.deleteAllInBatch();
        ticketRepository.deleteAllInBatch();
        meetRepository.deleteAllInBatch();
        projectRepository.deleteAllInBatch();
        chatRoomRepository.deleteAllInBatch();
        chatMessageRepository.deleteAll();


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

    protected ChatRoom createChatRoom(Project p){
        ChatRoom c=ChatRoom.builder()
                .chatType(ChatType.PROJECT)
                .refId(p.getId())
                .build();

        return chatRoomRepository.save(c);
    }

    protected Project createProject(String title,String projectUrl){
        Project p=new Project(title,projectUrl);
        return projectRepository.save(p);
    }


}