package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.food_ordering.Adapter.BestFoodsAdapter;
import com.example.food_ordering.Adapter.CategoryAdapter;
import com.example.food_ordering.Domain.Category;
import com.example.food_ordering.Domain.Foods;
import com.example.food_ordering.R;
import com.example.food_ordering.databinding.ActivityMainBinding;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.List;
import android.location.Address;
import android.location.Geocoder;
import java.util.ArrayList;
import java.util.Locale;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityMainBinding binding;
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private ActivityResultLauncher<String> locationPermissionRequest;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        EdgeToEdge.enable(this);
        setContentView(binding.getRoot());

        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Khởi tạo launcher để xử lý kết quả xin quyền
        locationPermissionRequest = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Người dùng đã cấp quyền, tiến hành lấy vị trí
                fetchCurrentLocation();
            } else {
                // Người dùng từ chối, có thể hiển thị thông báo hoặc dùng vị trí mặc định
                Toast.makeText(this, "Không có quyền truy cập vị trí", Toast.LENGTH_SHORT).show();
                binding.locationText.setText("Hà Nội"); // Ví dụ vị trí mặc định
            }
        });

        // Kiểm tra token ngay khi vào MainActivity
        if (!sharedPrefManager.isLoggedIn()) {
            Toast.makeText(this, "Vui lòng đăng nhập", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Hiển thị tên người dùng từ SharedPrefManager
        TextView userNameTextView = findViewById(R.id.userNameTextView);
        String userName = sharedPrefManager.getUserName();
        userNameTextView.setText(userName);

        // Khởi tạo nút giỏ hàng và đặt listener
        ImageView cartBtn = findViewById(R.id.cartBtn);
        cartBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CartActivity.class);
            startActivity(intent);
        });

        // Khởi tạo nút menu và đặt listener với PopupMenu
        ImageView menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(v -> showPopupMenu(v));

        // Khởi tạo SearchView và đặt listener
        SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!query.trim().isEmpty()) {
                    Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
                    intent.putExtra("isSearch", true);
                    intent.putExtra("searchQuery", query.trim());
                    startActivity(intent);
                    searchView.clearFocus();
                } else {
                    Toast.makeText(MainActivity.this, "Vui lòng nhập tên món ăn", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        // Khởi tạo và đặt listener cho Location, Time, Price
        setupLocationFilter();
        setupTimeFilter();
        setupPriceFilter();
        setupViewAllButton();
        initBestFoods();
        initCategories();
    }

    private void setupViewAllButton() {
        TextView viewAllBtn = findViewById(R.id.viewAllBtn);
        viewAllBtn.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListFoodsActivity.class);
            intent.putExtra("CategoryId", 0); // 0 để hiển thị tất cả món ăn
            intent.putExtra("CategoryName", "Tất cả món ăn");
            intent.putExtra("isSearch", false);
            startActivity(intent);
        });
    }

    private void showPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenu().add("Profile");
        popupMenu.getMenu().add("Order");
        popupMenu.getMenu().add("Logout");

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getTitle().equals("Profile")) {
                    Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals("Order")) {
                    if (!sharedPrefManager.isLoggedIn()) {
                        Toast.makeText(MainActivity.this, "Vui lòng đăng nhập để xem đơn hàng", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                        startActivity(intent);
                        return true;
                    }
                    Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
                    startActivity(intent);
                    return true;
                } else if (item.getTitle().equals("Logout")) {
                    // Xóa thông tin người dùng khi đăng xuất
                    sharedPrefManager.clearToken();
                    Toast.makeText(MainActivity.this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, IntroActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                    return true;
                }
                return false;
            }
        });

        popupMenu.show();
    }

    private void setupLocationFilter() {
        binding.locationLayout.setOnClickListener(v -> {
            // Khi người dùng bấm vào, yêu cầu lấy vị trí
            requestLocationPermission();
        });
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Đã có quyền, lấy vị trí luôn
            fetchCurrentLocation();
        } else {
            // Chưa có quyền, hiển thị dialog xin quyền
            locationPermissionRequest.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    @SuppressLint("MissingPermission")
    private void fetchCurrentLocation() {
        // Sử dụng getCurrentLocation để lấy vị trí mới nhất
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, new CancellationTokenSource().getToken())
            .addOnSuccessListener(this, location -> {
                if (location != null) {
                    // Lấy vị trí thành công, chuyển đổi sang địa chỉ
                    updateLocationUI(location);
                } else {
                    Toast.makeText(this, "Không thể lấy được vị trí hiện tại", Toast.LENGTH_SHORT).show();
                    binding.locationText.setText("Unknown");
                }
            })
            .addOnFailureListener(this, e -> {
                Toast.makeText(this, "Lỗi khi lấy vị trí: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                binding.locationText.setText("Error");
            });
    }

    private void updateLocationUI(android.location.Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            if (addresses != null && !addresses.isEmpty()) {
                String cityName = addresses.get(0).getLocality();
                if (cityName != null && !cityName.isEmpty()) {
                    binding.locationText.setText(cityName);
                } else {
                    // Nếu không có tên thành phố, thử lấy tên khu vực
                    binding.locationText.setText(addresses.get(0).getSubAdminArea());
                }
            } else {
                 binding.locationText.setText("Not Found");
            }
        } catch (IOException e) {
            e.printStackTrace();
            binding.locationText.setText("Geocoder Error");
        }
    }

    private void setupTimeFilter() {
        LinearLayout timeLayout = findViewById(R.id.timeLayout);
        TextView timeText = findViewById(R.id.timeText);
        timeLayout.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("0 - 10 min");
            popupMenu.getMenu().add("10 - 20 min");
            popupMenu.getMenu().add("20 - 30 min");

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedTime = item.getTitle().toString();
                if (timeText != null) {
                    timeText.setText(selectedTime);
                }
                Toast.makeText(MainActivity.this, "Selected Time: " + selectedTime, Toast.LENGTH_SHORT).show();
                return true;
            });
            popupMenu.show();
        });
    }

    private void setupPriceFilter() {
        LinearLayout priceLayout = findViewById(R.id.priceLayout);
        TextView priceText = findViewById(R.id.priceText);
        priceLayout.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenu().add("1$ - 10$");
            popupMenu.getMenu().add("10$ - 20$");
            popupMenu.getMenu().add("20$ - 30$");

            popupMenu.setOnMenuItemClickListener(item -> {
                String selectedPrice = item.getTitle().toString();
                if (priceText != null) {
                    priceText.setText(selectedPrice);
                }
                Toast.makeText(MainActivity.this, "Selected Price: " + selectedPrice, Toast.LENGTH_SHORT).show();
                return true;
            });
            popupMenu.show();
        });
    }

    private void initBestFoods() {
        apiService.getBestFoods().enqueue(new Callback<List<Foods>>() {
            @Override
            public void onResponse(Call<List<Foods>> call, Response<List<Foods>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Foods> list = response.body();
                    Log.d(TAG, "Danh sách foods: " + (list != null ? list.size() : "null"));
                    List<Foods> validFoods = new ArrayList<>();
                    if (list != null && !list.isEmpty()) {
                        for (Foods food : list) {
                            if (food.getId() != null && food.getId().matches("^[0-9a-fA-F]{24}$")) {
                                validFoods.add(food);
                                Log.d(TAG, "Valid Food Id: " + food.getId() + ", Title: " + food.getTitle());
                            } else {
                                Log.e(TAG, "Invalid Food Id: " + (food.getId() != null ? food.getId() : "null") + ", Title: " + food.getTitle());
                            }
                        }
                        if (!validFoods.isEmpty()) {
                            binding.recyclerViewFoods.setLayoutManager(new LinearLayoutManager(
                                    MainActivity.this, LinearLayoutManager.HORIZONTAL, false));
                            BestFoodsAdapter adapter = new BestFoodsAdapter(new ArrayList<>(validFoods), food -> {
                                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                                intent.putExtra("object", food);
                                startActivity(intent);
                            });
                            binding.recyclerViewFoods.setAdapter(adapter);
                        } else {
                            Log.w(TAG, "Không có món ăn hợp lệ để hiển thị");
                            Toast.makeText(MainActivity.this, "Không có món ăn hợp lệ để hiển thị", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w(TAG, "Danh sách món ăn rỗng");
                        Toast.makeText(MainActivity.this, "Không có món ăn nào để hiển thị", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.e(TAG, "Lỗi API: " + response.code() + " - " + response.message());
                    try {
                        String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                        Log.e(TAG, "Error body: " + errorBody);
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Lấy dữ liệu món ăn thất bại: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Foods>> call, Throwable t) {
                Log.e(TAG, "Lỗi kết nối: " + t.getMessage());
                Toast.makeText(MainActivity.this, "Lỗi kết nối server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        categories.add(new Category(0, "Pizza", "", R.drawable.btn_1,
                android.graphics.Color.parseColor("#CC66FF")));
        categories.add(new Category(1, "Burger", "", R.drawable.btn_2,
                android.graphics.Color.parseColor("#0099CC")));
        categories.add(new Category(2, "Chicken", "", R.drawable.btn_3,
                android.graphics.Color.parseColor("#FF6666")));
        categories.add(new Category(3, "Sushi", "", R.drawable.btn_4,
                android.graphics.Color.parseColor("#33FF33")));
        categories.add(new Category(4, "Meat", "", R.drawable.btn_5,
                android.graphics.Color.parseColor("#FF99FF")));
        categories.add(new Category(5, "Hotdog", "", R.drawable.btn_6,
                android.graphics.Color.parseColor("#33CC33")));
        categories.add(new Category(6, "Drink", "", R.drawable.btn_7,
                android.graphics.Color.parseColor("#FF66FF")));
        categories.add(new Category(7, "More", "", R.drawable.btn_8,
                android.graphics.Color.parseColor("#6699FF")));
        CategoryAdapter adapter = new CategoryAdapter(this, categories);
        binding.gridViewCategories.setAdapter(adapter);
    }
}