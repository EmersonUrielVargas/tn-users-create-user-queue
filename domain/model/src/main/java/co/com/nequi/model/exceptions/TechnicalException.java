package co.com.nequi.model.exceptions;

import co.com.nequi.model.enums.DomainMessage;
import lombok.Getter;

@Getter
public class TechnicalException extends UserException {

    public TechnicalException(DomainMessage domainMessage) {
        super(domainMessage);
    }

    public TechnicalException(Throwable exception, DomainMessage technicalMessage) {
        super(exception, technicalMessage);
    }
}
