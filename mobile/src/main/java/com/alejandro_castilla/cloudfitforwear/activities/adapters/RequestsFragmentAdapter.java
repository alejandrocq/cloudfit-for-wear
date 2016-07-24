package com.alejandro_castilla.cloudfitforwear.activities.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.asynctask.ReplyToRequestTask;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.RequestTrainer;
import com.alejandro_castilla.cloudfitforwear.cloudfit.models.User;
import com.alejandro_castilla.cloudfitforwear.cloudfit.services.CloudFitService;
import com.alejandro_castilla.cloudfitforwear.cloudfit.utilities.StaticReferences;
import com.alejandro_castilla.cloudfitforwear.interfaces.CloudFitDataHandler;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.blunderer.materialdesignlibrary.views.CardView;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 26/05/16.
 */
public class RequestsFragmentAdapter extends
        RecyclerView.Adapter<RequestsFragmentAdapter.ViewHolder> {

    private final String TAG = RequestsFragmentAdapter.class.getSimpleName();

    private Activity context;
    private ArrayList<RequestTrainer> requests;

    public RequestsFragmentAdapter(Activity context, ArrayList<RequestTrainer> requests) {
        this.context = context;
        this.requests = requests;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder (View view) {
            super(view);
            this.cardView = (CardView) view.findViewById(R.id.requestsCardView);
        }
    }

    public void setRequests(ArrayList<RequestTrainer> requests) {
        this.requests = requests;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.requests_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (requests != null && requests.size()>0) {
            User trainerUser = requests.get(position).getUsertrainerid();
            String trainerDescription = "Nombre: " + trainerUser.getName() + "\n"
                    + "Apellidos: " + trainerUser.getLastname();
            holder.cardView.setDescription(trainerDescription);
            holder.cardView.setOnNormalButtonClickListener(new
                    ButtonClickListener(StaticVariables.NORMAL_BUTTON, position));
            holder.cardView.setOnHighlightButtonClickListener(new
                    ButtonClickListener(StaticVariables.HIGHLIGHT_BUTTON, position));
        }
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    /**
     * Listener for CardView's buttons.
     */

    private class ButtonClickListener implements View.OnClickListener {

        private CloudFitDataHandler cloudFitDataHandler;
        private CloudFitService cloudFitService;
        private int position;
        private short buttonType;

        public ButtonClickListener(short buttonType, int position) {
            this.buttonType = buttonType;
            this.position = position;
            cloudFitDataHandler = (CloudFitDataHandler) context;
            cloudFitService = cloudFitDataHandler.getCloudFitService();
        }

        @Override
        public void onClick(View v) {
            switch (buttonType) {
                case StaticVariables.NORMAL_BUTTON:
                    new ReplyToRequestTask(context, cloudFitService,
                            Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
                            requests.get(position).getTrainerid(), StaticReferences.REQUEST_ACCEPT)
                            .execute();
                    break;
                case StaticVariables.HIGHLIGHT_BUTTON:
                    new ReplyToRequestTask(context, cloudFitService,
                            Long.parseLong(cloudFitService.getFit().getSetting().getUserID()),
                            requests.get(position).getTrainerid(), StaticReferences.REQUEST_CANCEL)
                            .execute();
                    break;
            }
        }
    }
}
