package com.example.campusbud;

public class ItemModel {

    private int image;
    private String one, two, three;

    public ItemModel() {}

    public ItemModel(int image, String one, String two, String three) {
        this.image = image;
        this.one = one;
        this.two = two;
        this.three = three;
    }

    public int getImage() {
        return image;
    }

    public String getOne() {
        return one;
    }
}
