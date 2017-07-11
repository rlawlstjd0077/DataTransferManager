package log;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * 장비 명 설정 정보.
 */
public class NameConfig {
  private Site site;
  private Subsystem subsystem = Subsystem.UNKNOWN;
  private String hardware = "NA";
  private String process = "NA";

  public NameConfig(Site site,
                    Subsystem subsystem,
                    String hardware,
                    String process) {
    this.site = site;
    this.subsystem = subsystem;
    this.hardware = hardware;
    this.process = process;
  }

  public Site getSite() {
    return site;
  }

  public Subsystem getSubsystem() {
    return subsystem;
  }

  public String getHardware() {
    return hardware;
  }

  public String getProcess() {
    return process;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }

    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }

    NameConfig that = (NameConfig) obj;

    return new EqualsBuilder()
            .append(site, that.site)
            .append(subsystem, that.subsystem)
            .append(hardware, that.hardware)
            .append(process, that.process)
            .isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37)
            .append(site)
            .append(subsystem)
            .append(hardware)
            .append(process)
            .toHashCode();
  }

  public String getSubsystemHardware() {
    return String.format("%s (%s)", subsystem.toString(), hardware);
  }
}
