package com.dongyun.cnucinema.spec.entity;

import lombok.*;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class Actor {
    private Long mid;

    private String name;
}
