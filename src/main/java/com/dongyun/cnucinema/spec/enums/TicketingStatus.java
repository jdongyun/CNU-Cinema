package com.dongyun.cnucinema.spec.enums;

public enum TicketingStatus {
    R, // Reserved
    C; // Cancelled

    @Override
    public String toString() {
        return switch (this) {
            case R -> "예약";
            case C -> "취소";
            default -> throw new IllegalArgumentException();
        };
    }
}
