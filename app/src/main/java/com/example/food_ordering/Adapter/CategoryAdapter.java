package com.example.food_ordering.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.food_ordering.Activity.ListFoodsActivity;
import com.example.food_ordering.R;
import com.example.food_ordering.Domain.Category;

import java.util.List;

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private List<Category> categoryList;
    private LayoutInflater inflater;

    public CategoryAdapter(Context context, List<Category> categoryList) {
        this.context = context;
        this.categoryList = categoryList;
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return categoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return categoryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
            holder = new ViewHolder();
            holder.categoryBackground = convertView.findViewById(R.id.categoryBackground);
            holder.textCategoryIcon = convertView.findViewById(R.id.textCategoryIcon);
            holder.imageCategoryIcon = convertView.findViewById(R.id.imageCategoryIcon);
            holder.textCategoryName = convertView.findViewById(R.id.textCategoryName);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Category category = categoryList.get(position);

        // Set background color
        holder.categoryBackground.setBackgroundColor(category.getBackgroundColor());

        // Set category name - Đảm bảo luôn hiển thị
        if (category.getName() != null && !category.getName().isEmpty()) {
            holder.textCategoryName.setText(category.getName());
            holder.textCategoryName.setVisibility(View.VISIBLE);
        } else {
            holder.textCategoryName.setText("Category");
            holder.textCategoryName.setVisibility(View.VISIBLE);
        }

        // Xử lý hiển thị icon hoặc drawable
        if (category.hasDrawable()) {
            // Hiển thị drawable
            holder.imageCategoryIcon.setImageResource(category.getDrawableRes());
            holder.imageCategoryIcon.setVisibility(View.VISIBLE);
            holder.textCategoryIcon.setVisibility(View.GONE);
        } else if (category.hasIcon()) {
            // Hiển thị emoji hoặc text icon
            holder.textCategoryIcon.setText(category.getIcon());
            holder.textCategoryIcon.setVisibility(View.VISIBLE);
            holder.imageCategoryIcon.setVisibility(View.GONE);
        } else {
            // Không có icon nào - ẩn cả hai
            holder.textCategoryIcon.setVisibility(View.GONE);
            holder.imageCategoryIcon.setVisibility(View.GONE);
        }

        // Thêm sự kiện nhấn vào danh mục với log debug
        convertView.setOnClickListener(v -> {
            int categoryId = category.getId();
            String categoryName = category.getName();
            Log.d("CategoryAdapter", "Clicked Category - Id: " + categoryId + ", Name: " + categoryName);
            Intent intent = new Intent(context, ListFoodsActivity.class);
            intent.putExtra("CategoryId", categoryId);
            intent.putExtra("CategoryName", categoryName);
            intent.putExtra("isSearch", false);
            context.startActivity(intent);
        });

        return convertView;
    }

    private static class ViewHolder {
        FrameLayout categoryBackground;
        TextView textCategoryIcon;
        ImageView imageCategoryIcon;
        TextView textCategoryName;
    }
}