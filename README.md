# sword-health-test
Test task for Sword Health

- Assume you have a patient with the sensors placed as in Fig. 1 and facing the North direction while performing a single repetition of the following movement: first lifting the right leg with the knee bent and then lifting the left leg in the exact same fashion, as illustrated in Fig. 3.
- Assume also that you have five continuous real-time streams of sensor data, i.e. for each instant in time (take 50 Hz as a possible rate) you receive one pair of reference vector and acceleration from each sensor, but you do not know on which of the five possible positions - chest, right thigh, left thigh, right shank and left shank - each sensor is placed although you do know that there is one sensor on each of these positions.

- Implement a simplified version of the above within one of the provided helper projects in Python 3 or Java (JDK 8 or newer) whilst complying with the following requirements:
  - Except for the TODO in the main file, all your code must be within a separate package (all existing code files are there to support your implementation and do not need to be changed)
  - Your top class must override SensorPositionFinder and must be instantiated in the mentioned TODO
  - Each time an unknown sensor position is identified, your top class must call the on_sensor_position_found or onSensorPositionFound methods (in Python or Java, respectively) of SensorPositionRequester
  - Once all sensor positions are identified, your main class should call the on_finish or onFinish method (in Python or Java, respectively) of SensorPositionRequester

As you can see, the provided helper project includes code for reading samples from a csv file and injecting them into your top class thereby providing you with a means of trying out your solution.
