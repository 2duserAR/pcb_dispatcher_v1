package com.yadro.pcbdispatcher.api;

public class PCBExamples {
    public static final String CREATE_SUCCESS = """
            {
              "id": 1,
              "serialNumber": "TEST001",
              "currentStatus": "REGISTRATION",
              "createdAt": "2024-12-01 01:40:03",
              "updatedAt": "2024-12-01 01:40:03"
            }
            """;
            
    public static final String NEXT_STATUS_SUCCESS = """
            {
              "id": 1,
              "serialNumber": "TEST001",
              "currentStatus": "COMPONENT_INSTALLATION",
              "createdAt": "2024-12-01 01:40:03",
              "updatedAt": "2024-12-01 01:45:12"
            }
            """;
            
    public static final String REPAIR_SUCCESS = """
            {
              "id": 1,
              "serialNumber": "TEST001",
              "currentStatus": "REPAIR",
              "createdAt": "2024-12-01 01:40:03",
              "updatedAt": "2024-12-01 01:46:22"
            }
            """;
            
    public static final String HISTORY_SUCCESS = """
            [{
              "id": 1,
              "pcb": {
                "id": 1,
                "serialNumber": "TEST001",
                "currentStatus": "COMPONENT_INSTALLATION",
                "createdAt": "2024-12-01 01:40:03",
                "updatedAt": "2024-12-01 01:45:12"
              },
              "fromStatus": "REGISTRATION",
              "toStatus": "COMPONENT_INSTALLATION",
              "timestamp": "2024-12-01 01:45:12"
            }]
            """;
            
    public static final String INVALID_SERIAL = """
            {
              "message": "Серийный номер должен быть в формате TESTxxx, где xxx - три цифры",
              "status": "BAD_REQUEST",
              "timestamp": "2024-12-01 01:40:03"
            }
            """;
            
    public static final String PCB_NOT_FOUND = """
            {
              "message": "PCB не найдена: id=1",
              "status": "NOT_FOUND",
              "timestamp": "2024-12-01 01:45:12"
            }
            """;
            
    public static final String INVALID_STATUS_FOR_REPAIR = """
            {
              "message": "Перемещение в ремонт возможно только с этапа контроля качества",
              "status": "BAD_REQUEST",
              "timestamp": "2024-12-01 01:46:22"
            }
            """;
} 