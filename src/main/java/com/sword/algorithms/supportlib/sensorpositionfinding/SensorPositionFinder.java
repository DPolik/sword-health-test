package com.sword.algorithms.supportlib.sensorpositionfinding;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;

import java.util.Map;

public interface SensorPositionFinder {

    /**
     * Sets the position requester to be called by this position finder.
     * @param positionRequester position requester that should be called by this
     *                          position finder.
     */
    void setPositionRequester(SensorPositionRequester positionRequester);

    /**
     * Callback called each time a new sample is received from the sensors.
     * @param sensorData a map containing sensor data as values and the corresponding
     *                   sensors as keys
     */
    void onNewSensorSample(Map<Sensor, SensorData> sensorData);

}
