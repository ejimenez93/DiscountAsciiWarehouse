package com.edisonjimenez.discountasciiwarehouse;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

public class AsciiFaceViewHolder extends RecyclerView.ViewHolder {
    protected TextView face;
    protected TextView price;
    protected ToggleButton button;

    public AsciiFaceViewHolder(View view) {
        super(view);
        this.face = (TextView) view.findViewById(R.id.face);
        this.price = (TextView) view.findViewById(R.id.price);
        this.button = (ToggleButton) view.findViewById(R.id.button);
    }
}
