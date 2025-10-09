package com.org.server.security.filters;
import com.org.server.ticket.service.TicketService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.org.server.redis.service.RedisUserInfoService;
import java.io.IOException;
import com.org.server.member.domain.Member;
import com.org.server.security.domain.CustomUserDetail;
import com.org.server.exception.MoiraException;

@Slf4j
public class ProjectTicketFilter extends OncePerRequestFilter{


    private RedisUserInfoService redisUserInfoService;

    private  TicketService ticketService;
    public ProjectTicketFilter(RedisUserInfoService redisUserInfoService,
                               TicketService ticketService) {
        this.redisUserInfoService=redisUserInfoService;
        this.ticketService = ticketService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String reqUri=request.getRequestURI();
        return !reqUri.startsWith("/enter") || reqUri.equals("/enter/list")
                ||!reqUri.startsWith("/s3/project") || reqUri.startsWith("/enter/project");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String reqUri=request.getRequestURI();
        String [] arr=reqUri.split("/");
        CustomUserDetail customUserDetail= (CustomUserDetail) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Member m=customUserDetail.getMember();
        if(!redisUserInfoService.checkTicketKey(String.valueOf(m.getId()),arr[2])){
            if(!ticketService.checkIn(Long.parseLong(arr[2]),m.getId())){
                sendErrorResponse(response,HttpStatus.BAD_REQUEST,
                        "해당 프로젝트에 대한 권한이없습니다");
                return;
            }
            else{
                redisUserInfoService.setTicketKey(String.valueOf(m.getId()),arr[arr.length-2]);
            }
        };
        filterChain.doFilter(request,response);
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatus httpStatus, String message) throws
            IOException {
        response.setStatus(httpStatus.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(String.format("{\"message\":\"%s\",\"status\":\"%s\"}", message, httpStatus.name()));
    }
}
