package cd.connect.tracing.jersey;

import cd.connect.tracing.HeaderLoggingConfiguration;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;

/**
 * Look for a header value called "loggingHeaders" and if it exists, grab all of the values and push them
 * into the logging context
 *
 * @author Richard Vowles - https://google.com/+RichardVowles
 */
@Priority(Integer.MAX_VALUE)
public class ServerContextPassFilter implements ContainerRequestFilter, ContainerResponseFilter {
  private final ThreadLocal<Long> processingTimeMs = new ThreadLocal<>();
  private Logger log = LoggerFactory.getLogger(getClass());
  private final HeaderLoggingConfiguration headerLoggingConfiguration;

  public ServerContextPassFilter(HeaderLoggingConfiguration headerLoggingConfiguration) {
    this.headerLoggingConfiguration = headerLoggingConfiguration;
  }

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
//    MultivaluedMap<String, String> headers = requestContext.getHeaders();
//
//    Map<String, String> validHeaders = headerLoggingConfiguration.getHeaderToLoggingMapping();
//    loggingHeaders.forEach(header -> {
//      List<String> headerKeys = Splitter.on(",").splitToList(header);
//
//      headerKeys.stream().forEach(headerKey -> {
//        headerLoggingConfiguration.headerPresent(headerKey, nh -> {
//          MDC.put(nh, String.join(" ", headers.get(headerKey)));
//        });
//      });
//    });
//
//    String appName = System.getProperty("app.name");
//
//    if (!ThreadContext.getContext().containsKey("originApp") && appName != null) {
//      ThreadContext.put("originApp", appName);
//    }
//
//    // if we don't have a id, add one
//    if (!ThreadContext.getContext().containsKey("request-id")) {
//      ThreadContext.put("request-id", appName + ":" + UUID.randomUUID().toString());
//    }
//
//    processingTimeMs.set(System.currentTimeMillis());
  }


  @Override
  public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
    try {
      Long start = processingTimeMs.get();

      int end = (start == null) ? 0 : (int)(System.currentTimeMillis() - start);

      LoggingContextResponse.toJsonLog(responseContext.getStatus(), end);

      if (responseContext.getStatus() >= 500) {
        log.error("request-complete");
      } else {
        log.debug("request-complete");
      }
    } finally {
      ThreadContext.clearAll();
      processingTimeMs.remove();
    }
  }
}
