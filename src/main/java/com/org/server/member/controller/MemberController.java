package com.org.server.member.controller;

import com.org.server.member.domain.MemberDto;
import com.org.server.member.domain.MemberSignInDto;
import com.org.server.member.domain.MemberUpdateDto;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberServiceImpl memberService;

    @PostMapping("/signIn")
    public ResponseEntity<ApiResponse> memberSignIn(@RequestBody @Valid MemberSignInDto memberDto){
        memberService.memberSignIn(memberDto);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<MemberDto>> myInfo(){
        return ResponseEntity.ok(ApiResponse.
                CreateApiResponse("ok",memberService.getMyInfo()));
    }
    @PostMapping("/update/myInfo")
    public ResponseEntity<ApiResponse<MemberDto>> memberUpdatePassword(@RequestBody @Valid MemberUpdateDto
                                                                        memberUpdateDto){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                memberService.updateMemberInfo(memberUpdateDto)));
    }

}
