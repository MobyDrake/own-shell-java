private static final String PROMPT = "$ ";
private static final String COMMAND_NOT_FOUND_MSG = "%s: command not found";
private static final String SHELL_BUILTIN = "%s is a shell builtin";
private static final String NOT_FOUND = "%s: not found";
private static final String NO_SUCH_FILE_OR_DIRECTORY = "cd: %s: No such file or directory";

private static final String[] PATH =
        Objects.toString(System.getenv("PATH"), "").split(File.pathSeparator);
private static final Path HOME_PATH =
        Path.of(Objects.toString(System.getenv("HOME"), System.getProperty("user.home")));

private static Path currentPath = Path.of("");

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
            case Cmd.PWD(String value) -> IO.println(value);
            case Cmd.EXECUTE(String[] args) -> {
                final String fileName = args[0];
                Optional<File> optionalFile = findFileInPath(fileName);
                if (optionalFile.isPresent()) {
                    executeFile(args);
                } else {
                    IO.println(NOT_FOUND.formatted(fileName));
                }
            }
            case Cmd.CD(String value) -> {
                if ("~".equals(value)) {
                    currentPath = HOME_PATH;
                } else {
                    Path path = currentPath.resolve(value).toAbsolutePath().normalize();
                    if (Files.isDirectory(path)) {
                        currentPath = path;
                    } else {
                        IO.println(NO_SUCH_FILE_OR_DIRECTORY.formatted(value));
                    }
                }
            }
        }
    }
}

private static Cmd parseCommand(final String line) {
    if (line == null || line.isBlank()) {
        return new Cmd.ERR(COMMAND_NOT_FOUND_MSG);
    }
    final String[] lineArgs = line.trim().split(" ");
    final String lineCmd = lineArgs[0];
    return CommandBuiltin.getByName(lineCmd)
                         .map(commandBuiltin -> createBuiltinCmd(commandBuiltin, lineArgs))
                         .orElseGet(() -> new Cmd.EXECUTE(lineArgs));
}

private static Cmd createBuiltinCmd(final CommandBuiltin commandBuiltin, final String[] lineArgs) {
    return switch (commandBuiltin) {
        case null -> new Cmd.ERR(COMMAND_NOT_FOUND_MSG.formatted(lineArgs[0]));
        case EXIT -> new Cmd.EXIT();
        case ECHO -> {
            String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
            yield new Cmd.ECHO(value);
        }
        case TYPE -> getTypeCmd(lineArgs);
        case PWD -> new Cmd.PWD(currentPath.toAbsolutePath().toString());
        case CD -> {
            String value = Arrays.stream(lineArgs, 1, lineArgs.length).collect(Collectors.joining(" "));
            yield new Cmd.CD(value);
        }
    };
}

private static Cmd getTypeCmd(final String[] lineArgs) {
    final String typedArg = lineArgs[1];
    Optional<CommandBuiltin> commandOpt = CommandBuiltin.getByName(typedArg);
    final String value = commandOpt.map(command -> SHELL_BUILTIN.formatted(command.name().toLowerCase()))
                                   .orElseGet(() -> findFileInPath(typedArg)
                                           .map(f -> "%s is %s".formatted(typedArg, f.getPath()))
                                           .orElse(NOT_FOUND.formatted(typedArg)));
    return new Cmd.TYPE(value);
}

private static Optional<File> findFileInPath(final String fileName) {
    return Arrays.stream(PATH)
                 .map(dir -> new File(dir, fileName))
                 .filter(f -> f.exists() && f.canExecute())
                 .findFirst();
}

private static void executeFile(final String[] args) {
    try {
        ProcessBuilder pb = new ProcessBuilder(args);
        pb.redirectErrorStream(true); // Merges stderr into stdout
        Process process = pb.start();
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line;
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
        process.waitFor();
    } catch (IOException | InterruptedException e) {
        throw new RuntimeException(e);
    }
}