package com.huyvu.lightmessage.jpa.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;


@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Message extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    Conversation conversation;

    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    Member sender;

    long timestamp;

}
