public sealed interface Cmd {

    record EXIT() implements Cmd {}
    record ERR(String value) implements Cmd {}
    record ECHO(String value) implements Cmd {}
    record TYPE(String value) implements Cmd {}
    record EXECUTE(String[] lineArgs) implements Cmd {}
    record PWD(String value) implements Cmd {}
    record CD(String value) implements Cmd {}
}
