package com.yadro.pcbdispatcher.exception;

public class PCBNotFoundException extends RuntimeException {
    public PCBNotFoundException(Long id) {
        super("PCB не найдена: id=" + id);
    }
} 