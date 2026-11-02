package org.example;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.SQLException;

public class Server {

    private UserHandling service = new UserHandling();
    private ObjectMapper mapper = new ObjectMapper();
    private MediaHandling mediaService = new MediaHandling();

    public void start() throws IOException{
        HttpServer server = HttpServer.create(new InetSocketAddress(8080),0);

        server.createContext("/test", exchange -> {
            String response = "{\"message\":\"Server funktioniert\"}";
            byte[]  bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");

            exchange.sendResponseHeaders(200, bytes.length);

            try (OutputStream output = exchange.getResponseBody()){
                output.write(bytes);
            }
        });

        server.createContext("/users", exchange -> {
            int status;
            String response;

            if(!exchange.getRequestURI().getPath().equals("/users")) {
                status = 404;
                response = "{\"message\":\"Endpunkt nicht gefunden\"}";

            }else if (!exchange.getRequestMethod().equals("POST")){
                status = 405;
                exchange.getResponseHeaders().set("Allow", "POST");
                response = "{\"message\":\"Nur Post erlaubt\"}";

            }else {
                try {
                    UserRequest request = mapper.readValue(exchange.getRequestBody(),UserRequest.class);

                    if(request == null){
                        status = 400;
                        response = "{\"message\":\"Benutzerdaten fehlen\"}";

                    }else {
                        boolean success = service.register(request.username, request.password);

                        if(success){
                            status = 201;
                            response = "{\"message\":\"Benutzer Registriert\"}";

                        }else {
                            status = 400;
                            response = "{\"message\":\"Eingabe ungueltig oder Benutzername vergeben\"}";

                        }
                    }

                }catch (IOException e){
                    status = 400;
                    response = "{\"message\":\"Ungueltiges json\"}";
                }catch (SQLException e){
                    status = 500;
                    response = "{\"message\":\"Datenbankfehler\"}";
                    System.out.println(e.getMessage());
                }
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set( "Content-Type", "application/json; charset=utf-8");

            exchange.sendResponseHeaders(status, bytes.length);

            try (OutputStream output = exchange.getResponseBody()){

                output.write(bytes);

            }

        });

        server.createContext("/sessions", exchange -> {

            int status;
            String response;

            if(!exchange.getRequestURI().getPath().equals("/sessions")) {
                status = 404;
                response = "{\"message\":\"Endpunkt nicht gefunden\"}";

            }else if (!exchange.getRequestMethod().equals("POST")){
                status = 405;
                exchange.getResponseHeaders().set("Allow", "POST");
                response = "{\"message\":\"Nur Post erlaubt\"}";

            }else {
                try {
                    UserRequest request = mapper.readValue(exchange.getRequestBody(),UserRequest.class);

                    if(request == null){
                        status = 400;
                        response = "{\"message\":\"Benutzerdaten fehlen\"}";

                    }else {
                        User user = service.login(request.username, request.password);

                        if(user != null){
                            String token = service.createToken(user);

                            status = 200;
                            response = "{\"token\":\""+ token + "\"}";

                        }else {
                            status = 401;
                            response = "{\"message\":\"Username oder Passwort falsch\"}";

                        }
                    }

                }catch (IOException e){
                    status = 400;
                    response = "{\"message\":\"Ungueltiges json\"}";
                }catch (SQLException e){
                    status = 500;
                    response = "{\"message\":\"Datenbankfehler\"}";
                    System.out.println(e.getMessage());
                }
            }

            byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

            exchange.getResponseHeaders().set( "Content-Type", "application/json; charset=utf-8");

            exchange.sendResponseHeaders(status, bytes.length);

            try (OutputStream output = exchange.getResponseBody()){

                output.write(bytes);

            }

        });

        server.createContext("/media" , exchange -> {

            String authorization = exchange.getRequestHeaders().getFirst("Authorization");

            if(authorization == null || !authorization.startsWith("Bearer ")){
                sendResponse(exchange, 401, "{\"message\":\"Token fehlt\"}");

                return;
            }

            String token = authorization.substring(7);
            User user = service.findUserByToken(token);

            if(user == null){
                sendResponse(exchange, 401, "{\"message\":\"Token ungueltig\"}");

                return;
            }

            String path = exchange.getRequestURI().getPath();
            String method = exchange.getRequestMethod();

            if (path.startsWith("/media/")) {
                if (!method.equals("GET") && !method.equals("PUT")
                        && !method.equals("DELETE")) {
                    exchange.getResponseHeaders().set("Allow", "GET, PUT, DELETE");
                    sendResponse(exchange, 405, "{\"message\":\"Nur GET, PUT und DELETE erlaubt\"}");
                    return;
                }

                try {
                    int id = Integer.parseInt(path.substring(7));
                    MediaEntry media = mediaService.findById(id);

                    if (media == null) {
                        sendResponse(exchange, 404, "{\"message\":\"Medieneintrag nicht gefunden\"}");

                    } else if (method.equals("GET")) {

                        String response = mapper.writeValueAsString(media);
                        sendResponse(exchange, 200, response);

                    } else if (media.getCreatorId() != user.getId()) {

                        sendResponse(exchange, 403, "{\"message\":\"Nur der Ersteller darf aendern oder loeschen\"}");

                    } else if (method.equals("DELETE")) {


                        boolean success = mediaService.delete(id,user.getId());

                        if(success) {

                            sendResponse(exchange, 200, "{\"message\":\"Eintrag geloescht\"}");
                        }else {

                            sendResponse(exchange,404, "{\"message\":\"Eintrag nicht gefunden\"}");

                        }


                    } else {
                        MediaRequest request;

                        try {
                            request = mapper.readValue(
                                    exchange.getRequestBody(), MediaRequest.class);
                        } catch (IOException e) {
                            sendResponse(exchange, 400,
                                    "{\"message\":\"Ungueltiges JSON\"}");
                            return;
                        }

                        if (request == null) {
                            sendResponse(exchange, 400,
                                    "{\"message\":\"Mediendaten fehlen\"}");
                            return;
                        }

                        MediaEntry updatedMedia = new MediaEntry(
                                id, request.title, request.description,
                                request.mediaType, request.releaseYear,
                                request.genres, request.ageRestriction, user.getId());

                        if (!mediaService.isValid(updatedMedia)) {
                            sendResponse(exchange, 400,
                                    "{\"message\":\"Mediendaten ungueltig\"}");
                        } else if (mediaService.update(updatedMedia)) {
                            sendResponse(exchange, 200,
                                    "{\"message\":\"Medieneintrag geaendert\"}");
                        } else {
                            sendResponse(exchange, 404,
                                    "{\"message\":\"Medieneintrag nicht gefunden\"}");
                        }
                    }

                } catch (NumberFormatException e) {
                    sendResponse(exchange, 400, "{\"message\":\"Ungueltige Medien-ID\"}");
                } catch (SQLException e) {
                    System.out.println(e.getMessage());
                    sendResponse(exchange, 500, "{\"message\":\"Datenbankfehler\"}");
                }

                return;
            }

            if (!path.equals("/media")) {
                sendResponse(exchange, 404, "{\"message\":\"Endpunkt nicht gefunden\"}");
                return;
            }

            if (!method.equals("POST")) {
                exchange.getResponseHeaders().set("Allow", "POST");
                sendResponse(exchange, 405, "{\"message\":\"Nur POST erlaubt\"}");
                return;
            }

            try {
                MediaRequest request = mapper.readValue(exchange.getRequestBody(), MediaRequest.class);

                if(request == null){
                    sendResponse(exchange, 400, "{\"message\":\"Mediendaten fehlen\"}");

                    return;
                }

                MediaEntry media = new MediaEntry(0, request.title, request.description, request.mediaType, request.releaseYear, request.genres, request.ageRestriction, user.getId());

                if(mediaService.create(media)) {
                    sendResponse(exchange, 201, "{\"message\":\"Medieneintrag erstellt\"}");

                }else {
                    sendResponse(exchange, 400, "{\"message\":\"Mediendaten ungueltig\"}");
                }

            }catch (IOException e){
                sendResponse(exchange, 400, "{\"message\":\"ungueltiges json\"}");
            }catch (SQLException e){

                System.out.println(e.getMessage());
                sendResponse(exchange, 500, "{\"message\":\"Datenbankfehler\"}");

            }

        });


        server.start();
        System.out.println("Server läuft auf http://localhost:8080");

    }

    private void sendResponse(HttpExchange exchange, int status, String response) throws IOException{

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status,bytes.length);

        try (OutputStream output = exchange.getResponseBody()){
            output.write(bytes);
        }


    }



}
