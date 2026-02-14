package com.example.food_ordering.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.R;
import com.example.food_ordering.model.Coupon;
import java.util.List;

public class CouponAdapter extends RecyclerView.Adapter<CouponAdapter.CouponViewHolder> {
    public interface OnCouponClickListener {
        void onCouponClick(Coupon coupon);
    }
    private List<Coupon> couponList;
    private OnCouponClickListener listener;

    public CouponAdapter(List<Coupon> couponList, OnCouponClickListener listener) {
        this.couponList = couponList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CouponViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_coupon, parent, false);
        return new CouponViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CouponViewHolder holder, int position) {
        Coupon coupon = couponList.get(position);
        holder.tvCode.setText(coupon.getCode());
        // Chỉ hiển thị giảm X% (bỏ tối đa, tối thiểu, loại số tiền)
        String valueStr = "Giảm " + coupon.getDiscountValue() + "%";
        String desc = coupon.getDescription() != null && !coupon.getDescription().isEmpty() ? coupon.getDescription() : "";
        holder.tvDescription.setText(desc.isEmpty() ? valueStr : (desc + "\n" + valueStr));
        holder.itemView.setOnClickListener(v -> listener.onCouponClick(coupon));
    }

    @Override
    public int getItemCount() {
        return couponList.size();
    }

    public static class CouponViewHolder extends RecyclerView.ViewHolder {
        TextView tvCode, tvDescription;
        public CouponViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCode = itemView.findViewById(R.id.tvCouponCode);
            tvDescription = itemView.findViewById(R.id.tvCouponDescription);
        }
    }
} 