package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class RequestsFragment extends ScrollViewFragment {

    private ActivityInterface activityInterface;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        activityInterface = (ActivityInterface) activity;
        super.onAttach(activity);
    }

    @Override
    public int getContentView() {
        return R.layout.fragment_requests;
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
//        new ReplyToRequestTask(getActivity(), ,
//                        Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
//                        requests.get(0).getTrainerid(), StaticReferences.REQUEST_ACCEPT)
//                        .execute();
        Toast.makeText(getActivity(), "Actualizando...", Toast.LENGTH_LONG).show();
    }
}
