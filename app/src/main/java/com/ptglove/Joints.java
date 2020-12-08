package com.ptglove;

public enum Joints{
    THUMB_UPPER,
    THUMB_KNUCKLE,
    INDEX_UPPER,
    INDEX_MIDDLE,
    INDEX_KNUCKLE,
    MIDDLE_UPPER,
    MIDDLE_MIDDLE,
    MIDDLE_KNUCKLE,
    RING_UPPER,
    RING_MIDDLE,
    RING_KNUCKLE,
    PINKIE_UPPER,
    PINKIE_MIDDLE,
    PINKIE_KNUCKLE,
    WRIST_F_B;

    public static int length() {
        return values().length;
    }
}