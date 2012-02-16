package com.kfodor.MySensors;

import java.util.ArrayList;

import android.hardware.Sensor;
import android.text.Html;
import android.text.Spanned;

/*
 * SensorInterface
 *
 * This is a simple class intended to make working with
 * Sensors a little easier.
 *
 */
public class SensorInterface {

    public final static String [] delay =
        {"Fastest", "Game", "UI", "Normal"};

    private final static String [] accuracy =
        {"Unreliable", "Low", "Medium", "High"};

    private final static String[] types = { "Unknown", "Accelerometer",
            "Magnetic Field", "Orientation", "Gyroscope", "Light", "Pressure",
            "Temperature", "Proximity", "Gravity", "Linear Acceleration",
            "Rotation Vector", "Relative Humidity", "Ambient Temperature" };

    private final static String[] units = { "?", "m/s&#178;", "&#181;T",
            "&#176;", "rad/s;", "lx", "hPa", "&#176;C", "m", "m/s&#178;",
            "m/s&#178;", "", "%", "&#176;C" };

    private final static int[] icons = { R.drawable.unknown_sensor,
            R.drawable.accelerometer_sensor, R.drawable.magnetic_sensor,
            R.drawable.orientation_sensor, R.drawable.gyroscope_sensor,
            R.drawable.light_sensor, R.drawable.pressure_sensor,
            R.drawable.temp_sensor, R.drawable.proximity_sensor,
            R.drawable.gravity_sensor, R.drawable.linear_acceleration_sensor,
            R.drawable.rotation_vector_sensor, 
            R.drawable.relative_humidity_sensor,
            R.drawable.temp_sensor };

    private final static String unknown = "Unknown";
    //private final static String nan = "nan";

    private Sensor sensor; // sensor this object interfaces to
    private ArrayList<String> labels;

    SensorInterface(Sensor s)
    {
        // Initialize the object
        if(s == null) {
            throw new IllegalArgumentException("Invalid sensor provided!");
        }
        else {

            // Create an array list of labels
            labels = new ArrayList<String>();

            setSensor(s);
        }
    }

    // Initialize the Sensor Interface based on the type
    // of sensor provided.
    private void init()
    {
        // Remove all elements from the array list
        labels.clear();

        // Add labels based on sensor type
        switch(sensor.getType())
        {
            case Sensor.TYPE_LIGHT:
                labels.add(0, "Ambient");
                break;
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_MAGNETIC_FIELD:
            case 9/*Sensor.TYPE_GRAVITY*/: //TODO
            case 10/*Sensor.TYPE_LINEAR_ACCELERATION*/: //TODO
                labels.add(0, "Lateral");
                labels.add(1, "Longitudinal");
                labels.add(2, "Vertical");
                break;
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_ORIENTATION:
                labels.add(0, "X-Axis [Azimuth]");
                labels.add(1, "Y-Axis [Pitch]");
                labels.add(2, "Z-Axis [Roll]");
                break;
            case Sensor.TYPE_PRESSURE:
                labels.add(0, "Pressure");
                break;
            case Sensor.TYPE_PROXIMITY:
                labels.add(0, "Distance");
                break;
            case 13/*Sensor.TYPE_AMBIENT_TEMPERATURE*/: // TODO
            case Sensor.TYPE_TEMPERATURE:
                labels.add(0, "Temperature");
                break;
            case 11/*Sensor.TYPE_ROTATION_VECTOR*/: //TODO
                labels.add(0, "X-Axis");
                labels.add(1, "Y-Axis");
                labels.add(2, "Z-Axis");
                labels.add(3, "Optional");
                break;
            case 12/*sensor.TYPE_RELATIVE_HUMIDITY*/: //TODO
                labels.add(0, "Humidity");
                break;
            default:
                labels.add(0, "Unknown ");
                break;
        }
    }

    public void setSensor(Sensor s) {
        sensor = s; // assign sensor
        init(); // initialize object
        return;
    }

    public final Sensor sensor() {
        return sensor;
    }

    public final String getType() {
        int type = sensor.getType();
        // Make sure request is within bounds
        if((type < 0) || (type >= types.length)) type = 0;
        return types[type];
    }

    public final static String getType(int type) {
        // Make sure request is within bounds
        if((type < 0) || (type >= types.length)) type = 0;
        return types[type];
    }

    public final Spanned getUnits() {
        int type = sensor.getType();
        // Make sure request is within bounds
        if((type < 0) || (type >= units.length)) type = 0;
        return Html.fromHtml(units[type]);
    }

    public final int getNumValues() {
        return labels.size();
    }

    public final int getIcon() {
        int type = sensor.getType();
        // Make sure request is within bounds
        if((type < 0) || (type >= icons.length)) type = 0;
        return icons[type];
    }

    public final static int getIcon(int type) {
        // Make sure request is within bounds
        if((type < 0) || (type >= icons.length)) type = 0;
        return icons[type];
    }

    public final String getLabel(int index) {

        // Make sure request is within bounds
        if((index < 0) || (index >= labels.size()))
            return unknown;
        else
            return labels.get(index);
    }

    public final static String accuracyToString(int a) {

        // Make sure request is within bounds
        if((a < 0) || (a >= accuracy.length))
            return unknown;
        else
            return accuracy[a];
    }

    public final static String delayToString(int d) {

        // Make sure request is within bounds
        if((d < 0) || (d >= delay.length))
            return unknown;
        else
            return delay[d];
    }

}
