package com.org.server.member.controller;

import com.org.server.member.domain.*;
import com.org.server.member.service.MemberServiceImpl;
import com.org.server.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/member")
@RequiredArgsConstructor
@Tag(name = "회원 관련 Api",description = "회원 로그인,회원 가입, 내정보 불러오기, 내정보 업데이트 관련입니다.")
public class MemberController {

    private final MemberServiceImpl memberService;


    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "이미 가입하였거나 혹은 존재하는 닉네임입니다",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class))),
    })
    @PostMapping("/signIn")
    public ResponseEntity<ApiResponseUtil<String>> memberSignIn(@RequestBody @Valid MemberSignInDto memberDto){
        memberService.memberSignIn(memberDto);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
    @Operation(summary = "내정보 불러오기", description = "내 정보를 불러옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공",useReturnTypeSchema = true),
    })
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponseUtil<MemberDto>> myInfo(){
        return ResponseEntity.ok(ApiResponseUtil.
                CreateApiResponse("ok",memberService.getMyInfo()));
    }

    @Operation(summary = "내정보 업데이트", description = "내정보를 업데이트 합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
            content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @PostMapping("/update/myInfo")
    public ResponseEntity<ApiResponseUtil<MemberDto>> memberUpdatePassword(@RequestBody @Valid MemberUpdateDto
                                                                        memberUpdateDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                memberService.updateMemberInfo(memberUpdateDto)));
    }
    @Operation(summary = "내 프로필 이미지 업데이트용", description = "내 프로필 이미지를 업데이트" +
            "하는대 필요한 url을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @PostMapping("/update/myImg")
    public ResponseEntity<ApiResponseUtil<String>> memberUpdateImg(
            @RequestBody @Valid MemberImgUpdate memberUpdateDto,
            HttpServletRequest request) {
        memberService.updateMemberImg(memberUpdateDto, request.getContentType());
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                memberService.updateMemberImg(memberUpdateDto, request.getContentType())));
    }
    @Operation(summary = "일반 회원 로그인", description = "일반 회원 로그인 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    headers = {
                            @Header(name = "Authorization", description = "Bearer [Access JWT 토큰]",
                                    schema = @Schema(type = "string")),
                    },
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @PostMapping("/login")
    public void memberLoginApi(@RequestBody NormalLoginDto normalLoginDto){
    }
    @Operation(summary = "소셜 로그인", description = "소셜 로그인 api입니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공",
                    headers = {
                            @Header(name = "Authorization", description = "Bearer [Access JWT 토큰]",
                                    schema = @Schema(type = "string")),
                    }
                    ,useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "registrationId",
            description = "소셜 로그인 공급자 ID google, naver, kakao 셋중 하나 현재는 google만 넣어주세요",
            required = true,
            in = ParameterIn.PATH)
    @GetMapping("/oauth2/authorization/{registrationId}")
    public void memberOAuth2Login(@PathVariable(name = "registrationId") String registarionId){
    }

    @PostMapping("/del")
    public ResponseEntity<ApiResponseUtil<String>> delMember(){
        memberService.delMember();
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
}
