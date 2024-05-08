package com.example.demo;

public class InvalidOperatorException extends RuntimeException{

    public InvalidOperatorException() {
        super("Invalid operator, you need to choose one of (+,-,*,/)");
    }
}
