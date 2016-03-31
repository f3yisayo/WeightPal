package ninja.feyisayo.apps.weightpal;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ExerciseViewHolder> {

    List<Exercise> exercises;

    RVAdapter(List<Exercise> exercises){
        this.exercises = exercises;
    }

    public static class ExerciseViewHolder extends RecyclerView.ViewHolder{

        CardView cv;
        TextView exerciseName;
        TextView exerciseDesc;
        ImageView exerciseImage;


        ExerciseViewHolder(View itemView) {
            super(itemView);

            cv = (CardView)itemView.findViewById(R.id.cv);
            exerciseName = (TextView) itemView.findViewById(R.id.exerciseName);
            exerciseDesc = (TextView) itemView.findViewById(R.id.exerciseDesc);
            exerciseImage = (ImageView) itemView.findViewById(R.id.exerciseImage);

        }
    }

    @Override
    public ExerciseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.card, parent, false);
        ExerciseViewHolder evh = new ExerciseViewHolder(v);
        return evh;
    }

    @Override
    public void onBindViewHolder(ExerciseViewHolder holder, int i) {

        holder.exerciseName.setText(exercises.get(i).name);
        holder.exerciseDesc.setText(exercises.get(i).description);
        holder.exerciseImage.setImageResource(exercises.get(i).imageId);
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
