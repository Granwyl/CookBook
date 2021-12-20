package id.ac.cookbook.data;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import id.ac.cookbook.R;

public class RateAdapter extends RecyclerView.Adapter<RateAdapter.ViewHolder> {
    private ArrayList<Rate> listRate;

    public RateAdapter(ArrayList<Rate> listRate){
        this.listRate = listRate;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new RateAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Rate rate = listRate.get(position);
        holder.bind(rate);
    }

    @Override
    public int getItemCount() {
        return listRate.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUsername, tvRate, tvResponse;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvItemReviewUsername);
            tvRate = itemView.findViewById(R.id.tvItemReviewRating);
            tvResponse = itemView.findViewById(R.id.tvItemReviewResponse);
        }

        public void bind(Rate rate){
            tvUsername.setText(rate.getUsername());
            tvRate.setText(""+rate.getStar());
            tvResponse.setText(rate.getReview());
        }
    }
}
