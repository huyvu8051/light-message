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


    /*@ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_message_conv"))
    Conversation conv;*/

    String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_message_sender"))
    Member sender;

    long timestamp;

}
