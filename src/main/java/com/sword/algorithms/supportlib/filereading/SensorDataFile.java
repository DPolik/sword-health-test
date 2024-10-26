package com.sword.algorithms.supportlib.filereading;

import com.sword.algorithms.supportlib.datatypes.Sensor;
import com.sword.algorithms.supportlib.datatypes.SensorData;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class SensorDataFile implements Iterable<Map<Sensor, SensorData>> {

    static final String LINE_DELIMITER = "\n";

    static final String VALUE_DELIMITER = ",";

    enum DataColumns {
        SAMPLE_INDEX,
        SENSOR,
        VEC_X,
        VEC_Y,
        VEC_Z,
        ACC;

        static String getExpectedHeaderRow() {
            return Arrays.stream(DataColumns.values())
                    .map(Enum::toString)
                    .map(String::toLowerCase)
                    .collect(Collectors.joining(VALUE_DELIMITER));
        }
    }

    private final File file;

    private final int nTrackers;

    public SensorDataFile(URL sensorDataFile, int nTrackers) {
        this.file = new File(sensorDataFile.getFile());
        this.nTrackers = nTrackers;
    }

    @Override
    public Iterator<Map<Sensor, SensorData>> iterator() {
        try {
            return new SensorDataIterator();
        } catch (FileNotFoundException e) {
            throw new SensorDataFileException("Sensor data file not found!");
        }
    }

    private class SensorDataIterator implements Iterator<Map<Sensor, SensorData>> {

        final Scanner sensorDataScanner;

        SensorDataIterator() throws FileNotFoundException {
            this.sensorDataScanner = new Scanner(file);
            this.sensorDataScanner.useDelimiter(LINE_DELIMITER);

            String headerRow = sensorDataScanner.next(); // skip header row

            if (!headerRow.equals(
                    DataColumns.getExpectedHeaderRow())) {
                throw new SensorDataFileException(
                        "Sensor data file has wrong format." +
                                "\nExpected header: " +
                                DataColumns.getExpectedHeaderRow());
            }
        }

        @Override
        public boolean hasNext() {
            return sensorDataScanner.hasNext();
        }

        @Override
        public Map<Sensor, SensorData> next() {
            Map<Sensor, SensorData> sensorData = new EnumMap<>(Sensor.class);

            for (int i = 0; i < nTrackers; i++) {
                String[] line = sensorDataScanner.next().split(
                        VALUE_DELIMITER);

                Sensor sensor = Sensor.values()[Integer.parseInt(
                        line[DataColumns.SENSOR.ordinal()]) - 1
                        ];

                float[] vec = new float[]{
                        Float.parseFloat(
                                line[DataColumns.VEC_X.ordinal()]),
                        Float.parseFloat(
                                line[DataColumns.VEC_Y.ordinal()]),
                        Float.parseFloat(
                                line[DataColumns.VEC_Z.ordinal()])
                };

                float acc = Float.parseFloat(
                        line[DataColumns.ACC.ordinal()]);

                sensorData.put(sensor, new SensorData(vec, acc));
            }

            return sensorData;
        }
    }

    private static class SensorDataFileException extends RuntimeException {

        public SensorDataFileException(String message) {
            super(message);
        }

    }
}
