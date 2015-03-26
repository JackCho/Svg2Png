package me.ele.batik;

public enum Density {

    LDPI(0.75f),
    MDPI(1),
    HDPI(1.5f),
    XHDPI(2),
    XXHDPI(3),
    XXXHDPI(4);

    float multiplier;

    Density(float multiplier) {
        this.multiplier = multiplier;
    }

    float getMultiplier() {
        return multiplier;
    }
}
