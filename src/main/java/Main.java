private static final String PROMPT = "$ ";
private static final String COMMAND_NOT_FOUND_MSG = "%s: command not found";
private static final String SHELL_BUILTIN = "%s is a shell builtin";
private static final String NOT_FOUND = "%s: not found";

void main() {
    boolean exit = false;
    while (!exit) {
        IO.print(PROMPT);
        String line = IO.readln();
        Cmd cmd = parseCommand(line);
        switch (cmd) {
            case Cmd.ERR(String msg) -> IO.println(msg);
            case Cmd.ECHO(String value) -> IO.println(value);
            case Cmd.EXIT _ -> exit = true;
            case Cmd.TYPE(String msg) -> IO.println(msg);
        }
    }
}

private static Cmd parseCommand(String line) {
    if (line == null || line.isBlank()) {
        return new Cmd.ERR(COMMAND_NOT_FOUND_MSG);
    }
    final String[] lineArgs = line.trim().split(" ");
    final String lineCmd = lineArgs[0];
    return Command.getByName(lineCmd)
            .map(c -> createCmd(c, lineArgs))
            .orElseGet(() -> new Cmd.ERR(COMMAND_NOT_FOUND_MSG.formatted(lineCmd)));
}

private static Cmd createCmd(Command command, String[] lineArgs) {
    return switch (command) {
        case null -> new Cmd.ERR(COMMAND_NOT_FOUND_MSG.formatted(lineArgs[0]));
        case EXIT -> new Cmd.EXIT();
        case ECHO -> {
            String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
            yield new Cmd.ECHO(value);
        }
        case TYPE -> {
            final String typedCommand = lineArgs[1];
            final String message = Command.getByName(typedCommand)
                    .map(c -> SHELL_BUILTIN.formatted(c.name().toLowerCase()))
                    .orElse(NOT_FOUND.formatted(typedCommand));
            yield new Cmd.TYPE(message);
        }
    };
}

