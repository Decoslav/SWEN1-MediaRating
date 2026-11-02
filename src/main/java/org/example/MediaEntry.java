package org.example;

public class MediaEntry {
    private int id;
    private String title;
    private String description;
    private String mediaType;
    private int releaseYear;
    private String genres;
    private int ageRestriction;
    private int creatorId;

    public MediaEntry(int id,String title, String description, String mediaType,
                      int releaseYear, String genres, int ageRestriction, int creatorId){

        this.id = id;
        this.title = title;
        this.description = description;
        this.mediaType = mediaType;
        this.releaseYear = releaseYear;
        this.genres = genres;
        this.ageRestriction = ageRestriction;
        this.creatorId = creatorId;
    }

    public int getId() {
        return id;
    }

    public String getTitle(){
        return title;
    }

    public String getDescription(){
        return description;
    }

    public String getMediaType(){
        return mediaType;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getGenres(){
        return genres;
    }

    public int getAgeRestriction(){
        return ageRestriction;
    }

    public int getCreatorId(){
        return creatorId;
    }
}
