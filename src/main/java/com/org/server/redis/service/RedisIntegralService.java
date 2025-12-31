package com.org.server.redis.service;

import com.org.server.ticket.domain.TicketMetaDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisIntegralService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String refresh_token_key="X-REFRESH-KEY-";
    private static final String mail_cert_key="MAIL-CERT-";
    private static final String member_exist_check="MEMBER-EXIST-";
    private static final String ticket_key="TICKET-KEY-";
    private static final String stompSessionValueKey="/queue/";
    private static final String stompSessionKey="stomp-session-";

    private final StringRedisTemplate stringRedisTemplate;

    public void settingRefreshTokenMemberInfo(Long memberId,String member,String refreshToken){
        stringRedisTemplate.execute(
                new DefaultRedisScript<>(LuaScriptSet.userInfoSetScript),
                List.of(member_exist_check+memberId,refresh_token_key+memberId),
                member,refreshToken,String.valueOf(TimeUnit.DAYS.toSeconds(30L))
        );
    }
    public String getRefreshToken(Long memberId){
        return redisTemplate.opsForValue().get(refresh_token_key+memberId.toString());
    }
    public String getUserInfo(Long memberId){
        return redisTemplate.opsForValue().get(member_exist_check+memberId.toString());
    }
    public void setUserInfo(Long memberId,String m) {
        redisTemplate.opsForValue().set(member_exist_check + memberId, m);
    }
    public Boolean CheckMemberExist(Long memberId){
        String value=redisTemplate.opsForValue().get(member_exist_check+memberId);
        if(value!=null){
            return true;
        }
        return false;
    }
    public void setCertCode(String email,String code){
        redisTemplate.opsForValue().set(mail_cert_key+email,code,5L,TimeUnit.MINUTES);
    }
    public boolean checkCertCode(String email,String code){
        Long result=stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.checkCertKeyScript,Long.class)
        ,List.of(mail_cert_key+email),code);
        if(result==1){
            return true;
        }
        return false;
    }

    // 레디스에 티켓 정보 저장시 project->member꼴로저장. 이게좀더 유연한듯. 왜냐면 회원 탈퇴보단 프로젝트 or 티켓단위의
    //삭제가 더 빈번하다고 보기때문.
    public void setTicketKey(String projectId,String memberId){
        redisTemplate.opsForSet().add(ticket_key+projectId,memberId);
    }
    public Boolean checkTicketKey(String projectId,String memberId){
        return redisTemplate.opsForSet().isMember(ticket_key+projectId,memberId);
    }
    public void delTicketKey(String projectId,String memberId){
        redisTemplate.opsForSet().remove(ticket_key+projectId,memberId);
    }
    public void delProjectKey(String projectId){
        redisTemplate.opsForSet().remove(projectId);
    }

    //리프레쉬토큰,티켓키,stompession,memeber_exist_check 삭제
    public void integralDelMemberInfo(String memberId,List<TicketMetaDto> ticketMetaDtoList){

        List<String> keys= Stream.concat(Stream.of(
                member_exist_check+memberId,refresh_token_key+memberId
                        ,ticket_key+memberId,stompSessionKey+memberId),ticketMetaDtoList.stream().map(x->{
            return ticket_key+x.getProjectId();
        })).collect(Collectors.toList());

        stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.userInfoDelScript),
             keys,memberId);
    }

    //리프레쉬토큰,stompession,memeber_exist_check 삭제
    public void logoutDelMemberInfo(Long memberId){
        stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.userLogOutScript),
                List.of(member_exist_check+memberId,refresh_token_key+memberId,stompSessionKey+memberId)
        );
    }
    public boolean checkSessionKeyExist(String memberId){
        Long result=stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.checkStompSessionExistScript,Long.class)
                , List.of(stompSessionKey+memberId),stompSessionValueKey+memberId);
        if(result==1){
            return false;
        }
        return true;
    }
    public void removeSubScribeDest(String memberId){
        redisTemplate.opsForSet().remove(stompSessionKey+memberId);
    }




}
