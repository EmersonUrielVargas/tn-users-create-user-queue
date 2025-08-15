package co.com.nequi.sqs.listener.util;

import co.com.nequi.sqs.listener.constants.SqsConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import static net.logstash.logback.argument.StructuredArguments.kv;

@Slf4j
@UtilityClass
public class SqsUtilities {

    private static final ObjectMapper mapper = new ObjectMapper();

    public static <T> T parserStringToObject(String messageBody, Class<T> type) {
        T objectData = null;
        try {
            objectData = mapper.readValue(messageBody,type);
        } catch (Exception exception) {
            log.info(SqsConstants.ERROR_PARSING_BODY_MESSAGE, kv(SqsConstants.LOG_PARSING_BODY_KEY, exception));
        }
        return objectData;
    }
}
