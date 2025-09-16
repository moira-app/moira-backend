package com.org.server.meet.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMeet is a Querydsl query type for Meet
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMeet extends EntityPathBase<Meet> {

    private static final long serialVersionUID = 1769737086L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMeet meet = new QMeet("meet");

    public final com.org.server.util.QBaseTime _super = new com.org.server.util.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createDate = _super.createDate;

    public final NumberPath<Long> id = createNumber("id", Long.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> lastModifiedDate = _super.lastModifiedDate;

    public final StringPath meetUrl = createString("meetUrl");

    public final com.org.server.project.domain.QProject project;

    public final DateTimePath<java.time.LocalDateTime> startTime = createDateTime("startTime", java.time.LocalDateTime.class);

    public QMeet(String variable) {
        this(Meet.class, forVariable(variable), INITS);
    }

    public QMeet(Path<? extends Meet> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMeet(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMeet(PathMetadata metadata, PathInits inits) {
        this(Meet.class, metadata, inits);
    }

    public QMeet(Class<? extends Meet> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.project = inits.isInitialized("project") ? new com.org.server.project.domain.QProject(forProperty("project")) : null;
    }

}

