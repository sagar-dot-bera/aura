package com.aura.command;

/**
 * Command for refunding a transaction.
 * Stub implementation for now.
 */
public class RefundCommand implements Command {
    private String transactionId;

    public RefundCommand(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public void execute() {
        // TODO: Implement refund logic
        // This would involve:
        // 1. Looking up the original transaction
        // 2. Returning items to inventory
        // 3. Processing refund
    }

    @Override
    public void undo() {
        // TODO: Undo a refund (reverse the refund)
    }

    @Override
    public String log() {
        return "REFUND transactionId=" + transactionId;
    }
}
