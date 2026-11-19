# Schul-Planer (Raumverwaltung & Stundenplanung)

Desktop-Anwendung zur Verwaltung von Stundenplänen, Räumen und Vertretungen für eine Schule (Jahrgangsstufen 5 bis 10).

Entstanden als 16-wöchige Projektarbeit im Informatikstudium an der Fachhochschule Südwestfalen (Modul „Projekt“), im Zweierteam.

## Team

- **Carlo Iuliano (@Carlo2903):** Backend und Persistenz – Datenbankmodell, JPA/Hibernate, Generic DAO, Domänenlogik (u. a. `KonfliktService`, `VertretungsService`), JUnit-Tests. Zusätzlich Unterstützung bei der Umsetzung des Frontends.
- **Ismail Hamoud (@isham002):** Frontend – Präsentationsschicht mit JavaFX und AtlantaFX, MVVM-Struktur der Oberfläche.

## Funktionen

- Login mit drei Rollen: Administrator (Bearbeiten), Lehrkraft und Schüler (nur Lesen)
- Stundenplan-Editor: Beim Anlegen einer Stunde prüft der `KonfliktService` Doppelbelegungen von Lehrkraft, Raum und Klasse sowie Wochenstunden und Sperrzeiten der Lehrkräfte und lehnt Konflikte mit einer `PlanungException` ab
- Vertretungsverwaltung: Ausfälle markieren, Vertretungen zuweisen und löschen
- Verwaltung von Lehrkräften, Fächern, Klassen, Räumen, Schülern und Erziehungsberechtigten
- Schülerportal mit Anzeige des eigenen Stundenplans und markierten Vertretungen
- Automatische Aktualisierung der Pläne im Hintergrund

## Architektur

- **Schichten:** Oberfläche (JavaFX) → Services (Geschäftslogik) → DAOs → PostgreSQL
- **Datenzugriff:** Generic-DAO-Pattern mit JPA/Hibernate
- **Oberfläche:** MVVM – Views sind über JavaFX-Properties an ViewModels gebunden
- **Passwörter:** werden als SHA-256-Hash gespeichert (ohne Salt; für einen echten Einsatz wäre ein Verfahren wie bcrypt oder Argon2 nötig)

## Technische Details

### Unnötige Datenbankabfragen (N+1-Problem)
Beim Laden von Stunden, Vertretungen und Lehrkräften mit ihren verknüpften Daten hat Hibernate zunächst für jede Verknüpfung eine eigene Abfrage abgesetzt. Die betroffenen JPQL-Abfragen in den DAOs (z. B. `VertretungDao`, `LehrkraftDao`) laden die benötigten Beziehungen jetzt mit `LEFT JOIN FETCH` in einer Abfrage.

### Aktualisierung der Oberfläche ohne Blockieren
Der `AutoRefreshService` stößt die Aktualisierung über eine JavaFX-`Timeline` regelmäßig an. Änderungen an der Oberfläche werden mit `Platform.runLater()` auf den JavaFX Application Thread gelegt, damit keine `IllegalStateException` („Not on FX application thread“) entsteht.

### Gemeinsames Testen über ZeroTier
Für Tests mit mehreren Clients auf derselben PostgreSQL-Datenbank haben wir unsere Rechner über ein ZeroTier-Netzwerk verbunden, ohne einen Server mieten zu müssen.

## Tech-Stack

- Java 21, JavaFX 21, AtlantaFX
- JPA / Hibernate, PostgreSQL
- Maven (Wrapper), JUnit
- ZeroTier (nur für Tests im Team)

## Lokales Setup

**Voraussetzungen:** JDK 21, PostgreSQL (der Dump wurde mit PostgreSQL 18 erstellt; ab Version 17 läuft er ohne Warnungen)

1. Repository klonen
   ```bash
   git clone https://github.com/Carlo2903/RaumVerwaltung.git
   cd RaumVerwaltung
   ```
2. Datenbank anlegen und Testdaten einspielen
   ```bash
   createdb -U postgres schulverwaltung
   psql -U postgres -d schulverwaltung -f database/schulverwaltung_dump.sql
   ```
   Die Testdaten (Namen, Adressen, Kontaktdaten) sind frei erfunden.
3. Zugangsdaten in `src/main/resources/META-INF/persistence.xml` eintragen
4. Tests ausführen
   ```bash
   ./mvnw test          # Windows: mvnw.cmd test
   ```
5. Anwendung starten
   ```bash
   ./mvnw clean javafx:run          # Windows: mvnw.cmd clean javafx:run
   ```

Ohne Dump legt Hibernate die Tabellen beim Start selbst an (`hbm2ddl.auto=update`). Dann existiert nur das Admin-Konto, das die Anwendung automatisch erstellt.

## Testkonten (aus dem Dump)

| Rolle | Benutzername | Passwort |
| --- | --- | --- |
| Administrator | `admin` | `admin123` |
| Lehrkraft | `krz` | `lehrer123` |
| Schüler (Klasse 5b) | `5b` | `schueler123` |
