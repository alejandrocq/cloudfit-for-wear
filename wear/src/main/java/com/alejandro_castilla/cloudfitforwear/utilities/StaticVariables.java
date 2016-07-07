package com.alejandro_castilla.cloudfitforwear.utilities;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class StaticVariables {

    /**
     * Message types
     */

    public static final int TARGET_DEVICE = 1;
    public static final int DEVICE_FOUND = 2;
    public static final int DEVICE_NOT_FOUND = 3;
    public static final int ZEPHYR_HEART_RATE = 4;

    /**
     * Bundle strings
     */



    /**
     * Data Map Request Types
     */

    public static final String TRAINING_FROM_HANDHELD = "/traininghandheld";
    public static final String ACK_FROM_WEARABLE = "/ackwearable";

    /**
     * Data Map Types
     */

    public static final String WEARABLE_TRAINING = "wearabletraining";
    public static final String WEARABLE_TRAINING_ACK = "wearabletrainingack";

    /**
     * Shared Preferences Types
     */

    public static final String KEY_PREF_ZEPHYR_ENABLED = "pref_zephyr_sensor_enabled";
    public static final String KEY_TRAINING_TO_BE_DONE = "training_to_be_done";
    public static final String KEY_TRAINING_DONE = "training_done";


}
