package im.nucker.command;

public interface CommandProvider {
    Command command(String alias);
}
