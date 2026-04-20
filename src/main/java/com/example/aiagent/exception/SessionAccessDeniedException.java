package com.example.aiagent.exception;

/**
 * 会话访问权限异常。
 */
public class SessionAccessDeniedException extends RuntimeException {

    public SessionAccessDeniedException(String message) {
        super(message);
    }
}
