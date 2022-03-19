package com.paywallet.userservice.user.repository;

import com.paywallet.userservice.user.model.notification.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends MongoRepository<Notification, String> {
    Optional<List<Notification>> findByRequestId(String requestId);
}