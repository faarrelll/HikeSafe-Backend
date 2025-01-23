package com.haven.app.haven.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class TransactionsStatusRequest {
    private String status;
}
