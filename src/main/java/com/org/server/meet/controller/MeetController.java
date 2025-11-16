package com.org.server.meet.controller;




import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.service.MeetService;
import com.org.server.util.ApiResponseUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
@Tag(name = "미팅 조회 Api",description = "미팅을 조회하는 api입니다.")
public class MeetController {


    private final MeetService meetService;


    @Operation(summary = "미팅 조회 api", description = "특정 한달동안의 미팅을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공",useReturnTypeSchema = true),
            @ApiResponse(responseCode = "401", description = "권한이 부족합니다.",
                    content = @Content(schema = @Schema(implementation = ApiResponseUtil.class)))
    })
    @Parameter(name = "Authorization",
            description = "요청시 토큰값을 넣어주셔야됩니다.",
            required = true,
            example = "Bearer [tokenvalue]",
            in = ParameterIn.HEADER)
    @Parameter(name = "date",
            description = "미팅을 조회할 년,월을 주시면됩니다.",
            required = true,
            example = "2025-~~~",
            in = ParameterIn.PATH)
    @GetMapping("/meetList/{date}")
    public ResponseEntity<ApiResponseUtil<List<MeetDateDto>>> getMeetList(@PathVariable(name="date")
                                                           String date){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                meetService.getMeetList(date)));
    }

}
