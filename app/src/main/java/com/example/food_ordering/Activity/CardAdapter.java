package com.example.food_ordering.Activity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.food_ordering.R;
import java.util.ArrayList;

public class CardAdapter extends RecyclerView.Adapter<CardAdapter.CardViewHolder> {
    public interface OnCardActionListener {
        void onCardSelected(int position);
        void onCardRemoved(int position);
    }
    private ArrayList<PaymentMethodActivity.CardModel> cardList;
    private OnCardActionListener listener;
    private int selectedIndex = -1;
    public CardAdapter(ArrayList<PaymentMethodActivity.CardModel> list, OnCardActionListener l) {
        cardList = list; listener = l;
    }
    public void setSelectedIndex(int idx) {
        selectedIndex = idx;
        notifyDataSetChanged();
    }
    @NonNull
    @Override
    public CardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false);
        return new CardViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull CardViewHolder holder, int position) {
        PaymentMethodActivity.CardModel card = cardList.get(position);
        holder.cardNumber.setText("**** **** **** " + card.number.substring(card.number.length()-4));
        holder.cardHolder.setText(card.holder);
        holder.cardCheck.setVisibility(position == selectedIndex ? View.VISIBLE : View.GONE);
        holder.itemView.setBackgroundResource(position == selectedIndex ? R.drawable.payment_method_selector_bg_selected : R.drawable.payment_method_selector_bg);
        holder.itemView.setOnClickListener(v -> listener.onCardSelected(position));
        holder.btnRemoveCard.setOnClickListener(v -> listener.onCardRemoved(position));
    }
    @Override
    public int getItemCount() { return cardList.size(); }
    static class CardViewHolder extends RecyclerView.ViewHolder {
        TextView cardNumber, cardHolder;
        ImageView btnRemoveCard, cardCheck;
        CardViewHolder(@NonNull View itemView) {
            super(itemView);
            cardNumber = itemView.findViewById(R.id.cardNumber);
            cardHolder = itemView.findViewById(R.id.cardHolder);
            btnRemoveCard = itemView.findViewById(R.id.btnRemoveCard);
            cardCheck = itemView.findViewById(R.id.cardCheck);
        }
    }
} 