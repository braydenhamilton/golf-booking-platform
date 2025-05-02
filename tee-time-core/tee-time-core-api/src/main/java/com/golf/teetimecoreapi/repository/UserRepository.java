package com.golf.teetimecoreapi.repository;

import com.golf.teetimecoreapi.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByGolfNZMemberId(String memberId);
}
