package io.ebean.test;

import io.avaje.applog.AppLog;
import io.ebeaninternal.api.SpiLogger;
import io.ebeaninternal.api.SpiLoggerFactory;

import static java.lang.System.Logger.Level.DEBUG;
import static java.lang.System.Logger.Level.TRACE;

/**
 * Create a logger that captures the SQL and register it for later access in tests.
 * <p>
 * The logged SQL is accessed by LoggedSql.
 * </p>
 */
public class CapturingLoggerFactory implements SpiLoggerFactory {

  @Override
  public SpiLogger create(String name) {
    SpiLogger logger = new LogAdapter(AppLog.getLogger(name));
    if (name.equals("io.ebean.SQL")) {
      return LoggedSql.register(logger);
    }
    return logger;
  }

  private static final class LogAdapter implements SpiLogger {

    private final System.Logger logger;

    LogAdapter(System.Logger logger) {
      this.logger = logger;
    }

    @Override
    public boolean isDebug() {
      return logger.isLoggable(DEBUG);
    }

    @Override
    public boolean isTrace() {
      return logger.isLoggable(TRACE);
    }

    @Override
    public void debug(String msg) {
      logger.log(DEBUG, msg);
    }

    @Override
    public void trace(String msg) {
      logger.log(TRACE, msg);
    }
  }
}
