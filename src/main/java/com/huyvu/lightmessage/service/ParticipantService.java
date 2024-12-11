package com.huyvu.lightmessage.service;

import com.huyvu.lightmessage.dto.MessageDTO;
import com.huyvu.lightmessage.dto.SendMessageRequest;
import com.huyvu.lightmessage.entity.MessageEntity;

import java.util.List;

public interface ParticipantService {
    MessageEntity sendMessage(SendMessageRequest request);
    List<MessageDTO> getMessages(long convId);
}
