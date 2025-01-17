package com.huyvu.lightmessage.jpa.model;

import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@MappedSuperclass
public abstract class Auditable<U> {

    @CreatedBy
    U createdBy;

    @CreatedDate
    Instant createdDate;

    @LastModifiedBy
    U lastModifiedBy;

    @LastModifiedDate
    Instant lastModifiedDate;
}
