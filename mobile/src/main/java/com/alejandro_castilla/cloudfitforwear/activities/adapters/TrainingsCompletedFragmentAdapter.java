package com.alejandro_castilla.cloudfitforwear.activities.adapters;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.activities.TrainingCompletedActivity;
import com.alejandro_castilla.cloudfitforwear.data.WearableTraining;
import com.alejandro_castilla.cloudfitforwear.utilities.StaticVariables;
import com.alejandro_castilla.cloudfitforwear.utilities.TrainingsDb;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.views.CardView;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 24/06/16.
 */
public class TrainingsCompletedFragmentAdapter extends
        RecyclerView.Adapter<TrainingsCompletedFragmentAdapter.ViewHolder> {

    private Activity context;
    private LayoutUpdater updater;
    private ArrayList<WearableTraining> trainingsCompleted;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder (View view) {
            super(view);
            this.cardView = (CardView) view.findViewById(R.id.trainingsCompletedCardView);
        }
    }

    /**
     * Updates the training list and the layout.
     */
    public interface LayoutUpdater {
        void updateLayout();
    }

    public TrainingsCompletedFragmentAdapter(Activity context, LayoutUpdater updater,
                                             ArrayList<WearableTraining> trainingsCompleted) {
        this.context = context;
        this.updater = updater;
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

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrainingsCompletedFragmentAdapter.ViewHolder holder,
                                 int position) {

        if (trainingsCompleted != null && trainingsCompleted.size()>0) {
            holder.cardView.setTitle(trainingsCompleted.get(position).getTitle());
            String date = Utilities.dateOnMillisToString(trainingsCompleted.get(position)
                    .getEndDate());
            String descr = "Completado el " + date;
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

    private class ButtonClickListener implements View.OnClickListener {

        private Activity context;
        private short buttonType;
        private WearableTraining training;

        public ButtonClickListener(Activity context, short buttonType,
                                   WearableTraining tr) {
            this.context = context;
            this.buttonType = buttonType;
            this.training = tr;
        }

        @Override
        public void onClick(View v) {
            switch (buttonType) {
                case StaticVariables.NORMAL_BUTTON:
                    Intent intent = new Intent(context, TrainingCompletedActivity.class);
                    Gson gson = new Gson();
                    intent.putExtra("training_completed", gson.toJson(training));
                    context.startActivity(intent);
                    break;
                case StaticVariables.HIGHLIGHT_BUTTON:
                    String dialogDescr = "¿Está seguro de que quiere eliminar el siguiente " +
                            "entrenamiento: "+training.getTitle()+"?";
                    new MaterialDialog.Builder(context)
                            .title("Atención")
                            .content(dialogDescr)
                            .positiveText("Sí")
                            .negativeText("Cancelar")
                            .titleColorRes(R.color.md_grey_800)
                            .contentColorRes(R.color.md_grey_800)
                            .backgroundColorRes(R.color.md_white_1000)
                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                @Override
                                public void onClick(@NonNull MaterialDialog dialog,
                                                    @NonNull DialogAction which) {
                                    TrainingsDb db = new TrainingsDb(context);
                                    boolean res = db.deleteTraining(training.getTrainingId());

                                    if (res) {
                                        Toast.makeText(context, "Entrenamiento eliminado " +
                                                "correctamente", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(context, "Error al eliminar el " +
                                                "entrenamiento", Toast.LENGTH_LONG).show();
                                    }

                                    updater.updateLayout();
                                }
                            })
                            .show();
                    break;
            }
        }
    }

}
