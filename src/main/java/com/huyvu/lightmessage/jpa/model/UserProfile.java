package com.huyvu.lightmessage.jpa.model;

import com.huyvu.lightmessage.jpa.Auditable;
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
public class UserProfile extends Auditable<String> {
    @Id
    @GeneratedValue
    long id;
    String name;
    String username;


    @OneToMany(mappedBy = "user")
    List<Member> convMembers;

    @OneToMany(mappedBy = "sender")
    List<Message> messages;
}
