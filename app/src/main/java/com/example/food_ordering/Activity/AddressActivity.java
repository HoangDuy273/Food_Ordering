package com.example.food_ordering.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.Adapter.AddressAdapter;
import com.example.food_ordering.R;
import com.example.food_ordering.model.Address;
import com.example.food_ordering.model.AddressResponse;
import com.example.food_ordering.network.ApiService;
import com.example.food_ordering.network.RetrofitClient;
import com.example.food_ordering.network.SharedPrefManager;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private AddressAdapter adapter;
    private List<Address> addressList = new ArrayList<>();
    private ApiService apiService;
    private SharedPrefManager sharedPrefManager;
    private boolean selectMode = false;
    private Address selectedAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);

        apiService = RetrofitClient.getApiService();
        sharedPrefManager = new SharedPrefManager(this);

        if (getIntent() != null && getIntent().getBooleanExtra("selectMode", false)) {
            selectMode = true;
        }

        recyclerView = findViewById(R.id.recyclerViewAddresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AddressAdapter(addressList, new AddressAdapter.AddressClickListener() {
            @Override
            public void onEditClick(Address address) { showEditAddressDialog(address); }
            @Override
            public void onDeleteClick(Address address) { deleteAddress(address); }
            @Override
            public void onSetDefaultClick(Address address) { setDefaultAddress(address); }
            @Override
            public void onSelectAddress(Address address) {
                if (selectMode) {
                    selectedAddress = address;
                    Intent result = new Intent();
                    result.putExtra("selectedAddress", address);
                    setResult(RESULT_OK, result);
                    finish();
                }
            }
        });
        recyclerView.setAdapter(adapter);

        findViewById(R.id.addAddressBtn).setOnClickListener(v -> showAddAddressDialog());
        fetchAddresses();
    }

    private void fetchAddresses() {
        String token = "Bearer " + sharedPrefManager.getToken();
        apiService.getAddresses(token).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    adapter.updateAddresses(response.body().getAddresses());
                } else {
                    Toast.makeText(AddressActivity.this, "Lấy danh sách địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAddAddressDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Thêm địa chỉ mới");
        android.view.View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
        android.widget.EditText streetEdt = dialogView.findViewById(R.id.editStreet);
        android.widget.EditText cityEdt = dialogView.findViewById(R.id.editCity);
        android.widget.EditText stateEdt = dialogView.findViewById(R.id.editState);
        android.widget.EditText countryEdt = dialogView.findViewById(R.id.editCountry);
        builder.setView(dialogView);
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String street = streetEdt.getText().toString().trim();
            String city = cityEdt.getText().toString().trim();
            String state = stateEdt.getText().toString().trim();
            String country = countryEdt.getText().toString().trim();
            if (street.isEmpty() || city.isEmpty() || state.isEmpty() || country.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }
            addAddressToServer(street, city, state, country);
        });
        builder.setNegativeButton("Hủy", null);
        builder.show();
    }

    private void addAddressToServer(String street, String city, String state, String country) {
        String token = "Bearer " + sharedPrefManager.getToken();
        // Sử dụng Map để đảm bảo key đúng chuẩn backend
        java.util.Map<String, Object> addressMap = new java.util.HashMap<>();
        addressMap.put("street", street);
        addressMap.put("city", city);
        addressMap.put("state", state);
        addressMap.put("country", country);
        // Nếu muốn truyền thêm location hoặc isDefault, bổ sung tại đây
        apiService.addAddress(token, addressMap).enqueue(new retrofit2.Callback<com.example.food_ordering.model.AddressResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.example.food_ordering.model.AddressResponse> call, retrofit2.Response<com.example.food_ordering.model.AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressActivity.this, "Thêm địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    fetchAddresses();
                } else {
                    Toast.makeText(AddressActivity.this, "Thêm địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<com.example.food_ordering.model.AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditAddressDialog(Address address) {
        // Tương tự showAddAddressDialog nhưng setText các trường và gọi API updateAddress
        // (bạn có thể bổ sung sau)
    }
    private void deleteAddress(Address address) {
        String token = "Bearer " + sharedPrefManager.getToken();
        apiService.deleteAddress(token, address.getId()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressActivity.this, "Xóa địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    fetchAddresses(); // Refresh list
                } else {
                    Toast.makeText(AddressActivity.this, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void setDefaultAddress(Address address) {
        String token = "Bearer " + sharedPrefManager.getToken();
        apiService.setDefaultAddress(token, address.getId()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressActivity.this, "Đặt làm mặc định thành công", Toast.LENGTH_SHORT).show();
                    fetchAddresses(); // Refresh list
                } else {
                    Toast.makeText(AddressActivity.this, "Đặt làm mặc định thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onEditClick(Address address) {
        // Logic to show a dialog/new activity to edit the address
        Toast.makeText(this, "Sửa địa chỉ: " + address.getStreet(), Toast.LENGTH_SHORT).show();
    }

    public void onDeleteClick(Address address) {
        String token = "Bearer " + sharedPrefManager.getToken();
        apiService.deleteAddress(token, address.getId()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressActivity.this, "Xóa địa chỉ thành công", Toast.LENGTH_SHORT).show();
                    fetchAddresses(); // Refresh list
                } else {
                    Toast.makeText(AddressActivity.this, "Xóa địa chỉ thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSetDefaultClick(Address address) {
        String token = "Bearer " + sharedPrefManager.getToken();
        apiService.setDefaultAddress(token, address.getId()).enqueue(new Callback<AddressResponse>() {
            @Override
            public void onResponse(Call<AddressResponse> call, Response<AddressResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(AddressActivity.this, "Đặt làm mặc định thành công", Toast.LENGTH_SHORT).show();
                    fetchAddresses(); // Refresh list
                } else {
                    Toast.makeText(AddressActivity.this, "Đặt làm mặc định thất bại", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<AddressResponse> call, Throwable t) {
                Toast.makeText(AddressActivity.this, "Lỗi kết nối", Toast.LENGTH_SHORT).show();
            }
        });
    }
} 