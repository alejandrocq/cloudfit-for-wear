package com.alejandro_castilla.cloudfitforwear.activities.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.views.CardView;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 24/06/16.
 */
public class TrainingsCompletedFragmentAdapter extends
        RecyclerView.Adapter<TrainingsCompletedFragmentAdapter.ViewHolder> {

    private Activity context;
    private ArrayList<WearableTraining> trainingsCompleted;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder (View view) {
            super(view);
            this.cardView = (CardView) view.findViewById(R.id.trainingsCompletedCardView);
        }
    }

    public TrainingsCompletedFragmentAdapter(Activity context,
                                              ArrayList<WearableTraining> trainingsCompleted) {
        this.context = context;
        this.trainingsCompleted = trainingsCompleted;
    }

    public void setTrainingsCompleted(ArrayList<WearableTraining> trainingsCompleted) {
        this.trainingsCompleted = trainingsCompleted;
        notifyDataSetChanged();
    }

    @Override
    public TrainingsCompletedFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.trainings_completed_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(TrainingsCompletedFragmentAdapter.ViewHolder holder,
                                 int position) {

        if (trainingsCompleted != null && trainingsCompleted.size()>0) {
            holder.cardView.setTitle(trainingsCompleted.get(position).getTitle());
            String date = Utilities.dateOnMillisToString(trainingsCompleted.get(position)
                    .getEndDate());
            String descr = "Completado el "+date;
            holder.cardView.setDescription(descr);
            holder.cardView.setOnNormalButtonClickListener(new ButtonClickListener(context,
                    StaticVariables.NORMAL_BUTTON, trainingsCompleted.get(position)));
            holder.cardView.setOnHighlightButtonClickListener(new ButtonClickListener(context,
                    StaticVariables.HIGHLIGHT_BUTTON, trainingsCompleted.get(position)));
        }

    }

    @Override
    public int getItemCount() {
        return trainingsCompleted.size();
    }

    /**
     * Listener for CardView's buttons.
     */

    private static class ButtonClickListener implements View.OnClickListener {

        private Activity context;
        private short buttonType;
        private WearableTraining training;

        public ButtonClickListener(Activity context , short buttonType,
                                   WearableTraining tr) {
            this.context = context;
            this.buttonType = buttonType;
            this.training = tr;
        }

        @Override
        public void onClick(View v) {
            switch (buttonType) {
                case StaticVariables.NORMAL_BUTTON:
                    //TODO Launch training completed details activity

                    break;
                case StaticVariables.HIGHLIGHT_BUTTON:
                    //TODO Delete training from database
                    break;
            }
        }
    }

}
