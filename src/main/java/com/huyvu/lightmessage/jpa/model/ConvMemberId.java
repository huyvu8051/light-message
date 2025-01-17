package com.huyvu.lightmessage.jpa.model;


import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Embeddable
public class ConvMemberId implements Serializable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="mem.id")
    Member mem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="conv.id")
    Conversation conv;
}
