package com.dongyun.cnucinema.spec.enums;

public enum MovieRating {
    ALL,
    _12,
    _15,
    _18;

    @Override
    public String toString() {
        switch(this) {
            case ALL: return "전체 관람가";
            case _12: return "12세 이상 관람가";
            case _15: return "15세 이상 관람가";
            case _18: return "청소년 관람불가";
            default: throw new IllegalArgumentException();
        }
    }
}
