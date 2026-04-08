package com.example.anew;

/**
 * Data model for a favorited place.
 * Used in {@link FavoritesActivity} and {@link PlaceAdapter} to display
 * saved places with their name, image, and coordinates.
 */
public class Place {
    private String id;
    private String name;
    private String imageUrl;
    private double lat;
    private double lng;

    public Place(String id, String name, String imageUrl, double lat, double lng) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.lat = lat;
        this.lng = lng;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}