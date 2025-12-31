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
import com.org.server.redis.service.RedisIntegralService;
import java.io.IOException;
import com.org.server.member.domain.Member;
import com.org.server.security.domain.CustomUserDetail;

@Slf4j
public class ProjectTicketFilter extends OncePerRequestFilter{


    private RedisIntegralService redisUserInfoService;

    private  TicketService ticketService;
    public ProjectTicketFilter(RedisIntegralService redisUserInfoService,
                               TicketService ticketService) {
        this.redisUserInfoService=redisUserInfoService;
        this.ticketService = ticketService;
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String reqUri=request.getRequestURI();
        if(reqUri.equals("/enter/list")||reqUri.startsWith("/enter/project")){
            return true;
        }
        return !(reqUri.startsWith("/enter"));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        log.info("티켓 필터 작동");
        String reqUri=request.getRequestURI();
        String [] arr=reqUri.split("/");
        CustomUserDetail customUserDetail= (CustomUserDetail) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Member m=customUserDetail.getMember();
        if(!redisUserInfoService.checkTicketKey(arr[2],String.valueOf(m.getId()))){
            if(!ticketService.checkIn(Long.parseLong(arr[2]),m.getId())){
                sendErrorResponse(response,HttpStatus.BAD_REQUEST,
                        "해당 프로젝트에 대한 권한이없습니다");
                return;
            }
            else{
                redisUserInfoService.setTicketKey(arr[arr.length-2],String.valueOf(m.getId()));
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
