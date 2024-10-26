package com.sword.algorithms.supportlib.sensorpositionfinding;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorPosition;

/**
 * Base class for the callbacks SensorPositionFinder uses to signal the identification
 * of sensor positions.
 */
public interface SensorPositionRequester {

    /**
     * Callback called when a position of a given sensor is identified.
     * @param sensor the sensor whose position was identified
     * @param position the position of that sensor
     */
    void onSensorPositionFound(Sensor sensor, SensorPosition position);

    /**
     * Callback called when all sensor positions were identified.
     */
    void onFinish();

}
