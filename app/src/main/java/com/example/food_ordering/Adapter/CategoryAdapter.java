package com.example.food_ordering.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;

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
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_category, parent, false);
        }

        Category category = categoryList.get(position);

        FrameLayout categoryBackground = convertView.findViewById(R.id.categoryBackground);
        TextView textCategoryIcon = convertView.findViewById(R.id.textCategoryIcon);
        TextView textCategoryName = convertView.findViewById(R.id.textCategoryName);

        categoryBackground.setBackgroundColor(category.getBackgroundColor());
        textCategoryIcon.setText(category.getIcon());
        textCategoryName.setText(category.getName());

        return convertView;
    }
}