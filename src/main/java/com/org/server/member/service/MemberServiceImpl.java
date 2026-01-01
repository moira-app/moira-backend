package com.org.server.member.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.org.server.exception.MoiraException;
import com.org.server.member.MemberType;
import com.org.server.member.domain.*;
import com.org.server.member.repository.MemberRepository;
import com.org.server.eventListener.domain.RedisEvent;
import com.org.server.eventListener.domain.RedisEventEnum;
import com.org.server.s3.domain.ImgAnsDto;
import com.org.server.s3.domain.ImgType;
import com.org.server.s3.domain.ImgUpdateDto;
import com.org.server.s3.S3Service;
import com.org.server.eventListener.domain.AlertKey;
import com.org.server.eventListener.domain.MemberAlertMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityMemberReadService securityMemberRead;
    private final S3Service s3Service;
    private final ObjectMapper objectMapper;
    private final ApplicationEventPublisher eventPublisher;
    @Value("${spring.cloud.aws.s3.personal-base-img}")
    private String baseImgUrl;
    public void memberSignIn(MemberSignInDto memberDto){
        if(!memberRepository.existsByEmail(memberDto.getEmail())&&
                !memberRepository.existsByNickName(memberDto.getNickName())){

            Member member=Member.builder()
                    .email(memberDto.getEmail())
                    .password(passwordEncoder.encode(memberDto.getPassword()))
                    .memberType(MemberType.LOCAL)
                    .nickName(memberDto.getNickName())
                    .imgUrl(baseImgUrl)
                    .build();
            member=memberRepository.save(member);
            System.out.printf("저장 로직 실행 완료:%s %s",member.getId(),member.getEmail());
            return ;
        }
        throw new MoiraException("이미 가입하였거나 혹은 존재하는 닉네임입니다", HttpStatus.BAD_REQUEST);
    }
    public MemberDto updateMemberInfo(MemberUpdateDto memberUpdateDto) {
        try {
            Member member=securityMemberRead.securityMemberRead();
            if (memberUpdateDto.getPassword() != null) {
                String password = passwordEncoder.encode(memberUpdateDto.getPassword());
                member.updatePassword(password);
            }
            if (!memberUpdateDto.getNickName().equals(member.getNickName())
                    && !memberRepository.existsByNickName(memberUpdateDto.getNickName())) {

                member.updateNickName(memberUpdateDto.getNickName());
            }
            memberRepository.save(member);
            publishRedisEvent(RedisEventEnum.MEMBERUPDATE,Map.of("memberId",member.getId(),
                    "memberData",objectMapper.writeValueAsString(member)));
            return MemberDto.builder()
                    .id(member.getId())
                    .email(member.getEmail())
                    .nickName(member.getNickName())
                    .build();
        }
        catch (JsonProcessingException e){
            throw new MoiraException("파싱 에러 발생",HttpStatus.UNAUTHORIZED);
        }
    }
    public ImgAnsDto updateMemberImg(ImgUpdateDto imgUpdateDto){
        try {
            Member member=securityMemberRead.securityMemberRead();
           ImgAnsDto imgAnsDto = s3Service.savePreSignUrl(imgUpdateDto,member.getId(),ImgType.MEMBER);
            member.updateImgUrl(imgAnsDto.getGetUrl());
            memberRepository.save(member);
            publishRedisEvent(RedisEventEnum.MEMBERUPDATE,Map.of("memberId",member.getId(),
                    "memberData",objectMapper.writeValueAsString(member)));
            publishEvent(AlertKey.IMAGECHANGE,Map.of("memberId", member.getId(),"getUrl",imgAnsDto.getGetUrl()));
            return imgAnsDto;
        }
        catch (JsonProcessingException e){
            throw new MoiraException("파싱 에러 발생",HttpStatus.UNAUTHORIZED);
        }
    }
    public MemberDto getMyInfo(){
        Member member=securityMemberRead.securityMemberRead();
        return MemberDto.builder()
                .id(member.getId())
                .email(member.getEmail())
                .imgUrl(member.getImgUrl())
                .nickName(member.getNickName())
                .build();
    }
    public void delMember() {
        Member m = securityMemberRead.securityMemberRead();
        m.updateDeleted();
        memberRepository.save(m);
        publishEvent(AlertKey.MEMBEROUT,Map.of("memberId",m.getId()));

    }
    private void publishRedisEvent(RedisEventEnum redisEventEnum,Map<String,Object> data){
        eventPublisher.publishEvent(RedisEvent.builder()
                .redisEventEnum(redisEventEnum)
                .data(data).build());
    }
    private void publishEvent(AlertKey alertKey, Map<String,Object> data){
        eventPublisher.publishEvent(MemberAlertMessageDto.builder()
                .alertKey(alertKey)
                .data(data)
                .build());
    }

}
