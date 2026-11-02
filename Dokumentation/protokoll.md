# Protokoll

## Beschreibung, Design und Struktur

Ich habe das Projekt schrittweise aufgebaut und mich dabei, soweit möglich, an den LV-Unterlagen orientiert.
Zuerst habe ich die Datenbank mit PostgreSQL eingerichtet und die Verbindung mit Java getestet. Danach kamen Registrierung, Login, Tokens und die Medienverwaltung.

Die Struktur habe ich vorerst einfach gehalten und alle Klassen im Package `org.example` gespeichert.

**Klassenstruktur und Erklärung:**

-**Main** - Erstellt den Server und startet ihn.
-**Server** - Verwendet den eingebauten Java-HTTP-Server, verarbeitet HTTP-MEthoden, Pfade und Header, liest JSON-Anfragen ein und sendet Antworten mit passenden Statuscode zurück.
-**DatabaseConnection** - Enthält die Verbindungsdaten und stellt über JDBC eine Verbindung zur Datenbank her.
-**User** - Datenmodell für Benutzer mit Id, Benutzername und Passwort
-**MediaEntry** - Datenmodell für Filme.
-**Rating** - Datenmodell für Bewertungen
-**UserRequest und MediaRequest** - Enthalten Daten eingehender JSON-Anfragen
-**UserRepository** - Speichert Benutzer und sucht sie anhand des Benutzernamens.
-**MediaRepository** - Führt SQL-Anweisungen zum Anglegen, Lesen, Ändern und Löschen von Medieneinträgen aus.
-**UserHandling** -  Enthält Registrierung, Login und TOken-Verwaltung. Passwörter werden mit Argon2 gehasht. Nach Login wird ein zufälliger Token erzeugt.
-**MediaHandling**- Prüft die Mediendaten und ruft die nötigen Methoden auf.

PostgreSQL läuft in einem Docker-Container. Ein Volume speichert die Daten. Für SQL-Abfragen wird "PreparedStatement" verwendet.
Die Ersteller-ID wird aus dem angemeldeten Benutzer übernommen. Nur der Ersteller darf Medieneinträge ändern oder löschen.

**Github-Repository** :
**Postman** liegt in `postman/collections/Semesterprojekt MRP`

## Aufgetretene Probleme

- Fehlende prüfung auf unique Benutzernamen, dadurch war es möglich mehrere Profile mit dem gleichen Benutzernamen anzulegen. Wurde gelöst indem die tabelle nur unique Werte akzeptiert.
- Tippfehler in HTTP-Pfaden und die Schreibweise "Post" statt "POST" führten zu fehlern.
- Registrierung gab anfangs nur eine Konsolenausgabe zurück, dadurch konnte der HTTP-Endpunkt nicht erkennen ob die Registrierung erfolgreich war. Das wurde auf einen Boolean Rückgabewert umgestellt.

## Aufwandsschätzung

- Projekt, Docker und PostgreSQL einrichten | 3h
- Datenbankverbindung, Models und Repository-Klassen erstellen | 4h
- Registrierung, Login und Token-Verwaltung implementieren | 6h
- HTTP-Server und Medien-Endpunkte implementieren | 8h
- Fehlersuche | 3h
- Protokoll | 3h