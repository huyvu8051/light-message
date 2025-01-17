package com.huyvu.lightmessage.jpa.model;


import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Embeddable
public class ConvMemberId implements Serializable {
    @ManyToOne(fetch = FetchType.LAZY)
    Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    Conversation conversation;
}
