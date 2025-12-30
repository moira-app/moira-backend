package com.org.server.certification.service;

import com.org.server.certification.domain.AliasDto;
import com.org.server.chat.domain.ChatRoom;
import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.*;
import com.org.server.meet.service.MeetService;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.domain.ProjectEnterAnsDto;
import com.org.server.project.domain.ProjectInfoDto;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.ticket.domain.Master;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.DateTimeMapUtil;
import com.org.server.websocket.domain.AlertKey;
import com.org.server.websocket.domain.AlertMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.org.server.member.domain.Member;
import com.org.server.certification.repository.ProjectCertRepo;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjectMeetEntranceService {

    private final ProjectCertRepo projectCertRepo;
    private final SecurityMemberReadService securityMemberReadService;
    private final MeetService meetService;
    private final ProjectRepository projectRepository;
    private final TicketService ticketService;
    private final RedisUserInfoService redisUserInfoService;
    private final ChatRoomService chatRoomService;
    private final ApplicationEventPublisher eventPublisher;


    public List<ProjectInfoDto> getProejctList(){
        Member m=securityMemberReadService.securityMemberRead();
        return projectCertRepo.getProjectList(m);
    }
    public ProjectEnterAnsDto createTicket(String projectUrl, AliasDto ticketDto){
        Optional<Project> project=projectRepository.findByProjectUrl(projectUrl);
        if(project.isEmpty()){
            throw new MoiraException("존재하지 않는 프로젝트입니다", HttpStatus.BAD_REQUEST);
        }
        Member m=securityMemberReadService.securityMemberRead();
        if(ticketService.checkByProjectIdAndMemberId(project.get().getId(),m.getId())){
            throw new MoiraException("이미 초대되었거나 혹은 퇴출된 유저입니다", HttpStatus.BAD_REQUEST);
        }
        Ticket ticket= new Ticket(project.get().getId(),m.getId(),ticketDto.getAlias(), Master.ELSE);
        ticketService.saveTicket(ticket);
        redisUserInfoService.setTicketKey(String.valueOf(ticket.getMemberId())
                ,String.valueOf(project.get().getId()));
        publishEvent(project.get().getId(),AlertKey.MEMBERIN
                ,Map.of("memberId",m.getId(),"alias",ticketDto.getAlias(),"master",ticket.getMaster()));

        ChatRoom chatRoom=chatRoomService.ensureRoom(ChatType.PROJECT,project.get().getId());

        return ProjectEnterAnsDto.builder()
                .projectId(project.get().getId())
                .title(project.get().getTitle())
                .chatRoomId(chatRoom.getId())
                .createDate(DateTimeMapUtil.parseServerTimeToClientFormat(project.get().getCreateDate()))
                .build();
    }
    public void changeAlias(String alias,Long projectId){
        Member m=securityMemberReadService.securityMemberRead();
        Ticket ticket= ticketService.findByProjectIdAndMemberId(projectId,m.getId());
        ticket.updateAlias(alias);
        publishEvent(projectId,AlertKey.MEMBERALIAS,Map.of("memberId",m.getId(), "alias",alias));
    }
    public MeetConnectDto checkInMeet(MeetEnterDto meetEnterDto, Long projectId) {
        LocalDateTime now =LocalDateTime.parse(meetEnterDto.getEntranceTime(),DateTimeMapUtil.FLEXIBLE_NANO_FORMATTER);
        Meet meet = meetService.findById(meetEnterDto.getMeetId());

        if (now.isBefore(meet.getStartTime())||now.isAfter(meet.getEndTime())) {
            throw new MoiraException("회의 입장시간 전이거나 이후 입니다.", HttpStatus.BAD_REQUEST);
        }
        Member m = securityMemberReadService.securityMemberRead();
        Ticket t=ticketService.findByProjectIdAndMemberId(projectId,m.getId());
        ChatRoom c=chatRoomService.ensureRoom(ChatType.MEET, meet.getId());
        return new MeetConnectDto(meet.getMeetName(),
                t.getAlias()==null ? m.getNickName() :t.getAlias(),c.getId());
    }
    public void createMeet(MeetCreateDto meetDto, Long projectId){
        Project project=projectRepository.findById(projectId).get();
        LocalDateTime startTime=DateTimeMapUtil.parseClientTimetoServerFormat(meetDto.getStartTime());
        LocalDateTime endTime=DateTimeMapUtil.parseClientTimetoServerFormat(meetDto.getEndTime());
        Meet m=Meet.builder()
                .project(project)
                .startTime(startTime)
                .meetName(meetDto.getMeetName())
                .endTime(endTime)
                .build();
        Long meetId=meetService.saveMeet(m);
        publishEvent(projectId,AlertKey.CREATEMEET,Map.of("meetId",meetId,
                "meetName",meetDto.getMeetName(),"startTime", meetDto.getStartTime()));
    }

    public void delTicket(Long projectId,Long nextMaster){
        Member m=securityMemberReadService.securityMemberRead();

        ticketService.delTicket(projectId, m.getId());
        publishEvent(projectId,AlertKey.MEMBEROUT,Map.of("memberId",m.getId()));

        if(ticketService.checkIsMaster(projectId,m.getId())){
            if (nextMaster != null) {
                ticketService.nextMaster(projectId, nextMaster);
                publishEvent(projectId, AlertKey.MASTERCHANGE, Map.of("memberId", m.getId()));
            } else {
                throw new MoiraException("마스터 권한은 탈퇴시 다음 마스터를 지정해야됩니다", HttpStatus.BAD_REQUEST);
            }
        }
    }
    public void delProject(Long projectId){
        Member m=securityMemberReadService.securityMemberRead();
        if(ticketService.checkIsMaster(projectId,m.getId())){
            Optional<Project> p=projectRepository.findById(projectId);
            if(p.isEmpty()||p.get().getDeleted()){
                throw new MoiraException("없는 프로젝트입니다", HttpStatus.BAD_REQUEST);
            }
            p.get().updateDeleted();
            publishEvent(projectId,AlertKey.PROJECTDEL,Map.of("projectId",projectId));
        }
        else {
            throw new MoiraException("프로젝트에 대한 권한이 부족합니다.", HttpStatus.BAD_REQUEST);
        }
    }

    public List<MeetDateDto> getMeetList(MeetListDto meetListDto,Long projectId){
        return meetService.getMeetList(meetListDto,projectId);
    }


    public void delMeet(Long meetId,Long projectId){
        meetService.delMeet(meetId);
        publishEvent(projectId,AlertKey.MEETDEL,Map.of("meetId",meetId));
    }

    private void publishEvent(Long projectId,AlertKey alertKey,Map<String,Object> data){
        eventPublisher.publishEvent(AlertMessageDto.builder()
                .alertKey(alertKey)
                .projectId(projectId)
                .data(data)
                .build());
    }



}