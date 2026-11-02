package org.example;

import java.time.LocalDateTime;

public class Rating {

    private int id;
    private int userId;
    private int mediaId;
    private int stars;
    private String comment;
    private LocalDateTime timestamp;
    private boolean confirmed;

    public Rating(int id, int userId, int mediaId, int stars, String comment){

        if(stars < 1 || stars > 5){
            throw new IllegalArgumentException("Bewertung muss ziwchen 1 und 5 liegen");

        }

        this.id = id;
        this.userId = userId;
        this.mediaId = mediaId;
        this.stars = stars;
        this.comment = comment;
        this.timestamp = LocalDateTime.now();
        this.confirmed = false;

    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public int getMediaId(){
        return mediaId;
    }

    public int getStars() {
        return stars;
    }

    public String getComment(){
        return comment;
    }

    public LocalDateTime getTimestamp(){
        return timestamp;
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}
