package ApplicationTest;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;

import java.util.Map;

public interface ISensorDataCleaner {
    boolean AddDirtySensorData(Map<Sensor, SensorData> sensorData);
    Map<Sensor, SensorData> CleanSensorData();
    boolean IsReadyToClean();
}
