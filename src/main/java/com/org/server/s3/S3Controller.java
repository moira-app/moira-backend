package com.org.server.s3;


import com.org.server.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
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
@RequiredArgsConstructor
@RequestMapping("/s3")
@Tag(name = "이미지 업로드시 s3 Api",description = "이미지 업로드 및 get에대한 url을받아오는 api입니다.")
public class S3Controller {

    private final S3Service s3Service;


    @Operation(summary = "get url", description = "이미지를 불러오는대 필요한url을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리턴은 url입니다.",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이미 가입하였거나 혹은 존재하는 닉네임입니다",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class))),
    })
    @PostMapping("/GetPreSignUrl")
    public ResponseEntity<ApiResponseUtil<String>> getPreSignUrl(@RequestBody @Valid PreSignDto preSignDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                s3Service.getPreSignUrl(preSignDto.getFileName())));

    }
    @Operation(summary = "post url", description = "이미지를 업로드에 필요한url을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "리턴은 url입니다.",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "이미 가입하였거나 혹은 존재하는 닉네임입니다",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class))),
    })
    @PutMapping("/PutPreSignUrl")
    public ResponseEntity<ApiResponseUtil<String>> postPreSignUrl(@RequestBody @Valid PreSignDto preSignDto){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                s3Service.savePreSignUrl(preSignDto.getContentType(),preSignDto.getFileName()).get(1)));
    }
}
