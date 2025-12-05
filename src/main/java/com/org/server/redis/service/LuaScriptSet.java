package com.org.server.redis.service;


public class LuaScriptSet {
    public final static String userInfoSetScript="--유저 정보 설정\n" +
            "redis.call(\"set\", KEYS[1], ARGV[1], \"NX\")\n" +
            "--유저 refreshkey 설정\n" +
            "redis.call(\"set\", KEYS[2], ARGV[2], \"NX\", \"EX\", ARGV[3])\n" +
            "\n";
    public final static String userInfoDelScript="--KEY값들은 순서대로 회원정보키,리프레쉬토큰키,티켓키 이다.\n" +
            "if KEYS[3] ~= nil then\n" +
            "    -- 3번이 있으면 -> 1, 2, 3 다 지운다\n" +
            "    redis.call(\"del\", KEYS[1], KEYS[2], KEYS[3])\n" +
            "else\n" +
            "    -- 3번이 없으면 -> 1, 2만 지운다\n" +
            "    redis.call(\"del\", KEYS[1], KEYS[2])\n" +
            "end";

    public final static String checkCertKeyScript="local certCode=redis.call(\"get\",KEYS[1])\n" +
            "if certCode==nil or certCode~=ARGV[1] then\n" +
            "    return 0 \n" +
            "else\n" +
            "    redis.call(\"del\",KEYS[1])\n" +
            "    return 1 \n" +
            "end";
}
