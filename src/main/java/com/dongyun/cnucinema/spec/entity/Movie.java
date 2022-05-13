package com.dongyun.cnucinema.spec.entity;

import com.dongyun.cnucinema.spec.enums.MovieRating;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@ToString
public class Movie {
    @Setter(AccessLevel.NONE)
    private Long mid;

    private String title;

    private LocalDate openDay;

    private String director;

    private MovieRating rating;

    private int length;

    private List<Actor> actors;

    private List<Schedule> schedules;
}
