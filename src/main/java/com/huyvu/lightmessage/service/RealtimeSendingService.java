package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;

import java.util.UUID;

public interface RealtimeSendingService {
    void sendMessageNotification(UUID convId, MessageEntity entity);
}
