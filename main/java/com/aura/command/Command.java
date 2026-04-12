package com.aura.command;

/**
 * Command pattern: encapsulates a request as an object.
 */
public interface Command {
    /**
     * Execute the command.
     */
    void execute();

    /**
     * Undo the command (revert its effects).
     */
    void undo();

    /**
     * Get a log entry describing this command.
     * 
     * @return Log string
     */
    String log();
}
