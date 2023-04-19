package ru.javawebinar.topjava.util.exception;

public class EmailDuplicationException extends RuntimeException {
    public EmailDuplicationException(String message) {
        super(message);
    }
}