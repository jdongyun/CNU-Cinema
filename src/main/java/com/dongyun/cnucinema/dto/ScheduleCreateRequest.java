package com.dongyun.cnucinema.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@ToString
public class ScheduleCreateRequest {
    @NotNull
    private Long mid;

    @NotNull
    @Size(min = 1, max = 20)
    private String tname;

    @NotNull
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime showAt;
}
