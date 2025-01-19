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
@Table(indexes = {
        @Index(name = "idx_user_profile__username", columnList = "username")
})
public class UserProfile extends Auditable<String> {
    @Id
    @GeneratedValue
    UUID id;

    @OneToMany(mappedBy = "user")
    List<Member> convMembers;

    @OneToMany(mappedBy = "sender")
    List<Message> messages;

    String name;
    String username;


}
