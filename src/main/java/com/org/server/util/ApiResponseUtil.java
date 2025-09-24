package com.org.server.util;

import lombok.Getter;


//exepction 컨트롤러에서 valid어노테이션 에러 캐치시에ㅜgetter가없으니까 에러가뜬다 왜일까...?

@Getter
public class ApiResponseUtil<T>{

    private String msg;
    private T data;

    public ApiResponseUtil(String msg, T data) {
        this.msg = msg;
        this.data = data;
    }

    public static <T> ApiResponseUtil<T> CreateApiResponse(String msg, T data){
        return new ApiResponseUtil<T>(msg,data);
    }
}
