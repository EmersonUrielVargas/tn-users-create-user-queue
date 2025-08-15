package co.com.nequi.model.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum DomainMessage {

    INTERNAL_ERROR("0-000", "0-000","Something went wrong, please try again"),
    INTERNAL_ERROR_PERSISTENCE_ADAPTER("0-099", "0-099","Something went wrong with persistence adapter, please try " +
            "again"),
    USER_CREATION_FAIL("0-001","0-001","An error occurred while creating the user");

    private final String code;
    private final String externalCode;
    private final String message;

    public static DomainMessage findByExternalCode(String code) {
        return Arrays.stream(DomainMessage.values())
                .filter(msg -> msg.getExternalCode().equalsIgnoreCase(code))
                .findFirst()
                .orElse(INTERNAL_ERROR);
    }
}