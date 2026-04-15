package de.fhswf.raumverwaltung.command;

import java.util.Objects;

public abstract class AbstractCommand implements Command
{
    private final String commandName;

    private final int commandAccelerator;

    public AbstractCommand(String commandName, int commandAccelerator)
    {
        this.commandName = commandName;
        this.commandAccelerator = commandAccelerator;
    }

    @Override
    public String getCommandName()
    {
        return commandName;
    }

    @Override
    public int getCommandAccelerator()
    {
        return commandAccelerator;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCommand that = (AbstractCommand) o;

        return Objects.equals(commandName, that.commandName);
    }

    @Override
    public int hashCode()
    {
        return commandName != null ? commandName.hashCode() : 0;
    }
}
