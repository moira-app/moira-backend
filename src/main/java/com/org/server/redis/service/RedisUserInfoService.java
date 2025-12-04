package com.org.server.redis.service;

import com.org.server.member.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUserInfoService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String refresh_token_key="X-REFRESH-KEY-";
    private static final String mail_cert_key="MAIL-CERT-";
    private static final String member_exist_check="MEMBER-EXIST-";
    private static final String ticket_key="TICKET-KEY-";

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
        Long result=Long.parseLong(stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.checkCertKeyScript)
        ,List.of(mail_cert_key+email),code));
        if(result==1){
            return true;
        }
        return false;
    }
    public void setTicketKey(String memberId,String projectId){
        redisTemplate.opsForSet().add(ticket_key+memberId,projectId);
    }
    public Boolean checkTicketKey(String memberId,String projectId){
        return redisTemplate.opsForSet().isMember(ticket_key+memberId,projectId);
    }
    public void delTicketKey(String memberId,String projectId){
        redisTemplate.opsForSet().remove(ticket_key+memberId,projectId);
    }
    public void integralDelMemberInfo(Member m){
        stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.userInfoDelScript),
                List.of(member_exist_check+m.getId(),refresh_token_key+m.getId(),ticket_key+m.getId())
        );
    }
    public void logoutDelMemberInfo(Long memberId){
        stringRedisTemplate.execute(new DefaultRedisScript<>(LuaScriptSet.userInfoDelScript),
                List.of(member_exist_check+memberId,refresh_token_key+memberId)
        );
    }
}
