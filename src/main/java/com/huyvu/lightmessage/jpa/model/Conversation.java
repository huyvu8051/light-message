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
//@Entity
public class Conversation extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;

    String name;
    boolean isGroupChat;

    /*@ManyToMany
    List<Member> members;*/


    /*@OneToMany(mappedBy = "conv")
    List<Message> messages;*/
}
