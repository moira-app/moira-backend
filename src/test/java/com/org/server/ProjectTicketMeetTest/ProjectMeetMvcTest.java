package com.org.server.ProjectTicketMeetTest;


import com.org.server.member.domain.Member;
import com.org.server.member.domain.MemberSignInDto;
import com.org.server.project.domain.Project;
import com.org.server.security.domain.CustomUserDetail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import com.org.server.support.IntegralTestEnv;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
public class ProjectMeetMvcTest extends IntegralTestEnv{

    @Autowired
    MockMvc mockMvc;


    Member member;

    Project withTicket;
    Project withNoTicket;
    @BeforeEach
    void settingMember(){
        member=createMember(1L);
        withTicket=createProject("testTicket");
        withNoTicket=createProject("testNoTicket");
        createTicket(member,withTicket,"zcx");
    }



    @Test
    @DisplayName("ticket이 없을떄 에러를 잘돌려주는가")
    void testingWhenNoTicket() throws Exception{

        String accessToken=jwtUtil.genAccessToken(member.getId());

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

        Mockito.when(redisUserInfoService.checkTicketKey(member.getEmail(),
                String.valueOf(
                withNoTicket.getId())))
                .thenReturn(false);

        String val=String.valueOf(withNoTicket.getId());
        String requestBody="""
                {
                "email":"test@1test.com",
                "alias": "1234"
        }""";

        mockMvc.perform(post("/enter/"+val+"/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer "+accessToken)
                        .contentType(requestBody))
                .andExpect(status().isBadRequest());
    }
}
