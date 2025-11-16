package com.org.server.certification.controller;


import com.org.server.certification.service.CertificationService;
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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/cert")
@Tag(name = "검증을 실시하는 api.",description = "입장 코드 같은 검증이 필요한 요소애 대해서 진행하는 api입니다.")
public class CertificationController {

    private final CertificationService certificationService;



    @Operation(summary = "메일 인증 코드 생성", description ="메일 인증에 필요한 코드를 생성하는 api입니다. " +
            "생성시 해당 메일로 코드를 전송합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "생성 성공",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "mail",
            description = "인증 코드를 요청할 메일값입니다.",
            required = true,
            example = "test@test.com",
            in = ParameterIn.PATH)
    @GetMapping("/create/{mail}")
    public ResponseEntity<ApiResponseUtil<String>> createCode(@PathVariable(name ="mail")String mail){
        certificationService.createCertCode(mail);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }


    @Operation(summary = "메일 인증 코드 인증 api")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok",
                    useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "mail",
            description = "인증 코드를 요청한 메일값",
            required = true,
            example = "test@test.com",
            in = ParameterIn.PATH)
    @Parameter(name = "code",
            description = "인증 코드값",
            required = true,
            example = "1234abcd",
            in = ParameterIn.PATH)
    @GetMapping("/check/{mail}/{code}")
    public ResponseEntity<ApiResponseUtil<String>> checkCode(@PathVariable(name = "mail")String mail,
                                                       @PathVariable(name="code")String code){
        certificationService.checkCode(mail,code);
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",null));
    }
}
