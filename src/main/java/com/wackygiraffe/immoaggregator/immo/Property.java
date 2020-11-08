package com.wackygiraffe.immoaggregator.immo;

public class Property {

    private String immo;
    private String price;
    private String link;
    private String image;

    public Property(String immo, String price, String link, String image) {
        this.immo = immo;
        this.price = price;
        this.link = link;
        this.image = image;
    }

    public String getImmo() {
        return immo;
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
