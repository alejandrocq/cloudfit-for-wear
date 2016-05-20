package com.alejandro_castilla.cloudfitforwear.interfaces;


import com.alejandro_castilla.cloudfitforwear.cloudfit.models.CalendarEvent;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 18/05/16.
 */
public interface TaskToFragmentInterface {

    void stopRefreshing();
    void updateTrainingsList(ArrayList<CalendarEvent> calendarEvents);

}
