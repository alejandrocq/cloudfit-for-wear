package com.alejandro_castilla.cloudfitforwear.activities.adapters;

import android.support.wearable.view.GridPagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.alejandro_castilla.cloudfitforwear.R;

public class TrainingActivityGridPagerAdapter extends GridPagerAdapter {

    private final int[] layoutsIDs = {
            R.id.practiceMainLayout,
            R.id.exerciseInfoLayout,
            R.id.sensorsInfoLayout,
            R.id.practicePauseActionLayout,
            R.id.practiceExitActionLayout};
    private final int NUMBER_OF_ACTIONS = layoutsIDs.length;

    @Override
    public int getRowCount() {
        return 1;
    }

    @Override
    public int getColumnCount(int i) {
        return NUMBER_OF_ACTIONS;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int row, int column) {
        return container.findViewById(layoutsIDs[column]);
    }

    @Override
    public void destroyItem(ViewGroup viewGroup, int i, int i1, Object o) {
        //Nothing to do here
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view.equals(object);
    }
}
