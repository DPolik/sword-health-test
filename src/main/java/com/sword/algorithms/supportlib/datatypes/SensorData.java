package com.sword.algorithms.supportlib.datatypes;

import java.util.Arrays;

public class SensorData {

    private final float[] vec;

    private final float acc;

    public SensorData(float[] vec, float acc) {
        this.vec = vec.clone();
        this.acc = acc;
    }

    public float[] getVec() {
        return vec.clone();
    }

    public float getAcc() {
        return acc;
    }

    @Override
    public String toString() {
        return "vec: " + Arrays.toString(vec) + " acc: " + acc;
    }
}
