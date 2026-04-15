package de.fhswf.raumverwaltung.command;

public interface Command
{
    void execute();

    String getCommandName();

    int getCommandAccelerator();
}
