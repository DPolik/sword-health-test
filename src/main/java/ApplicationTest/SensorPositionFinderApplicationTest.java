package ApplicationTest;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;
import com.sword.algorithms.supportlib.datatypes.SensorPosition;
import com.sword.algorithms.supportlib.sensorpositionfinding.SensorPositionFinder;
import com.sword.algorithms.supportlib.sensorpositionfinding.SensorPositionRequester;

import java.util.EnumMap;
import java.util.Map;

public class SensorPositionFinderApplicationTest implements SensorPositionFinder
{

    private SensorPositionRequester positionRequester;
    private ISensorDataCleaner sensorDataCleaner;

    // Threshold value for considering a meaningful acceleration
    private float accelThreshold = 3f;
    // Threshold value for considering a similar direction for the thigh exercise
    private float thighDirectionDotThreshold = 0.8f;
    // Threshold value for considering a similar direction for the shank exercise
    private float shankDirectionDotThreshold = 0.95f;

    // Internal cached list of sensors found for helping with the algorithm
    private final EnumMap<Sensor, SensorPosition> sensorsFound = new EnumMap<>(Sensor.class);
    // Intended direction of the thigh sensor for the exercise
    private final float[] thighExerciseDirection = new float[]{1, 0, 0};
    // Intended direction of the shank sensor for the exercise
    private final float[] shankExerciseDirection = new float[]{0, 0, -1};

    public SensorPositionFinderApplicationTest()
    {
        sensorDataCleaner = new SensorDataAverager();
    }

    public SensorPositionFinderApplicationTest(float accelThreshold, float thighDirectionDotThreshold, float shankDirectionDotThreshold)
    {
        sensorDataCleaner = new SensorDataAverager();
        this.accelThreshold = accelThreshold;
        this.thighDirectionDotThreshold = thighDirectionDotThreshold;
        this.shankDirectionDotThreshold = shankDirectionDotThreshold;
    }

    @Override
    public void setPositionRequester(SensorPositionRequester positionRequester)
    {
        this.positionRequester = positionRequester;
    }

    @Override
    public void onNewSensorSample(Map<Sensor, SensorData> sensorData)
    {
        // Add sample to the cleaner
        sensorDataCleaner.AddDirtySensorData(sensorData);
        if(!sensorDataCleaner.IsReadyToClean())
        {
            return;
        }

        // Clean samples added (average their values)
        Map<Sensor, SensorData> cleanedData = sensorDataCleaner.CleanSensorData();

        for (Sensor sensor : cleanedData.keySet())
        {
            // Skip rechecks
            if(sensorsFound.containsKey(sensor))
            {
                continue;
            }

            SensorData data = cleanedData.get(sensor);
            // 1- Check if there was a significant acceleration value
            if(data.getAcc() < accelThreshold)
            {
                continue;
            }

            // 2- Check which side (left/right) we are considering
            // (this exercise should start with the right leg)
            boolean isRightSide = !PositionWasAlreadyIdentified(SensorPosition.RIGHT_THIGH) ||
                    !PositionWasAlreadyIdentified(SensorPosition.RIGHT_SHANK);

            // 3- Check if the sensor is in the thigh first
            // 3a- Check if the direction is similar to the exercise (1, 0, 0)
            float thighDotProduct = GetVectorDotProduct(thighExerciseDirection, data.getVec());
            if(thighDotProduct >= thighDirectionDotThreshold)
            {
                // 3b- Probable thigh, print error message if already discovered
                SensorPosition sensorPosition = isRightSide ? SensorPosition.RIGHT_THIGH : SensorPosition.LEFT_THIGH;
                if(PositionWasAlreadyIdentified(sensorPosition))
                {
                    System.out.println("Sensor position " + sensorPosition + " was already identified");
                }
                else
                {
                    sensorsFound.put(sensor, sensorPosition);
                    positionRequester.onSensorPositionFound(sensor, sensorPosition);
                }
                continue;
            }

            // 4- Only check shanks if we are done with thighs
            // (starting directions of thighs are too close to the intended exercise direction of shanks)
            if(isRightSide && !PositionWasAlreadyIdentified(SensorPosition.RIGHT_THIGH) ||
            !isRightSide && !PositionWasAlreadyIdentified(SensorPosition.LEFT_THIGH))
            {
                continue;
            }

            // 3- Check if the sensor is in the shank
            // 3a- Check if the direction is similar to the exercise (0, 0, -1)
            float shankDotProduct = GetVectorDotProduct(shankExerciseDirection, data.getVec());
            if(shankDotProduct >= shankDirectionDotThreshold)
            {
                // Probable shank, print error message if already discovered
                SensorPosition sensorPosition = isRightSide ? SensorPosition.RIGHT_SHANK : SensorPosition.LEFT_SHANK;
                if(PositionWasAlreadyIdentified(sensorPosition))
                {
                    System.out.println("Sensor position " + sensorPosition + " was already identified");
                }
                else
                {
                    sensorsFound.put(sensor, sensorPosition);
                    positionRequester.onSensorPositionFound(sensor, sensorPosition);
                }
            }
        }

        // 5- Handle chest as the last sensor to be found
        if(sensorsFound.keySet().size() == 4)
        {
            for (Sensor sensor : cleanedData.keySet())
            {
                // Skip rechecks
                if(sensorsFound.containsKey(sensor))
                {
                    continue;
                }

                sensorsFound.put(sensor, SensorPosition.CHEST);
                positionRequester.onSensorPositionFound(sensor, SensorPosition.CHEST);
                positionRequester.onFinish();
            }
        }
    }

    private boolean PositionWasAlreadyIdentified(SensorPosition sensorPosition)
    {
        for (Sensor sensor : sensorsFound.keySet())
        {
            if(sensorsFound.get(sensor) == sensorPosition)
            {
                return true;
            }
        }
        return false;
    }

    private float GetVectorDotProduct(float[] vec1, float[] vec2)
    {
        return vec1[0] * vec2[0] + vec1[1] * vec2[1] + vec1[2] * vec2[2];
    }
}
