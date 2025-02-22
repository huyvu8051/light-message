package com.huyvu.lightmessage.r2.model;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@org.springframework.data.relational.core.mapping.Table("conversation")
public class Conversation {
    @Id
    long id;
    String name;
    boolean isGroupChat;
}
