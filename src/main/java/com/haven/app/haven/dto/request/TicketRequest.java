package com.haven.app.haven.dto.request;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class TicketRequest {
    private String hikerName;
    private String identificationType;
    private String identificationNumber;
    private String address;
    private String phoneNumber;
}
