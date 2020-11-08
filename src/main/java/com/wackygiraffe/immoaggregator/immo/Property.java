package com.wackygiraffe.immoaggregator.immo;

public class Property {

    private String price;
    private String link;
    private String image;

    public Property(String price, String link, String image) {
        this.price = price;
        this.link = link;
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public String getLink() {
        return link;
    }

    public String getImage() {
        return image;
    }
}
