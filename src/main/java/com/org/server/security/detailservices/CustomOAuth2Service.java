package com.org.server.security.detailservices;

import com.org.server.member.MemberType;
import com.org.server.member.domain.Member;
import com.org.server.member.repository.MemberRepository;
import com.org.server.member.service.SecurityMemberReadService;
import com.org.server.security.domain.*;
import com.org.server.util.RandomCharSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import com.org.server.exception.MoiraException;

@Slf4j
public class CustomOAuth2Service extends DefaultOAuth2UserService {

    private  MemberRepository memberRepository;

    public CustomOAuth2Service(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User userData=super.loadUser(userRequest);

        String registrationId=userRequest.getClientRegistration().getRegistrationId();

        OAuth2Response response=createOAuth2Response(registrationId,userData);

        if(response==null){
            throw new RuntimeException();
        }
        else{
            log.info("{}-유저가 로그인 시도",registrationId);
            Optional<Member> member=memberRepository.findByEmail(response.getEmail());
            if(member.isPresent()&&member.get().getDeleted()){
                throw new MoiraException("없는 회원입니다", HttpStatus.BAD_REQUEST);
            }
            if(member.isPresent()){
                return whenExistMember(member.get(),response.getProvider());
            }
            log.info("기존에 없는 {}-{}가 로그인 시도",response.getProvider(),response.getEmail());
            return whenNotExistMember(response);
        }

    }
    private OAuth2User whenExistMember(Member member, MemberType provider){
        log.info("기존에 존재하는 {}-{} 가 로그인 시도",provider,member.getEmail());
        return CustomOAuth2User
                .builder()
                .id(member.getId())
                .member(member)
                .email(member.getEmail())
                .memberType(member.getMemberType())
                .build();
    }

    @Transactional
    private OAuth2User whenNotExistMember(OAuth2Response response){
        Member newMember=Member.builder()
                .email(response.getEmail())
                .memberType(response.getProvider())
                .nickName(RandomCharSet.createRandomName())
                .build();

        newMember=memberRepository.save(newMember);
        return CustomOAuth2User
                .builder()
                .id(newMember.getId())
                .member(newMember)
                .email(newMember.getEmail())
                .memberType(newMember.getMemberType())
                .build();
    }

    private OAuth2Response createOAuth2Response(String registrationId,OAuth2User userData){
        switch (registrationId){
            case "kakao" -> {
                return new KakaoResponse(userData.getAttributes());
            }
            case "naver"->{
                return new NaverResponse(userData.getAttributes());
            }
            case "google"->{
                return  new GoogleResponse(userData.getAttributes());
            }
            default ->{
                return null;
            }
        }

    }

}
