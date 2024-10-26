package ApplicationTest;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;

import java.util.EnumMap;
import java.util.ArrayList;
import java.util.Map;

public class SensorDataAverager implements ISensorDataCleaner{

    private final ArrayList<Map<Sensor, SensorData>> dirtySensorDataArray;
    private final int maxDataToAverage = 5;

    @Override
    public boolean IsReadyToClean() {return dirtySensorDataArray.size() >= maxDataToAverage;}

    public SensorDataAverager()
    {
        dirtySensorDataArray = new ArrayList<>(maxDataToAverage);
    }

    @Override
    public boolean AddDirtySensorData(Map<Sensor, SensorData> sensorData)
    {
        if(IsReadyToClean())
        {
            return false;
        }

        dirtySensorDataArray.add(sensorData);
        return true;
    }

    @Override
    public Map<Sensor, SensorData> CleanSensorData()
    {
        if(!IsReadyToClean())
        {
            return null;
        }

        Map<Sensor, SensorData> sensorDataSum = new EnumMap<>(Sensor.class);

        for (Map<Sensor, SensorData> sensorData : dirtySensorDataArray)
        {
            if(sensorDataSum.isEmpty())
            {
                sensorDataSum = sensorData;
                continue;
            }

            // Sum each sensor's vec and acc
            for (Sensor sensor: sensorData.keySet())
            {
                float[] vecSum = FloatArraySum(sensorDataSum.get(sensor).getVec(), sensorData.get(sensor).getVec());
                float accSum = sensorDataSum.get(sensor).getAcc() + sensorData.get(sensor).getAcc();
                SensorData dataSum = new SensorData(vecSum, accSum);
                sensorDataSum.put(sensor, dataSum);
            }
        }

        Map<Sensor, SensorData> sensorDataAveraged = new EnumMap<>(Sensor.class);

        // Average each sensor's vec and acc by the number of entries
        for (Sensor sensor: sensorDataSum.keySet())
        {
            SensorData data = sensorDataSum.get(sensor);
            float[] vecAverage = new float[]
                    {
                            data.getVec()[0] / maxDataToAverage,
                            data.getVec()[1] / maxDataToAverage,
                            data.getVec()[2] / maxDataToAverage
                    };
            float accAverage = data.getAcc() / maxDataToAverage;
            SensorData dataAveraged = new SensorData(vecAverage, accAverage);
            sensorDataAveraged.put(sensor, dataAveraged);
        }

        // Reset the internal data
        dirtySensorDataArray.clear();

        return sensorDataAveraged;
    }

    // Sums the values of 2 float[] and returns the new float[]
    private float[] FloatArraySum(float[] a, float[] b)
    {
        if(a.length != b.length)
        {
            return null;
        }

        float[] result = new float[a.length];

        for (int i = 0; i < a.length; i++)
        {
            result[i] = a[i] + b[i];
        }

        return result;
    }
}
