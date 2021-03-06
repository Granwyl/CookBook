package id.ac.cookbook.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import id.ac.cookbook.R;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {
    private ArrayList<Recipe> listRecipe;
    private OnItemClickCallback onItemClickCallback;

    public RecipeAdapter(ArrayList<Recipe> listRecipe) {
        this.listRecipe = listRecipe;
    }

    public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback){
        this.onItemClickCallback = onItemClickCallback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = listRecipe.get(position);
        holder.bind(recipe);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickCallback.onItemClicked(recipe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listRecipe.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivLogo;
        TextView tvNama, tvCreator, tvRate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLogo = itemView.findViewById(R.id.ivItemRecipe);
            tvNama = itemView.findViewById(R.id.tvItemRecipeNama);
            tvCreator = itemView.findViewById(R.id.tvItemRecipeCreator);
            tvRate = itemView.findViewById(R.id.tvItemRecipeRating);
        }

        void bind(Recipe recipe){
            tvCreator.setText("created by : " + recipe.getUsernameCreator());
            tvNama.setText(recipe.getNama());
            if (recipe.getKategori().equalsIgnoreCase("food")){
                ivLogo.setImageResource(R.drawable.ic_food);
            }else if (recipe.getKategori().equalsIgnoreCase("beverage")){
                ivLogo.setImageResource(R.drawable.ic_beverage);
            }else if (recipe.getKategori().equalsIgnoreCase("side dish")){
                ivLogo.setImageResource(R.drawable.ic_side_dish);
            }
            tvRate.setText(String.format("%.2f", recipe.getRate()));
        }
    }

    public interface OnItemClickCallback{
        void onItemClicked(Recipe recipe);
    }
}
