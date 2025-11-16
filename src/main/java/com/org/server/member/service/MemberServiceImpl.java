package com.org.server.member.service;

import com.org.server.exception.MoiraException;
import com.org.server.member.MemberType;
import com.org.server.member.domain.*;
import com.org.server.member.repository.MemberRepository;
import com.org.server.redis.service.RedisUserInfoService;
import com.org.server.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityMemberReadService securityMemberRead;
    private final S3Service s3Service;
    private final RedisUserInfoService redisUserInfoService;
    public void memberSignIn(MemberSignInDto memberDto){
        if(!memberRepository.existsByEmail(memberDto.getEmail())&&
                !memberRepository.existsByNickName(memberDto.getNickName())){
            Member member=Member.builder()
                    .email(memberDto.getEmail())
                    .password(passwordEncoder.encode(memberDto.getPassword()))
                    .memberType(MemberType.LOCAL)
                    .nickName(memberDto.getNickName())
                    .build();
            member=memberRepository.save(member);
            System.out.printf("저장 로직 실행 완료:%s %s",member.getId(),member.getEmail());
            return ;
        }
        throw new MoiraException("이미 가입하였거나 혹은 존재하는 닉네임입니다", HttpStatus.BAD_REQUEST);
    }
    public MemberDto updateMemberInfo(MemberUpdateDto memberUpdateDto){
        Member member=memberRepository.findById(memberUpdateDto.getId()).get();
        if(!securityMemberRead.securityMemberRead().getId().equals(member.getId())){
            throw new MoiraException("권한이 부족합니다",HttpStatus.UNAUTHORIZED);
        }
        if(memberUpdateDto.getPassword()!=null){
            String password=passwordEncoder.encode(memberUpdateDto.getPassword());
            member.updatePassword(password);
        }
        if(!memberUpdateDto.getNickName().equals(member.getNickName())
                &&!memberRepository.existsByNickName(memberUpdateDto.getNickName())){

            member.updateNickName(memberUpdateDto.getNickName());
        }
        member=memberRepository.save(member);
        return MemberDto.createMemberDto(member);
    }
    public String updateMemberImg(MemberImgUpdate memberImgUpdate,String contentType){
        Member member=memberRepository.findById(memberImgUpdate.getId()).get();
        if(!securityMemberRead.securityMemberRead().getId().equals(member.getId())){
            throw new MoiraException("권한이 부족합니다",HttpStatus.UNAUTHORIZED);
        }
        if(memberImgUpdate.getFileName()==null){
            throw new MoiraException("파일 이름을 넣어주세요",HttpStatus.UNAUTHORIZED);
        }
        List<String> data=s3Service.savePreSignUrl(contentType,memberImgUpdate.getFileName());
        member.updateImgUrl(data.get(0));
        return data.get(1);
    }
    public MemberDto getMyInfo(){
        Member member=securityMemberRead.securityMemberRead();
        return MemberDto.createMemberDto(member);
    }
    public void delMember(){
        Member m=securityMemberRead.securityMemberRead();
        m.updateDeleted();
        memberRepository.save(m);
        redisUserInfoService.integralDelMemberInfo(m);
    }


}
