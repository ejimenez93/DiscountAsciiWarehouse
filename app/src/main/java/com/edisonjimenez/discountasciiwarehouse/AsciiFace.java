package com.edisonjimenez.discountasciiwarehouse;


import java.io.Serializable;

public class AsciiFace implements Serializable {

    private static final long serialVerisonID = 1L;

    private String mType;
    private String mID;
    private int mSize;
    private int mPrice;
    private String mFace;
    private int mStock;
    private String mTags;

    private boolean mEnabled;
    private boolean mChecked;
    private String mTextOff;
    private float mAlpha;

    public AsciiFace(String type, String ID, int size, int price, String face, int stock, String tags) {
        this.mType = type;
        this.mID = ID;
        this.mSize = size;
        this.mPrice = price;
        this.mFace = face;
        this.mStock = stock;
        this.mTags = tags;

        // State properties to change on conditions
        this.mChecked = false;

        // Set the label, opacity, and disabled property depending on stock amount
        if (stock == 0) {
            this.mEnabled = false;
            this.mAlpha = 0.5f;
            this.mTextOff = "Out of Stock!";
        }
        else if (stock == 1) {
            this.mTextOff = "Buy Now!\n(Only 1 more in stock!)";
            this.mEnabled = true;
            this.mAlpha = 1f;
        }
        else {
            this.mTextOff = "Buy Now!";
            this.mEnabled = true;
            this.mAlpha = 1f;
        }
    }

    public static long getSerialVerisonID() {
        return serialVerisonID;
    }

    public String getFace() {
        return mFace;
    }

    public void setFace(String face) {
        mFace = face;
    }

    public String getID() {
        return mID;
    }

    public void setID(String ID) {
        mID = ID;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        mPrice = price;
    }

    public int getSize() {
        return mSize;
    }

    public void setSize(int size) {
        mSize = size;
    }

    public int getStock() {
        return mStock;
    }

    public void setStock(int stock) {
        mStock = stock;
    }

    public String getTags() {
        return mTags;
    }

    public void setTags(String tags) {
        mTags = tags;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean checked) {
        this.mChecked = checked;
    }

    public float getAlpha() {
        return mAlpha;
    }

    public void setAlpha(float alpha) {
        mAlpha = alpha;
    }

    public boolean isEnabled() {
        return mEnabled;
    }

    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    public String getTextOff() {
        return mTextOff;
    }

    public void setTextOff(String textOff) {
        mTextOff = textOff;
    }

    @Override
    public String toString() {
        return "{" +
                "type='" + mType + '\'' +
                ", id='" + mID + '\'' +
                ", size=" + mSize +
                ", price=" + mPrice +
                ", face='" + mFace + '\'' +
                ", stock=" + mStock +
                ", tags='[" + mTags + ']' + '\'' +
                '}';
    }
}
