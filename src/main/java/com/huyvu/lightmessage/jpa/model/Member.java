package com.huyvu.lightmessage.jpa.model;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.FieldDefaults;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Entity
public class Member extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;
    String name;
    String username;

    /*@ManyToMany
    @JoinTable
    List<Conversation> conversations;*/

    @OneToMany(mappedBy = "sender")
    List<Message> messages;
}
