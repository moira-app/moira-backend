package com.org.server.meet.service;


import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.meet.repository.MeetAdvanceRepo;
import com.org.server.meet.repository.MeetRepository;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.project.domain.Project;
import com.org.server.project.repository.ProjectRepository;
import com.org.server.ticket.domain.Ticket;
import com.org.server.ticket.repository.TicketRepository;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.org.server.util.RandomCharSet;
import java.util.Optional;
import java.time.LocalDateTime;
import com.org.server.member.domain.Member;
import java.util.List;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {

    private final MeetRepository meetRepository;
    private final MeetAdvanceRepo meetAdvanceRepo;
    private final SecurityMemberReadService securityMemberReadService;

    public List<MeetDateDto> getMeetList(String date){
        LocalDateTime startTime=LocalDate.parse(date,DateTimeMapUtil.formatByDot2).atStartOfDay();
        LocalDateTime endTime=startTime.plusMonths(1L);
        Member m=securityMemberReadService.securityMemberRead();

        List<MeetDateDto> meetList=meetAdvanceRepo.getMeetList(startTime,endTime,m);

        meetList.forEach(x->{
            x.updateDate(DateTimeMapUtil.provideTimePattern(x.getDate()));

        });
        return meetList;
    }

    public void delMeet(Long meetId){
        Meet m=meetRepository.findById(meetId).get();
        if(m.getDeleted()){
            return ;
        }
        m.updateDeleted();
    }
}
