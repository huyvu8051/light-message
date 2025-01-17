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
@Table(indexes = {@Index(columnList = "mem.id, conv.id")})
public class ConvMember extends Auditable<String> {

    @EmbeddedId
    ConvMemberId id;

    boolean isEnabled;

}
