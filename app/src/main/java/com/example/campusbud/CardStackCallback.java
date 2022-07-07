package com.example.campusbud;

import androidx.recyclerview.widget.DiffUtil;

import java.util.List;

public class CardStackCallback extends DiffUtil.Callback {

    private List<ItemModel> old, other;

    public CardStackCallback(List<ItemModel> old, List<ItemModel> other) {
        this.old = old;
        this.other = other;
    }

    @Override
    public int getOldListSize() {
        return old.size();
    }

    @Override
    public int getNewListSize() {
        return other.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition).getImage() == other.get(newItemPosition).getImage();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return old.get(oldItemPosition) == other.get(newItemPosition);
    }
}
