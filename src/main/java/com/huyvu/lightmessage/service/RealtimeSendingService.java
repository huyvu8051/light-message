package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.entity.MessageKafkaDTO;

public interface RealtimeSendingService {
    void sendMessageNotification(long convId, MessageKafkaDTO entity);
}
