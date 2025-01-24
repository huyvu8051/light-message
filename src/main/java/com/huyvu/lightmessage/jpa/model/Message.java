package com.huyvu.lightmessage.jpa.model;

import com.huyvu.lightmessage.jpa.Auditable;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.CreationTimestamp;

import java.time.OffsetDateTime;


@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@Entity
@Table(indexes = {
        @Index(name = "idx_message__conv_id", columnList = "conv_id"),
        @Index(name = "idx_message__conv_id__send_at", columnList = "conv_id, send_at desc")
})
@NoArgsConstructor
@AllArgsConstructor
public class Message extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_message__conv"))
    Conversation conv;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_message__sender"))
    UserProfile sender;

    String content;

    OffsetDateTime sendAt;

}
