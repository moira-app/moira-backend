package com.org.server.redis.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisUserInfoService {

    private final RedisTemplate<String,String> redisTemplate;
    private static final String refresh_token_key="X-REFRESH-KEY-";
    private static final String mail_cert_key="MAIL-CERT-";

    public void saveRefreshToken(Long memberId, String refreshToken){
        redisTemplate.opsForValue().set(refresh_token_key+memberId.toString(),refreshToken,
                TimeUnit.DAYS.toDays(30L));
    }
    public String getRefreshToken(Long memberId){
        return redisTemplate.opsForValue().get(refresh_token_key+memberId.toString());
    }

    public void delRefreshToken(Long memberId){
        redisTemplate.opsForValue().getAndDelete(refresh_token_key+memberId.toString());
    }

    public void setCertCode(String email,String code){
        redisTemplate.opsForValue().set(mail_cert_key+email,code,5L,TimeUnit.MINUTES);
    }

    public boolean checkCertCode(String email,String code){
        String codeToCheck=redisTemplate.opsForValue().get(mail_cert_key+email);
        if(codeToCheck!=null&&code.equals(codeToCheck)){
            redisTemplate.delete(mail_cert_key+email);
            return true;
        }
        return false;
    }
}
