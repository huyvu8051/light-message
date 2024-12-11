package com.huyvu.lightmessage.exception;

public class ConversationNotExistException extends RuntimeException {
    public ConversationNotExistException(String msg) {
        super(msg);
    }
}
