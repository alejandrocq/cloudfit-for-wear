package com.alejandro_castilla.cloudfitforwear.interfaces;

import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;

public interface WearableHandler {
    void saveWearableTraining(WearableTraining tr);
    void showTrainingSentConfirmationAndUpdateData(boolean result);
}
