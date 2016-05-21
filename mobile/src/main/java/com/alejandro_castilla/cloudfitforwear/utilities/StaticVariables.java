package com.alejandro_castilla.cloudfitforwear.utilities;

import com.alejandro_castilla.cloudfitforwear.activities.adapters.TrainingsFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetTrainingsTask;

/**
 * Created by alejandrocq on 14/05/16.
 */
public class StaticVariables {

    /**
     * Variables for {@link GetTrainingsTask}
     */

    public static final short GET_ALL_TRAININGS = 1;
    public static final short GET_SINGLE_TRAINING = 2;
    public static final short GET_TRAINING_NOT_DONE = 3;
    public static final short GET_TRAINING_DONE = 4;

    /**
     * Variables for click listener on {@link TrainingsFragmentAdapter}
     */

    public static final short NORMAL_BUTTON = 1;
    public static final short HIGHLIGHT_BUTTON = 2;

}
