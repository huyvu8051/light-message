package com.huyvu.lightmessage.jpa.model;


import com.huyvu.lightmessage.jpa.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.time.OffsetDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
@Table(indexes = {
        @Index(name = "idx_member__user_id", columnList = "user_id"),
        @Index(name = "idx_member__conv_id", columnList = "conversation_id"),
        @Index(name = "idx_member__conv_id__user_id", columnList = "conversation_id, user_id"),
        @Index(name = "idx_member__user_id__last_send_at", columnList = "user_id, last_send_at desc")
})

public class Member extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_member__user"))
    UserProfile user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_member__conv"))
    Conversation conversation;

    OffsetDateTime lastSendAt;

}
