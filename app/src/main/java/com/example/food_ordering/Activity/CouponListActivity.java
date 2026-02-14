package com.example.food_ordering.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.Adapter.CouponAdapter;
import com.example.food_ordering.R;
import com.example.food_ordering.model.Coupon;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.food_ordering.model.CouponsResponse;

public class CouponListActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CouponAdapter adapter;
    private List<Coupon> couponList = new ArrayList<>();
    private ApiService apiService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_list);
        recyclerView = findViewById(R.id.recyclerViewCoupons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        apiService = RetrofitClient.getApiService();
        adapter = new CouponAdapter(couponList, coupon -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("coupon_code", coupon.getCode());
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        });
        recyclerView.setAdapter(adapter);
        loadCoupons();
        findViewById(R.id.backBtn).setOnClickListener(v -> finish());
    }

    private void loadCoupons() {
        apiService.getActiveCoupons().enqueue(new Callback<CouponsResponse>() {
            @Override
            public void onResponse(Call<CouponsResponse> call, Response<CouponsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getCoupons() != null) {
                    couponList.clear();
                    couponList.addAll(response.body().getCoupons());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(CouponListActivity.this, "Không lấy được danh sách coupon", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CouponsResponse> call, Throwable t) {
                Toast.makeText(CouponListActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("CouponListActivity", "API error", t);
            }
        });
    }
} 