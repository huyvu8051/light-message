package com.huyvu.lightmessage.r2.model;

import jakarta.persistence.Id;
import lombok.Data;

@Data
@org.springframework.data.relational.core.mapping.Table("user_profile")
public class UserProfile {
    @Id
    long id;
    String name;
    String username;
}
