package co.com.nequi.model.exceptions;

import co.com.nequi.model.enums.DomainMessage;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    private final DomainMessage domainMessage;

    public UserException(String message, DomainMessage domainMessage) {
        super(message);
        this.domainMessage = domainMessage;
    }

    public UserException(DomainMessage domainMessage) {
        super(domainMessage.getMessage());
        this.domainMessage = domainMessage;
    }

    public UserException(Throwable throwable, DomainMessage technicalMessage) {
        super(throwable);
        this.domainMessage = technicalMessage;
    }
}
