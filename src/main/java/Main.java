private static final String PROMPT = "$ ";
private static final String COMMAND_NOT_FOUND_MSG = "%s: command not found";

void main() {
    boolean exit = false;
    while (!exit) {
        IO.print(PROMPT);
        String line = IO.readln();
        Cmd cmd = parseCommand(line);
        switch (cmd) {
            case Cmd.ECHO(String value) -> IO.println(value);
            case Cmd.ERR(String msg) -> IO.println(msg);
            case Cmd.EXIT _ -> exit = true;
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
        case EXIT -> new Cmd.EXIT();
        case ECHO -> {
            String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
            yield new Cmd.ECHO(value);
        }
        case null -> new Cmd.ERR(COMMAND_NOT_FOUND_MSG.formatted(lineArgs[0]));
    };
}

enum Command {
    EXIT,
    ECHO;

    static Optional<Command> getByName(String name) {
        if (name == null || name.isBlank()) {
            return Optional.empty();
        }
        String str = name.toUpperCase();
        return Arrays.stream(Command.values())
                .filter(cm -> cm.name().equals(str))
                .findFirst();
    }
}

