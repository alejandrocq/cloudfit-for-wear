package com.alejandro_castilla.cloudfitforwear.activities.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alejandro_castilla.cloudfitforwear.R;
import com.alejandro_castilla.cloudfitforwear.cloudfit.exercises.ExerciseGroup;
import com.alejandro_castilla.cloudfitforwear.utilities.Utilities;
import com.blunderer.materialdesignlibrary.views.CardView;

import java.util.ArrayList;

/**
 * Created by alejandrocq on 22/05/16.
 */
public class ExercisesListAdapter extends RecyclerView.Adapter<ExercisesListAdapter.ViewHolder> {

    private final String TAG = ExercisesListAdapter.class.getSimpleName();

    private Activity context;

    private ArrayList<ExerciseGroup> exercises;

    public ExercisesListAdapter(Activity context, ArrayList<ExerciseGroup> exercises) {
        this.context = context;
        this.exercises = exercises;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;

        public ViewHolder (View view) {
            super(view);
            this.cardView = (CardView) view.findViewById(R.id.exercisesCardView);
        }
    }

    public void setExercises(ArrayList<ExerciseGroup> exercises) {
        this.exercises = exercises;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.exercises_cardview, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (exercises != null && exercises.size()>0) {
            Log.d(TAG, "Exercises array size: "+exercises.size());
            holder.cardView.setTitle(exercises.get(position).getTitle());
            holder.cardView.setOnNormalButtonClickListener
                    (new ButtonClickListener(context, exercises.get(position)));
        }
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    private static class ButtonClickListener implements View.OnClickListener {

        private Activity context;
        private ExerciseGroup exerciseGroup;
        private String exerciseDescription = "";

        public ButtonClickListener(Activity context, ExerciseGroup exerciseGroup) {
            this.context = context;
            this.exerciseGroup = exerciseGroup;
            exerciseDescription = Utilities
                    .buildExerciseDescriptionFromExerciseGroup(exerciseGroup);
        }

        @Override
        public void onClick(View v) {
            MaterialDialog.Builder materialDialog = new MaterialDialog.Builder(context)
                    .title(exerciseGroup.getTitle())
                    .content(exerciseDescription)
                    .positiveText("Entendido");
            materialDialog.show();
        }
    }

}
