package server.model;

public class ActionDeniedException extends Exception{

    public ActionDeniedException(String cause) {
        super(cause);
    }
}
