private static final String PROMPT = "$ ";

void main() {
    final FileManager fileManager = new FileManager();
    final CommandExecutor commandExecutor = new CommandExecutor(fileManager);
    final FileExecutor fileExecutor = new FileExecutor(fileManager);
    for (; ; ) {
        IO.print(PROMPT);
        String line = IO.readln();
        final List<String> lineArgs = StringUtils.parse(line);
        if (!lineArgs.isEmpty()) {
            Optional<CommandBuiltin> commandBuiltinOpt = CommandBuiltin.getByName(lineArgs.getFirst());
            if (commandBuiltinOpt.isPresent()) {
                commandExecutor.executeCommand(commandBuiltinOpt.get(), lineArgs);
            } else {
                fileExecutor.executeFile(lineArgs);
            }
        }
    }
}