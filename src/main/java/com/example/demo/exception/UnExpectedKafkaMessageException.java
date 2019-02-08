package com.example.demo.exception;

public class UnExpectedKafkaMessageException extends Exception {
    public UnExpectedKafkaMessageException(){}

    public UnExpectedKafkaMessageException(String message){
        super(message);
    }
}
