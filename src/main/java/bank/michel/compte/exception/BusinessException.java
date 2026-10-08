package bank.michel.compte.exception;

public class BusinessException extends RuntimeException {          // conflit métier -> 409
    public BusinessException(String message) { super(message); }
}
