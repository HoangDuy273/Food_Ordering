package com.example.food_ordering.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.R;
import com.example.food_ordering.model.Address;
import java.util.List;
import android.widget.RadioButton;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.AddressViewHolder> {

    private List<Address> addressList;
    private AddressClickListener listener;
    private String selectedAddressId;

    public interface AddressClickListener {
        void onEditClick(Address address);
        void onDeleteClick(Address address);
        void onSetDefaultClick(Address address);
        void onSelectAddress(Address address);
    }

    public AddressAdapter(List<Address> addressList, AddressClickListener listener) {
        this.addressList = addressList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address, parent, false);
        return new AddressViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {
        Address address = addressList.get(position);
        holder.addressLine1.setText(address.getStreet());
        holder.addressLine2.setText(String.format("%s, %s", address.getCity(), address.getCountry()));
        holder.defaultIndicator.setVisibility(address.isDefault() ? View.VISIBLE : View.GONE);

        holder.editBtn.setOnClickListener(v -> listener.onEditClick(address));
        holder.deleteBtn.setOnClickListener(v -> listener.onDeleteClick(address));
        holder.setDefaultBtn.setOnClickListener(v -> listener.onSetDefaultClick(address));
        holder.radioButton.setChecked(address.getId().equals(selectedAddressId));
        holder.radioButton.setOnClickListener(v -> {
            selectedAddressId = address.getId();
            notifyDataSetChanged();
            listener.onSelectAddress(address); // callback về activity
        });
    }

    @Override
    public int getItemCount() {
        return addressList.size();
    }

    public void updateAddresses(List<Address> newAddresses) {
        this.addressList = newAddresses;
        notifyDataSetChanged();
    }

    public void setSelectedAddressId(String id) { this.selectedAddressId = id; notifyDataSetChanged(); }
    public String getSelectedAddressId() { return selectedAddressId; }

    static class AddressViewHolder extends RecyclerView.ViewHolder {
        TextView addressLine1, addressLine2, defaultIndicator;
        Button editBtn, deleteBtn, setDefaultBtn;
        RadioButton radioButton;

        public AddressViewHolder(@NonNull View itemView) {
            super(itemView);
            addressLine1 = itemView.findViewById(R.id.addressLine1);
            addressLine2 = itemView.findViewById(R.id.addressLine2);
            defaultIndicator = itemView.findViewById(R.id.defaultAddressIndicator);
            editBtn = itemView.findViewById(R.id.editAddressBtn);
            deleteBtn = itemView.findViewById(R.id.deleteAddressBtn);
            setDefaultBtn = itemView.findViewById(R.id.setDefaultBtn);
            radioButton = itemView.findViewById(R.id.radioButton);
        }
    }
} 