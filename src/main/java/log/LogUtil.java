package log;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.TimeBasedRollingPolicy;
import org.slf4j.LoggerFactory;


/**
 * LogUtil은 GK2 지상시스템 소프트웨어의 로그를 쉽게 작성하도록 도와주는 유틸리티.
 * Created by lubang on 7/29/16.
 */
public class LogUtil {
  /**
   * 전달되는 이름을 기반으로 GK2 지상시스템 소프트웨어를 위한 Logger 인스턴스를 반환.
   *
   * @param loggerName 로거 이름
   * @return GK2를 위한 Logger 인스턴스
   */
  public static Logger getLogger(String loggerName) {
    return new Logger(LoggerFactory.getLogger(loggerName));
  }

  public static void configure(LogConfig logConfig, NameConfig nameConfig) {
    final String filename = nameConfig.getSubsystem() + "_LOG_%d{yyyy-MM-dd}.log";
    final String filePattern = logConfig.getLogDir() + "/" + filename;

    final String encodingPattern = "%d{yyyy-MM-dd'T'HH:mm:ss'Z',GMT},%marker,"
      + nameConfig.getSubsystem() + ","
      + nameConfig.getHardware() + ","
      + nameConfig.getProcess() + ",%m%n";

    ch.qos.logback.classic.Logger gk2Logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("ui");

    gk2Logger.detachAppender("rollingFileAppender");
    RollingFileAppender<ILoggingEvent> rollingFileAppender = generateRollingAppender("rollingFileAppender",
      encodingPattern,
      filePattern);
    gk2Logger.addAppender(rollingFileAppender);
  }

  /**
   * Appender 생성기 (RollingFilAppender 기반을 GK2로 Custom).
   *
   * @param name            Appender 이름
   * @param encodingPattern 인코딩 패턴
   * @param filePattern     File 이름 패턴
   * @return RollingFileAppender
   */
  public static RollingFileAppender<ILoggingEvent> generateRollingAppender(String name,
                                                                           String encodingPattern,
                                                                           String filePattern) {
    final LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

    final RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
    appender.setName(name);
    appender.setContext(context);

    final TimeBasedRollingPolicy<ILoggingEvent> rollingPolicy = new TimeBasedRollingPolicy<>();
    rollingPolicy.setContext(context);
    rollingPolicy.setParent(appender);
    rollingPolicy.setFileNamePattern(filePattern);
    rollingPolicy.start();

    final PatternLayoutEncoder encoder = new PatternLayoutEncoder();
    encoder.setContext(context);
    encoder.setPattern(encodingPattern);
    encoder.start();

    appender.setEncoder(encoder);
    appender.setRollingPolicy(rollingPolicy);
    appender.start();

    return appender;
  }
}
