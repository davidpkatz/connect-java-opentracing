package cd.connect.tracing.jersey;

import cd.connect.tracing.HeaderLoggingConfiguration;
import cd.connect.tracing.HeaderLoggingConfigurationSource;
import org.slf4j.MDC;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * This looks for any logging parameters on the MDC and adds them to the outgoing HTTP
 * headers. The ServerContextPassFilter picks it up on the other side.
 *
 * @author Richard Vowles - https://google.com/+RichardVowles
 */
public class ClientContextPassFilter implements ClientRequestFilter, HeaderLoggingConfigurationSource {
  private final HeaderLoggingConfiguration headerLoggingConfiguration;

  public ClientContextPassFilter(HeaderLoggingConfiguration headerLoggingConfiguration) {
    this.headerLoggingConfiguration = headerLoggingConfiguration;
  }

  @Override
  public void filter(ClientRequestContext context) throws IOException {
    final MultivaluedMap<String, Object> headers = context.getHeaders();

    if (MDC.get("originApp") == null && headerLoggingConfiguration.getAppName() != null) {
      MDC.put("originApp", headerLoggingConfiguration.getAppName());
    }

    if (MDC.get("appName") == null && headerLoggingConfiguration.getAppName() != null) {
      MDC.put("appName", headerLoggingConfiguration.getAppName());
    }

    // if we don't have a id, add one
    if (MDC.get("request-id") == null) {
      MDC.put("request-id", headerLoggingConfiguration.getAppName() + ":" + UUID.randomUUID().toString());
    }

    Map<String, String> allowedHeaders = headerLoggingConfiguration.getHeaderToLoggingMapping();

//    logContext.entrySet().stream().forEach(logEntry -> {
//      // otherwise we have escaping problems, so we pass what keys we are passing in loggingHeaders
//      // and then write each key as its own header.
//      String value = logEntry.getValue().toString();
//
//      if (logEntry.getValue() != null && value.length() > 0) {
//        if ( allowedHeaders.contains(logEntry.getKey())) {
//          headers.add(PROP_HEADER, logEntry.getKey());
//          headers.add(logEntry.getKey(), value);
//        }
//      }
//    });
  }

  @Override
  public List<String> getHeaderLoggingConfig() {
    return Arrays.asList("request-id", "originApp");
  }
}
