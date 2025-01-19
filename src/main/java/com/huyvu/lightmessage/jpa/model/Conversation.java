package com.huyvu.lightmessage.jpa.model;

import com.huyvu.lightmessage.jpa.Auditable;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.List;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Conversation extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;

    @OneToMany(mappedBy = "conversation")
    List<Member> convMembers;

    @OneToMany(mappedBy = "conv")
    List<Message> messages;

    String name;
    boolean isGroupChat;

}
