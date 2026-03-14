package com.lifeload.server.payload.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventChoiceRequest {
    private Long eventId;
    private int optionIndex;
}
