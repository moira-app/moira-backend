package com.org.server.ticket.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class TicketMetaDto {

    private Long projectId;
    private Long chatRoomId;

}
