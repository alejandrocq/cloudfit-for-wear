package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class RequestsFragment extends ScrollViewFragment {

    private RequestFragmentInterface requestFragmentInterface;

    public interface RequestFragmentInterface {
        CloudFitService getCloudFitService();
        ArrayList<RequestTrainer> getRequests();
    }

    @Override
    public void onAttach(Activity activity) {
        requestFragmentInterface = (RequestFragmentInterface) activity;
        super.onAttach(activity);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View requestsFragmentView = inflater.inflate(R.layout.fragment_requests, container, false);



        return requestsFragmentView;
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
    }
}
