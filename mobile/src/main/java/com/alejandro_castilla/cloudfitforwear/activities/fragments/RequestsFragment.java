package com.alejandro_castilla.cloudfitforwear.activities.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.adapters.RequestsFragmentAdapter;
import com.alejandro_castilla.cloudfitforwear.asynctask.GetRequestsTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.interfaces.ActivityInterface;
import com.blunderer.materialdesignlibrary.fragments.ScrollViewFragment;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 17/05/16.
 */
public class RequestsFragment extends ScrollViewFragment {

    private ActivityInterface activityInterface;

    private RecyclerView recyclerView;
    private RequestsFragmentAdapter requestsFragmentAdapter;

    private ArrayList<RequestTrainer> requests = new ArrayList<>();

    public void setRequests(ArrayList<RequestTrainer> requests) {
        setRefreshing(false);
        this.requests = requests;
        if (requestsFragmentAdapter != null) requestsFragmentAdapter.setRequests(requests);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        requestsFragmentAdapter =
                new RequestsFragmentAdapter(getActivity(), requests);
        recyclerView = (RecyclerView) view.findViewById(R.id.reqRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(requestsFragmentAdapter);
        recyclerView.setHasFixedSize(true);

        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        activityInterface = (ActivityInterface) activity;
        super.onAttach(activity);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestsFragmentAdapter.setRequests(requests);
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
        new GetRequestsTask(getActivity(), activityInterface.getCloudFitService()).execute();
    }
}
