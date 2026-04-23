package com.lifeload.server.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActionRequest {
    private String actionId; // e.g. "WORK", "STUDY"
}
