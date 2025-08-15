package co.com.nequi.model.exceptions;

import co.com.nequi.model.enums.DomainMessage;
import lombok.Getter;

@Getter
public class BusinessException extends UserException {

    public BusinessException(DomainMessage domainMessage) {
        super(domainMessage);
    }

    public BusinessException(String message, DomainMessage technicalMessage) {
        super(message, technicalMessage);
    }
}
