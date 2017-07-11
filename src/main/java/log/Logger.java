package log;


import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.rolling.RollingFileAppender;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/**
 * GK2 지상시스템에서 사용하는 로그 형식을 위한 로거.
 */
public class Logger {
  private static final Marker ERR = MarkerFactory.getMarker(LogLevel.ERR.name());
  private static final Marker WAR = MarkerFactory.getMarker(LogLevel.WAR.name());
  private static final Marker INF = MarkerFactory.getMarker(LogLevel.INF.name());
  private static final Marker EXT = MarkerFactory.getMarker(LogLevel.EXT.name());
  private static final Marker INT = MarkerFactory.getMarker(LogLevel.INT.name());
  private static final Marker PRC = MarkerFactory.getMarker(LogLevel.PRC.name());
  private static final Marker DEB = MarkerFactory.getMarker(LogLevel.DEB.name());

  private org.slf4j.Logger logger;

  public Logger(org.slf4j.Logger logger) {
    this.logger = logger;
  }

  /**
   * Error 메시지를 작성.
   *
   * @param log Log 메시지
   */
  public void error(String log) {
    logger.error(Logger.ERR, log);
  }

  /**
   * Error 메시지와 디버깅을 위한 Exception 작성.
   *
   * @param log Log 메시지
   * @param e   Exception 정보
   */
  public void error(String log, Throwable e) {
    logger.error(Logger.ERR, log, e);
  }

  /**
   * Warn 메시지를 작성.
   *
   * @param log Warn 메시지
   */
  public void warn(String log) {
    logger.warn(Logger.WAR, log);
  }

  /**
   * Info 메시지를 작성.
   *
   * @param log Info 메시지
   */
  public void info(String log) {
    logger.info(Logger.INF, log);
  }

  /**
   * External interface 를 작성
   * 외부 자료 수신 시, 시각, 파일명(혹은 ID)를 기록
   * 다른 서브시스템으로 파일 전달 시 Target과 파일명(혹은 ID) 작성
   *
   * @param log EXT 메시지
   */
  public void extInf(String log) {
    logger.info(Logger.EXT, log);
  }

  /**
   * Internal interface 를 작성
   * 외부 자료 수신 시, 시각, 파일명(혹은 ID)를 기록
   * 다른 서브시스템으로 파일 전달 시 Target과 파일명(혹은 ID) 작성
   *
   * @param log INT 메시지
   */
  public void intInf(String log) {
    logger.info(Logger.INT, log);
  }

  /**
   * 프로세스 처리를 작성
   * 모든 기능 수행 시의 시작/종료/처리결과에 대한 내역 기록은 모두 기록
   *
   * @param log Process 메시지
   */
  public void proc(String log) {
    logger.info(Logger.PRC, log);
  }

  /**
   * 디비깅 메시지를 작성.
   *
   * @param log Debug 메시지
   */
  public void debug(String log) {
    logger.debug(Logger.DEB, log);
  }

  /**
   * 디버깅 메시지와 Exception 작성.
   *
   * @param log Log 메시지
   * @param e   Exception 정보
   */
  public void debug(String log, Throwable e) {
    logger.error(Logger.DEB, log, e);
  }

  /**
   * 현재의 Logger에 Appender 추가.
   * @param appender Appender
   */
  public void addAppender(RollingFileAppender<ILoggingEvent> appender) {
    ((ch.qos.logback.classic.Logger)logger).addAppender(appender);
  }
}

