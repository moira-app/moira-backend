package com.org.server.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = 1791407140L;

    public static final QMember member = new QMember("member1");

    public final com.org.server.util.QBaseTime _super = new com.org.server.util.QBaseTime(this);

    public final NumberPath<Integer> birthDay = createNumber("birthDay", Integer.class);

    public final NumberPath<Integer> birthMonth = createNumber("birthMonth", Integer.class);

    public final NumberPath<Integer> birthYear = createNumber("birthYear", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final StringPath email = createString("email");

    public final EnumPath<com.org.server.member.GenderType> genderType = createEnum("genderType", com.org.server.member.GenderType.class);

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final EnumPath<com.org.server.member.MemberType> memberType = createEnum("memberType", com.org.server.member.MemberType.class);

    public final StringPath nickName = createString("nickName");

    public final StringPath password = createString("password");

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}

