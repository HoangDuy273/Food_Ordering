package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.food_ordering.R;

import java.util.List;

import model.Food;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.FoodViewHolder> {
    private Context context;
    private List<Food> foodList;

    public FoodAdapter(Context context, List<Food> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_food, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        Food food = foodList.get(position);

        holder.textFoodName.setText(food.getName());
        holder.textPrice.setText(food.getPrice());
        holder.textTime.setText(food.getTime());
        holder.textRating.setText(String.valueOf(food.getRating()));
        holder.imageFood.setImageResource(food.getImageResource());

        holder.buttonAdd.setOnClickListener(v -> {
            // Handle add to cart action
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {
        TextView textFoodName, textPrice, textTime, textRating;
        ImageView imageFood, buttonAdd;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            textPrice = itemView.findViewById(R.id.textPrice);
            textTime = itemView.findViewById(R.id.textTime);
            textRating = itemView.findViewById(R.id.textRating);
            imageFood = itemView.findViewById(R.id.imageFood);
            buttonAdd = itemView.findViewById(R.id.buttonAdd);
        }
    }
}