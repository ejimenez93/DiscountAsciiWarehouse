package com.edisonjimenez.discountasciiwarehouse;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerViewAdapter extends RecyclerView.Adapter<AsciiFaceViewHolder> {
    private final String TAG = RecyclerViewAdapter.class.getSimpleName();
    private List<AsciiFace> mAsciiFaceList;
    private List<AsciiFace> mAsciiFacesInCart;
    private Context mContext;

    public RecyclerViewAdapter(Context context, List<AsciiFace> asciiFaceList) {
        this.mAsciiFaceList = asciiFaceList;
        this.mAsciiFacesInCart = new ArrayList<AsciiFace>();
        this.mContext = context;
    }

    @Override
    public AsciiFaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.browse, null);
        return new AsciiFaceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AsciiFaceViewHolder asciiFaceViewHolder, final int i) {
        final AsciiFace asciiFaceItem = getAsciiFace(i);
        asciiFaceViewHolder.face.setText(asciiFaceItem.getFace());

        // Convert the price to a USD value
        NumberFormat currencyFormatter = NumberFormat. getCurrencyInstance(Locale.US);
        asciiFaceViewHolder.price.setText(currencyFormatter.format(asciiFaceItem.getPrice() / 100.00));


        // Set Defaults based on AsciiFace properties
        asciiFaceViewHolder.button.setOnCheckedChangeListener(null);
        asciiFaceViewHolder.button.setEnabled(asciiFaceItem.isEnabled());
        asciiFaceViewHolder.button.setAlpha(asciiFaceItem.getAlpha());
        asciiFaceViewHolder.button.setTextOff(asciiFaceItem.getTextOff());
        asciiFaceViewHolder.button.setChecked(asciiFaceItem.isChecked());

        // Add a click listener on the ToggleButton in order
        // and save the state to the AsciiFace object
        asciiFaceViewHolder.button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getAsciiFace(i).setChecked(isChecked);

                if (isChecked) {
                    mAsciiFacesInCart.add(asciiFaceItem);
                }
                else {
                    mAsciiFacesInCart.remove(asciiFaceItem);
                }

                notifyItemChanged(i, asciiFaceItem);

                Toast.makeText(buttonView.getContext(), mAsciiFacesInCart.size() + " face(s) in your cart", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Method used to get the size of the list
     * @return int value with the size or null if empty
     */
    @Override
    public int getItemCount() {
        return (mAsciiFaceList != null ? mAsciiFaceList.size() : 0);
    }

    /**
     * Method used to replace existing data with new items
     * @param newPhotos List of AsciiFace objects
     */
    public void replaceNewData(List<AsciiFace> newPhotos) {
        mAsciiFaceList = newPhotos;
        notifyDataSetChanged();
    }

    /**
     * Method used to append new data onto the existing list of items
     * @param newPhotos List of AsciiFace objects
     */
    public void addNewData(List<AsciiFace> newPhotos) {
        mAsciiFaceList.addAll(newPhotos);
        notifyItemRangeInserted(getItemCount(), mAsciiFaceList.size() - 1);
    }

    /**
     * Method used to retrieve an AsciiFace object based on the location
     * @param position int value containing the position
     * @return AsciiFace object
     */
    public AsciiFace getAsciiFace(int position) {
        return (mAsciiFaceList != null) ? mAsciiFaceList.get(position) : null;
    }
}

