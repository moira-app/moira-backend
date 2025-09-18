package com.org.server.ticket.domain;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TicketDto {
    private String email;
    private String alias;
}
