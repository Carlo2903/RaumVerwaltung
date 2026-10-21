# 🏫 Schul-Planer Pro 2026 (Raumverwaltung & Stundenplanung)

Eine plattformunabhängige Desktop-Applikation zur Schulverwaltung, Stundenplanung und zum automatisierten Vertretungsmanagement (Jahrgangsstufen 5 bis 10).

Das System wurde im Rahmen einer 16-wöchigen Projektarbeit im Informatik-Studium entwickelt. Der Fokus lag auf einer sauberen N-Tier-Architektur, der strikten Trennung von UI und Geschäftslogik sowie der Gewährleistung von Datenintegrität in einer relationalen Datenbank.

---

## 👥 Team & Architektur-Aufteilung

Die Konzeption und Implementierung erfolgte kollaborativ mit einer klaren Trennung der Architekturschichten:

* **Carlo Iuliano (@Carlo2903) – Backend & Persistenz:** Relationales Datenbankdesign, ORM (Hibernate/JPA), Generic-DAOs sowie die Implementierung der zentralen Domänenlogik (KonfliktService und VertretungsService).
* **Ismail Hamoud (@isham002) – Frontend & Client-Architektur:** Requirements Engineering, Implementierung der Präsentationsschicht (JavaFX / AtlantaFX), MVVM-Strukturierung und asynchrones Data-Binding.

> **💡 Hinweis zum Repository:** Das Projekt wurde kollaborativ im Team entwickelt. Bei diesem Repository handelt es sich um meinen persönlichen Mirror, um meine Beiträge zur Backend-Architektur, Persistenzschicht und Domänenlogik für mein Portfolio unabhängig zu dokumentieren.

---

## 📸 UI & Features

1. **Authentifizierungsmaske:** Modernes Interface im Flat-Design (AtlantaFX).
2. **Interaktiver Stundenplan-Editor:** Präventives Abfangen von Doppelbelegungen (PlanungException) in Echtzeit durch den KonfliktService.
3. **Schreibgeschütztes Schüler-Portal:** Reagiert asynchron auf Updates im Backend und signalisiert Vertretungen visuell über ein Ampelsystem.

---

## 🏗 Architektur & Design Patterns

* **Backend (N-Tier & ORM):** Strikte Schichtenarchitektur. Die Datenzugriffsschicht nutzt das Generic-DAO-Pattern in Kombination mit JPA/Hibernate zur Kapselung von SQL-Abfragen und sauberen Transaktionssteuerung.
* **Frontend (MVVM):** Reaktive Architektur mit JavaFX. Ansichten sind über Properties (ObservableList) an ViewModels gekoppelt. UI-Komponenten sind vollständig von der Domänenlogik entkoppelt.
* **IT-Sicherheit:** Benutzerpasswörter werden kryptografisch per SHA-256 gehasht. Das Rollenkonzept (Admin, Lehrer, Schüler) verhindert das Laden administrativer CRUD-Masken auf Speicherebene, wenn der Nutzer nur über Lesezugriff verfügt (Security by Design).

---

## 🧠 Technische Herausforderungen & Implementierungsdetails

### 1. ORM-Optimierung (N+1 Select Problem)
Das Ladeverhalten von Hibernate führte bei stark verknüpften Entitäten (Stunde, Lehrkraft, Raum) zunächst zu Performance-Einbußen durch das N+1-Select-Problem. Durch gezieltes Refactoring der JPQL-Abfragen auf LEFT JOIN FETCH werden relationale Abhängigkeiten atomar und effizient in einem einzigen SQL-Statement geladen.

### 2. Verteiltes Integrationstesting via SDN (ZeroTier)
Um komplexe Kaskadierungseffekte (z. B. das Löschen einer Lehrkraft samt verknüpfter Vertretungsstunden) und den automatischen UI-Refresh realitätsnah zu testen, wurde ein verschlüsseltes Peer-to-Peer-VPN über ZeroTier eingerichtet. Dies ermöglichte ein effizientes Integrationstesting unter echten Client-Server-Bedingungen auf einer gemeinsamen PostgreSQL-Instanz ohne externe Hosting-Kosten.

### 3. Asynchrone Thread-Synchronisation im UI
Zeitintensive Datenaktualisierungen dürfen den JavaFX Application Thread nicht blockieren. Zur automatisierten Aktualisierung der Stundenpläne wurde ein AutoRefreshService auf Basis der Java-Timeline implementiert. Dieser lagert Abfragen asynchron aus und reiht UI-Updates mittels Platform.runLater() thread-sicher ein, um NotOnFxApplicationThreadException-Fehler zu verhindern.

---

## 🛠 Tech-Stack

* **Backend:** Java (JDK 21), JPA / Hibernate (ORM), PostgreSQL
* **Frontend:** JavaFX (v21.0.6), AtlantaFX (v2.1.0)
* **Tools & Infrastruktur:** Maven (Wrapper), JUnit (Testing), ZeroTier One (SDN)

---

## ⚙️ Lokales Setup & Testing

### 📋 Voraussetzungen
* Java JDK 21 installiert und in Umgebungsvariablen (JAVA_HOME) gesetzt.
* PostgreSQL lokal installiert und aktiv.

### 🛠 Installation & Start

1. Repository klonen:
   git clone https://github.com/Carlo2903/RaumVerwaltung.git
   cd RaumVerwaltung

2. Datenbank initialisieren (PostgreSQL):
   * Lokale Datenbank namens schulverwaltung anlegen.
   * Schema ausführen: database/01_schema.sql
   * Testdaten einspielen: database/02_data.sql
   * JDBC-Zugangsdaten in der Hibernate-Konfiguration anpassen: src/main/resources/META-INF/persistence.xml

3. Automatisierte Tests ausführen (JUnit):
   * Windows: mvnw.cmd test
   * Linux / macOS: ./mvnw test

4. Anwendung starten:
   * Windows: mvnw.cmd clean javafx:run
   * Linux / macOS: ./mvnw clean javafx:run

---

## 🔑 Login-Daten für die Testumgebung

Im SQL-Skript (02_data.sql) sind folgende Test-Accounts hinterlegt:

* Administrator: Benutzername: admin | Passwort: admin123
* Lehrkraft (Lesezugriff): Benutzername: krz | Passwort: lehrer123
* Schüler (Lesezugriff): Benutzername: 5b | Passwort: schueler123

---

## 📄 Lizenz
Dieses Projekt ist unter der MIT-Lizenz lizenziert. Weitere Details finden sich in der LICENSE-Datei.
