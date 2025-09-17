package com.org.server.meet.controller;




import com.org.server.meet.domain.MeetConnectDto;
import com.org.server.meet.domain.MeetDateDto;
import com.org.server.meet.domain.MeetDto;
import com.org.server.meet.service.MeetService;
import com.org.server.util.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<MeetDateDto>>> getMeetList(@PathVariable(name="date")
                                                           String date){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                meetService.getMeetList(date)));
    }

}
