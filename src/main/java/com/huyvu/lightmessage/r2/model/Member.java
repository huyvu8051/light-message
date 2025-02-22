package com.huyvu.lightmessage.r2.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.OffsetDateTime;

@Data
@org.springframework.data.relational.core.mapping.Table("member")
public class Member {

    @Id
    long id;

    long conversationId;
    long userId;

    OffsetDateTime lastSendAt;

}
