package com.org.server.exception;


import com.org.server.util.ApiResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ExceptionController {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponseUtil<String>> dtoValidFail(MethodArgumentNotValidException e){
        return new ResponseEntity<>(extractMethodValidError(e),null,
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MoiraException.class)
    public ResponseEntity<ApiResponseUtil<String>> moiraException(MoiraException e){
        return new ResponseEntity<>(ApiResponseUtil.CreateApiResponse(e.getMessage()
                ,null),e.getHttpStatus());
    }


    private ApiResponseUtil<String> extractMethodValidError(MethodArgumentNotValidException e){
        return e.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(x->x.getDefaultMessage())
                .map(x->{
                    log.info("에러메시지:{}",x);
                    return ApiResponseUtil.<String>CreateApiResponse(x,null);
                })
                .orElse(ApiResponseUtil.CreateApiResponse("알수없는 에러",null));
    }
}
