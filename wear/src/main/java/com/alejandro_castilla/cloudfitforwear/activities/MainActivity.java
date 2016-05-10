package com.alejandro_castilla.cloudfitforwear.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.DotsPageIndicator;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.widget.ImageView;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.MainActivityGridPagerAdapter;

public class MainActivity extends WearableActivity {

    private final String TAG = MainActivity.class.getSimpleName();

    private GridViewPager gridViewPager;

    /* Layout Views */
    private ImageView startActionImgView;
    private ImageView settingsImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                startActionImgView = (ImageView) stub.findViewById(R.id.startActionImg);
                startActionImgView.setOnClickListener(new ActionButtonsClicListener());
                settingsImgView = (ImageView) stub.findViewById(R.id.settingsImg);
                settingsImgView.setOnClickListener(new ActionButtonsClicListener());

                gridViewPager = (GridViewPager) stub.findViewById(R.id.pager);
                gridViewPager.setAdapter(new MainActivityGridPagerAdapter(MainActivity.this));
                gridViewPager.setOffscreenPageCount(2);
                DotsPageIndicator dotsPageIndicator = (DotsPageIndicator)
                        findViewById(R.id.page_indicator);
                dotsPageIndicator.setPager(gridViewPager);
            }
        });

        setAmbientEnabled();
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);


    }

    private class ActionButtonsClicListener implements View.OnClickListener {

        private int resId;

        @Override
        public void onClick(View v) {
            resId = v.getId();
            switch (resId) {
                case R.id.startActionImg:
                    Intent startPracticeActivityIntent = new Intent(MainActivity.this,
                            TrainingActivity.class);
                    startActivity(startPracticeActivityIntent);
                    break;
                case R.id.previousPracticesImg:
                    //TODO something here
                    break;
                case R.id.settingsImg:
                    Intent startSettingsActivityIntent = new Intent (MainActivity.this,
                            SettingsActivity.class);
                    startActivity(startSettingsActivityIntent);
                    break;
            }
        }
    }

}
