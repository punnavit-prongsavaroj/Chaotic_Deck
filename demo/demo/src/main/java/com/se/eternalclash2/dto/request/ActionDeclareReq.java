package com.se.eternalclash2.dto.request;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
@Data
public class ActionDeclareReq {
    @NotBlank private String actionType;
    private String targetPlayerId;
}
