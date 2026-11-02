package org.example;



public class Main {
    static void main() {



        Server server = new Server();

        try {
            server.start();
        } catch (java.io.IOException e) {
            System.out.println("Serverfehler: " + e.getMessage());
        }
    }

}
