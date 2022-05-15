package com.dongyun.cnucinema.spec.enums;

public enum MovieRating {
    ALL(0),
    _12(12),
    _15(15),
    _18(18);

    private final int value;
    MovieRating(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

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
