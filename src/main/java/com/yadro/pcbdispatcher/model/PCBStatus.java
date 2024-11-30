package com.yadro.pcbdispatcher.model;

public enum PCBStatus {
    REGISTRATION("Регистрация"),
    COMPONENT_INSTALLATION("Установка компонентов"),
    QUALITY_CONTROL("Контроль качества"),
    REPAIR("Ремонт"),
    PACKAGING("Упаковывание");

    private final String description;

    PCBStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 