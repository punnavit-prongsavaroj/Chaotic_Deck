package com.se.eternalclash2.dto.request;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
@Data
public class RoomCreateReq {
    @Min(3) @Max(6) private int maxPlayers;
}
