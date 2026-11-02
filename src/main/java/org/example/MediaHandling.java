package org.example;

import java.sql.*;

public class MediaHandling {

    private MediaRepository repository = new MediaRepository();

    public boolean isValid(MediaEntry media) throws SQLException{

        if(media.getTitle() == null ||media.getTitle().isBlank()){
            return false;
        }

        String type = media.getMediaType();

        if(type == null){
            return false;
        }

        if(!type.equals("movie") && !type.equals("series") && !type.equals("game")){
            return false;
        }

        if(media.getAgeRestriction() < 0){
            return false;
        }

        return true;
    }

    public boolean create(MediaEntry media) throws SQLException{

        if(!isValid(media)){
            return false;
        }

        repository.save(media);
        return true;
    }

    public MediaEntry findById(int id)throws SQLException{
        return repository.findMediaById(id);

    }

    public boolean update(MediaEntry media) throws SQLException{

        if(!isValid(media)){
            return false;
        }

        return repository.update(media);
    }

    public boolean delete(int id, int creatorId) throws SQLException{

        return repository.delete(id, creatorId);
    }



}
