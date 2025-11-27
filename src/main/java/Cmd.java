public sealed interface Cmd {

    record EXIT() implements Cmd {}
    record ERR(String value) implements Cmd {}
    record ECHO(String value) implements Cmd {}
}
