package log;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 로그 설정.
 */
public class LogConfig {
  private String logDir;
  private String hwStatusDir;
  private String swStatusDir;

  public LogConfig(String logDir,
                   String hwStatusDir,
                   String swStatusDir) {
    this.logDir = logDir;
    this.hwStatusDir = hwStatusDir;
    this.swStatusDir = swStatusDir;
  }

  public String getLogDir() {
    return logDir;
  }

  public String getHwStatusDir() {
    return hwStatusDir;
  }

  public String getSwStatusDir() {
    return swStatusDir;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (!(obj instanceof LogConfig)) {
      return false;
    }

    LogConfig logConfig = (LogConfig) obj;

    return new EqualsBuilder()
            .append(getLogDir(), logConfig.getLogDir())
            .append(getHwStatusDir(), logConfig.getHwStatusDir())
            .append(getSwStatusDir(), logConfig.getSwStatusDir())
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(getLogDir())
            .append(getHwStatusDir())
            .append(getSwStatusDir())
            .toHashCode();
  }
}
