package com.org.server.security.domain;

import com.org.server.member.GenderType;
import com.org.server.member.MemberType;

public interface OAuth2Response {
    MemberType getProvider();
    String getEmail();
}
