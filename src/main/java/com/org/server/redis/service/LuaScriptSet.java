package com.org.server.redis.service;


public class LuaScriptSet {
    public final static String userInfoSetScript="--유저 정보 설정\n" +
            "redis.call(\"set\", KEYS[1], ARGV[1], \"NX\")\n" +
            "--유저 refreshkey 설정\n" +
            "redis.call(\"set\", KEYS[2], ARGV[2], \"NX\", \"EX\", ARGV[3])\n" +
            "\n";
    public final static String userInfoDelScript="--KEY값들은 순서대로 회원정보키,리프레쉬토큰키,티켓키,STOMPSESSION KEY,project key이다.\n" +
            "for i=1,4 do\n" +
            "redis.call(\"del\", KEYS[i])\n"+
            "end\n"+
            "for i=5, #KEYS do\n"+
            "redis.call(\"SREM\",KEYS[i],ARGV[1])\n"+
            "end";


    public final static String userLogOutScript="--KEY값들은 순서대로 회원정보키,리프레쉬토큰키,stompsession 키 이다.\n" +
            "redis.call(\"del\", KEYS[1], KEYS[2], KEYS[3])\n";




    public final static String checkCertKeyScript="local certCode=redis.call(\"get\",KEYS[1])\n" +
            "if certCode==nil or certCode~=ARGV[1] then\n" +
            "    return 0 \n" +
            "else\n" +
            "    redis.call(\"del\",KEYS[1])\n" +
            "    return 1 \n" +
            "end";
    public final static String checkStompSessionExistScript="local exists = redis.call(\"EXISTS\", KEYS[1])\n" +
            "\n" +
            "if exists==0 then\n" +
            "    redis.call(\"SADD\",KEYS[1],ARGV[1])\n" +
            "    return 1\n" +
            "else\n" +
            "    return 0  \n" +
            "end";
}
