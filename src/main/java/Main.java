import ApplicationTest.SensorPositionFinderApplicationTest;
import com.sword.algorithms.supportlib.filereading.SensorDataFile;
import com.sword.algorithms.supportlib.sensorpositionfinding.SensorPositionFinder;
import com.sword.algorithms.supportlib.sensorpositionfinding.SensorPositionRequester;
import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;
import com.sword.algorithms.supportlib.datatypes.SensorPosition;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;

public class Main {

    static final URL SENSOR_DATA_FILE_PATH =
            Main.class.getClassLoader().getResource("sensor_data.csv");

    static final int N_TRACKERS = 5;

    static final EnumMap<Sensor, SensorPosition> EXPECTED_POSITIONS =
            new EnumMap<>(Sensor.class);

    static {
        EXPECTED_POSITIONS.put(Sensor.SENSOR_1, SensorPosition.RIGHT_SHANK);
        EXPECTED_POSITIONS.put(Sensor.SENSOR_2, SensorPosition.CHEST);
        EXPECTED_POSITIONS.put(Sensor.SENSOR_3, SensorPosition.LEFT_THIGH);
        EXPECTED_POSITIONS.put(Sensor.SENSOR_4, SensorPosition.LEFT_SHANK);
        EXPECTED_POSITIONS.put(Sensor.SENSOR_5, SensorPosition.RIGHT_THIGH);
    }

    static class SensorPositionFinderTester implements SensorPositionRequester {

        final SensorPositionFinder positionFinder;

        final SensorDataFile sensorDataFile;

        final EnumMap<Sensor, SensorPosition> sensorPositions;

        boolean finished;

        SensorPositionFinderTester() {
            this.positionFinder = new SensorPositionFinderApplicationTest();
            this.positionFinder.setPositionRequester(this);

            this.sensorDataFile = new SensorDataFile(SENSOR_DATA_FILE_PATH, N_TRACKERS);
            this.sensorPositions = new EnumMap<>(Sensor.class);
            this.finished = false;
        }

        void run() {
            for (Map<Sensor, SensorData> sensorData : sensorDataFile) {
                positionFinder.onNewSensorSample(sensorData);

                if (finished) {
                    break;
                }
            }

            if (!finished) {
                System.out.println("EOF reached before onFinish was called");
            }
        }

        @Override
        public void onSensorPositionFound(Sensor sensor, SensorPosition position) {
            System.out.println(sensor + "\'s position identified as " + position);

            sensorPositions.put(sensor, position);
        }

        @Override
        public void onFinish() {
            finished = true;

            if (sensorPositions.equals(EXPECTED_POSITIONS)) {
                System.out.println("All sensor positions correctly identified!");
            } else {
                System.out.println("Sensor positions are not correct!");

                System.out.println("Expected:");
                for (Map.Entry<Sensor, SensorPosition> entry : EXPECTED_POSITIONS.entrySet()) {
                    System.out.println(entry.getKey() + ": " + entry.getValue());
                }

                System.out.println("Actual:");
                for (Sensor sensor : EXPECTED_POSITIONS.keySet()) {
                    System.out.println(sensor + ": " + sensorPositions.get(sensor));
                }
            }
        }
    }

    public static void main(String[] args) {
        new SensorPositionFinderTester().run();
    }
}
