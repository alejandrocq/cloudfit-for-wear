package com.alejandro_castilla.cloudfitforwear.interfaces;

import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;

/**
 * Created by alejandrocq on 29/06/16.
 */
public interface WearableHandler {

    void saveWearableTraining(WearableTraining tr);
    void showTrainingSentConfirmationAndUpdateData(boolean result);

}
