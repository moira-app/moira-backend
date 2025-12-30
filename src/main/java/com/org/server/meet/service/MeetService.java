package com.org.server.meet.service;


import com.org.server.chat.domain.ChatType;
import com.org.server.chat.service.ChatRoomService;
import com.org.server.exception.MoiraException;
import com.org.server.meet.domain.Meet;
import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.domain.MeetListDto;
import com.org.server.meet.repository.MeetAdvanceRepo;
import com.org.server.meet.repository.MeetRepository;
import com.org.server.util.DateTimeMapUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MeetService {


    private final MeetRepository meetRepository;
    private final MeetAdvanceRepo meetAdvanceRepo;
    private final ChatRoomService chatRoomService;

    public List<MeetDateDto> getMeetList(MeetListDto meetListDto,Long projectId){
        LocalDateTime startTime=DateTimeMapUtil.parseClientTimetoServerFormat(meetListDto.getTime());
        LocalDateTime endTime=startTime.plusMonths(1L);
        List<Meet> meetList=meetAdvanceRepo.getMeetList(startTime,endTime,projectId);

       return meetList.stream().map(x->{
            return MeetDateDto.builder()
                    .meetId(x.getId())
                    .meetName(x.getMeetName())
                    .startTime(DateTimeMapUtil.parseServerTimeToClientFormat(x.getStartTime()))
                    .build();
        }).collect(Collectors.toList());
    }
    public void delMeet(Long meetId){
        Optional<Meet> m=meetRepository.findById(meetId);
        if(m.isEmpty()||m.get().getDeleted()){
            throw new MoiraException("없는 회의 입니다",HttpStatus.BAD_REQUEST);
        }
        m.get().updateDeleted();
    }
    public Meet findById(Long meetId){
        Optional<Meet> meet= meetRepository.findById(meetId);
        if (meet.isEmpty()||meet.get().getDeleted()) {
            throw new MoiraException("존재하지 않는 회의입니다", HttpStatus.BAD_REQUEST);
        }
        return meet.get();
    }
    public Long saveMeet(Meet meet){
        meet=meetRepository.save(meet);
        chatRoomService.ensureRoom(ChatType.MEET,meet.getId());
        return meet.getId();
    }
}
