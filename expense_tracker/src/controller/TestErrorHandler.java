package controller;

public class TestErrorHandler implements ErrorHandler {
    private boolean errorReported = false;

    @Override
    public void reportError(String message) {
        errorReported = true;
    }

    public boolean isErrorReported() {
        return errorReported;
    }
}
