package com.org.server.ticket.controller;


import com.org.server.ticket.domain.TicketDto;
import com.org.server.ticket.service.TicketService;
import com.org.server.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @GetMapping("/checkIn/{projectId}")
    public ResponseEntity<ApiResponse<Boolean>> checkIn(
            @PathVariable(value = "projectId")Long projectId){
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",
                ticketService.checkIn(projectId)));
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<String>> createTicket(@RequestBody TicketDto ticketDto){
        ticketService.createTicket(ticketDto);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }

    @PostMapping("/change/alias/{projectId}/{alias}")
    public ResponseEntity<ApiResponse<String>> changeAlias(@PathVariable(name = "alias")
                                                           String alias,@PathVariable(name ="projectId")
                                                           Long projectId){
        ticketService.changeAlias(alias,projectId);
        return ResponseEntity.ok(ApiResponse.CreateApiResponse("ok",null));
    }

}
