package com.huyvu.lightmessage.jpa.repo;

import com.huyvu.lightmessage.jpa.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserProfileJpaRepo extends JpaRepository<UserProfile, UUID> {

}
