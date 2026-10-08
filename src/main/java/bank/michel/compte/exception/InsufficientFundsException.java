package bank.michel.compte.exception;

public class InsufficientFundsException extends RuntimeException { // -> 422
    public InsufficientFundsException(String message) { super(message); }
}
