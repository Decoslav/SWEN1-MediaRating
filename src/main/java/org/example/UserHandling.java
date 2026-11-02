package org.example;

import de.mkammerer.argon2.*;
import java.sql.*;
import java.util.*;

public class UserHandling {
    private UserRepository repository = new UserRepository();
    private Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
    private HashMap<String, User> tokens = new HashMap<>();

    public boolean register(String username, String password) throws SQLException{

        if(username ==null || username.isBlank()){
            System.out.println("Benutzername darf nicht leer sein");
            return false;
        }

        if(password == null ||password.isBlank()){
            System.out.println("Passwort darf nicht leer sein");
            return false;
        }

        if(repository.findWithUsername(username) != null){
            System.out.println("Benutzername bereits registriert");
            return false;
        }

        String hashedPassword = argon2.hash(3,65536,1,password.toCharArray());
        repository.save(username, hashedPassword);
        System.out.println("Benutzer erfolgreich registriert");
        return true;
    }

    public User login(String username, String password) throws SQLException {
        if(username == null || username.isBlank()
            || password == null || password.isBlank()) {

            return null;
        }

        User user = repository.findWithUsername(username);

        if(user == null){
            return null;
        }

        if(argon2.verify(user.getHashedPassword(),password.toCharArray())){
            return user;
        }

        return null;
    }

    public String createToken(User user){

        String token = UUID.randomUUID().toString();
        tokens.put(token, user);
        return token;
    }

    public User findUserByToken(String token){
        return tokens.get(token);
    }

}
