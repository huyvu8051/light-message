package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.ConversationEntity;
import com.huyvu.lightmessage.entity.MessageEntity;

public interface RealtimeMessagingService {
    void sendMessageNotification(ConversationEntity convId, MessageEntity entity);
}
