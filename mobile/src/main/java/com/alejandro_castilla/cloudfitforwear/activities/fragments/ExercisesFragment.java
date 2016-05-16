package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.R;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;
import com.blunderer.materialdesignlibrary.views.CardView;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class ExercisesFragment extends ScrollViewFragment {

    private CardView cardView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View exercisesFragmentView = inflater.inflate(R.layout.fragment_exercises, container,
                false);

        cardView = (CardView) exercisesFragmentView.findViewById(R.id.cardView);

        cardView.setOnNormalButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity().getApplicationContext(), "Botón de descarga pulsado",
                        Toast.LENGTH_LONG).show();
            }
        });

        return exercisesFragmentView;
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_exercises;
    }

    @Override
    public boolean pullToRefreshEnabled() {
        return true;
    }

    @Override
    public int[] getPullToRefreshColorResources() {
        return new int[]{R.color.mdl_color_primary};
    }

    @Override
    public void onRefresh() {
//        setRefreshing(false);
    }
}