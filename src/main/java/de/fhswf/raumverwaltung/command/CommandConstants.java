package de.fhswf.raumverwaltung.command;

public enum CommandConstants
{
    EXIT_COMMAND("Beenden"),
    HELLO_COMMAND("Hallo"),
    PRINT_ALL_USERS_COMMAND("Zeige alle Benutzer"),
    SHOW_HELLO_MENU_COMMAND("Zeige Hallo Menü"), SHOW_USER_MENU_COMMAND("Zeige Benutzermenü");

    private final String name;

    CommandConstants(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

}
