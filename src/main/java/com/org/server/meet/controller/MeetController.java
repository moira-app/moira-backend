package com.org.server.meet.controller;




import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.service.MeetService;
import com.org.server.util.ApiResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
@RequestMapping("/meet")
@RequiredArgsConstructor
public class MeetController {


    private final MeetService meetService;
    @GetMapping("/meetList/{date}")
    public ResponseEntity<ApiResponseUtil<List<MeetDateDto>>> getMeetList(@PathVariable(name="date")
                                                           String date){
        return ResponseEntity.ok(ApiResponseUtil.CreateApiResponse("ok",
                meetService.getMeetList(date)));
    }

}
