package com.org.server.member;

import com.org.server.eventListener.domain.RedisEvent;
import com.org.server.member.domain.Member;
import com.org.server.member.domain.MemberSignInDto;
import com.org.server.member.domain.MemberUpdateDto;
import com.org.server.s3.domain.ImgAnsDto;
import com.org.server.s3.domain.ImgUpdateDto;
import com.org.server.security.domain.CustomUserDetail;
import com.org.server.support.IntegralTestEnv;
import com.org.server.eventListener.domain.MemberAlertMessageDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@AutoConfigureMockMvc
public class MemberCreateTest extends IntegralTestEnv {
    @Autowired
    MockMvc mockMvc;


    Member member;

    @BeforeEach
    void settingMember(){
        member=createMember(1L);
    }
    @Test
    @DisplayName("회원가입이 성공적으로 진행이되는가")
    void createMemBerTest(){

        assertThat(memberRepository.findById(member.getId()).isPresent()).isTrue();

        MemberSignInDto memberSignInDto=
                new MemberSignInDto("test@naver.com","zczxc","1234");
        memberService.memberSignIn(memberSignInDto);


        List<Member> memberList=memberRepository.findAll();

        assertThat(memberList.get(1).getEmail()).isEqualTo(memberSignInDto.getEmail());
    }
    @Test
    @DisplayName("중복회원 가입 방지 이벤트")
    void createSameMemberTest() throws Exception{

        MemberSignInDto memberSignInDto=
                new MemberSignInDto(member.getEmail(),member.getNickName(),"1234");
        String requestBody="""
                {
                "email":"test@1test.com",
                "nickName":"test1",
                "password": "1234"
        }""";

        mockMvc.perform(post("/member/signIn")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andDo(print())
                .andExpect(status().isBadRequest());

    }
    @Test
    @DisplayName("회원 정보 업데이트 테스트")
    void testUpdateUserInfo()throws  Exception{

        Mockito.doNothing()
                .when(redisEventListener)
                .redisEventHandler(Mockito.any(RedisEvent.class));


        CustomUserDetail customUserDetail = new CustomUserDetail(member);

        // Authentication mock
        org.springframework.security.core.Authentication authentication =
                Mockito.mock(Authentication.class);
        Mockito.when(authentication.getPrincipal()).thenReturn(customUserDetail);

        // SecurityContext 세팅
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);


        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(member);

        MemberUpdateDto memberUpdateDto=new MemberUpdateDto(
                "testing",
                "12345"
        );
        memberService.updateMemberInfo(memberUpdateDto);
        Member test=memberRepository.findById(member.getId()).get();
        assertThat(test.getNickName()).isEqualTo("testing");
        Mockito.verify(redisEventListener,Mockito.times(1))
                .redisEventHandler(Mockito.any(RedisEvent.class));

        String requestBody="""
                {
                "mail":"test@1test.com",
                "password": "1234"
        }""";

        String requestBody2="""
                {
                "mail":"test@1test.com",
                "password": "12345"
        }""";

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2))
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("회원 이미지 업데이트 테스트")
    void testGetMemberImgUrlUpdate(){

        Mockito.doNothing()
                .when(redisEventListener)
                .redisEventHandler(Mockito.any(RedisEvent.class));

        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(member);
        ImgAnsDto imgUrl= memberService.updateMemberImg(ImgUpdateDto.builder()
                        .fileName("test")
                        .contentType("image/png")
                .build());

        assertThat(imgUrl.getPutUrl()).isNotEmpty();
        Member m2=memberRepository.findById(member.getId()).get();
        assertThat(m2.getImgUrl()!=null).isTrue();

        Mockito.verify(redisEventListener,Mockito.times(1))
                .redisEventHandler(Mockito.any(RedisEvent.class));
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    void updateMemberDelTest(){
        Mockito.when(securityMemberReadService.securityMemberRead())
                .thenReturn(member);
        Mockito.doNothing()
                .when(redisIntegralService)
                .integralDelMemberInfo(member.getId().toString(),List.of());
        Mockito.doNothing()
                .when(redisIntegralService)
                .removeSubScribeDest(member.getId().toString());

        Mockito.doNothing()
                        .when(alertEventListener)
                                .alertGlobalMessage(Mockito.any(MemberAlertMessageDto.class));

        memberService.delMember();
        Member m=memberRepository.findById(member.getId()).get();
        assertThat(m.getDeleted()).isTrue();
        Mockito.verify(alertEventListener).alertGlobalMessage(Mockito.any(MemberAlertMessageDto.class));
    }

    @Test
    @DisplayName("멤버 관련 dto valid 테스팅")
    void test() throws Exception {


        String accessToken=jwtUtil.genAccessToken(member.getId());

        String requestBody="""
                {
                "id":"1",
                "birthDay":"1",
                "birthMonth":"2",
                "birthYear":"2000"
        }""";


        String requestBody2="""
                {
                "id":"1",
                "nickName":"12",
                "birthDay":"30",
                "birthMonth":"2",
                "birthYear":"2000"
        }""";


        String requestBody3="""
                {
                "id":"1",
                "nickName":"1212xcvxcxcxcxcvxcxcvxcvxccxcvcxvxc",
                "birthDay":"30",
                "birthMonth":"2",
                "birthYear":"2000"
        }""";


        mockMvc.perform(post("/member/update/myInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+accessToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/member/update/myInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody2)
                        .header("Authorization","Bearer "+accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/member/update/myInfo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody3)
                        .header("Authorization","Bearer "+accessToken)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

    }

}
