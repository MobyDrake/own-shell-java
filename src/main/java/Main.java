private static final String PROMPT = "$ ";
private static final String COMMAND_NOT_FOUND_MSG = "%s: command not found";
private static final String EXIT_COMMAND = "exit";

void main() {
    boolean exit = false;
    while (!exit) {
        IO.print(PROMPT);
        String line = IO.readln();
        if (EXIT_COMMAND.equals(line)) {
            exit = true;
        } else {
            IO.println(COMMAND_NOT_FOUND_MSG.formatted(line));
        }
    }
}
