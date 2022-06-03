package com.dongyun.cnucinema.spec.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@Getter
@ToString
public class MovieRankStatsVo {

    private int rank;

    private String movieTitle;

    private int seats;
}
