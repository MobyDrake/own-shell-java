private static final String PROMPT = "$ ";

void main() {
    final FileManager fileManager = new FileManager();
    final CommandExecutor commandExecutor = new CommandExecutor(fileManager);
    final FileExecutor fileExecutor = new FileExecutor(fileManager);
    for (; ; ) {
        IO.print(PROMPT);
        String line = IO.readln();

        if (line != null && !line.isBlank()) {
            final String[] lineArgs = line.trim().split(" ");
            final String lineCmd = lineArgs[0];
            Optional<CommandBuiltin> commandBuiltinOpt = CommandBuiltin.getByName(lineCmd);
            if (commandBuiltinOpt.isPresent()) {
                commandExecutor.executeCommand(commandBuiltinOpt.get(), lineArgs);
            } else {
                fileExecutor.executeFile(lineArgs);
            }
        }
    }
}