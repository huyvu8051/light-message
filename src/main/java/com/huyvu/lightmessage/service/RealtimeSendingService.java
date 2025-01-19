package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.MessageEntity;

public interface RealtimeSendingService {
    void sendMessageNotification(long convId, MessageEntity entity);
}
