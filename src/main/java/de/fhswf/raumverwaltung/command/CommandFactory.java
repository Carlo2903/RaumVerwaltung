package de.fhswf.raumverwaltung.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CommandFactory
{
    private static CommandFactory instance = null;

    private final List<Command> commands;

    private CommandFactory()
    {
        commands = new ArrayList<>();
    }

    public static CommandFactory getInstance()
    {
        if (instance == null)
        {
            instance = new CommandFactory();
        }
        return instance;
    }

    public Optional<Command> canRegister(String commandName)
    {
        return commands.stream().filter(c -> c.getCommandName().equalsIgnoreCase(commandName)).findAny();
    }

    public Command register(Command command) throws CommandExistsException, InvalidCommandException
    {
        Optional<Command> registredCommand = canRegister(command.getCommandName());
        if (!registredCommand.isEmpty())
        {
            return registredCommand.get();
        }
        if (command.getCommandAccelerator() == 0)
        {
            throw new InvalidCommandException();
        }

        commands.add(command);

        return command;
    }

}
