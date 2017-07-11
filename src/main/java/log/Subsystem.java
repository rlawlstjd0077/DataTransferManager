package log;

/**
 * 서브시스템 명칭.
 */
public enum Subsystem {
  EDS,
  EIS,
  AMI_DPS,
  GK2A_MPS,
  GK2A_MPS_CLIENT,
  GK2A_FDS,
  GK2A_PDS,
  GK2A_ITOS,
  GK2A_CCM,
  GK2A_CCM_SA,
  GK2A_MSS,
  AMI_INR,
  AGENT,
  GK2B_FDS,
  FOCUSLEOP,
  NMSC_FTP,
  UNKNOWN;

  /**
   * 가운데 공백, (-)와 같은 요소를 (_) 로 변환한 뒤 Enum으로 변환.
   *
   * @param subsystem 서브시스템 문자열.
   * @return 서브시스템 Enum.
   */
  public static Subsystem fromString(String subsystem) {
    final String validText = subsystem.replace("-", "_").replace(" ", "_");
    return Subsystem.valueOf(validText);
  }

  /**
   * Enum 명의 밑줄을 공백으로 바꾼 데이터 이름
   *
   * @return 서브시스템 명.
   */
  public String toName() {
    return this.toString().replace("_", " ");
  }

  /**
   * 간략한 서브시스템 명칭 (위성, 센서 명 제외, 예: DPS, MSS 등)
   *
   * @return 서브시스템 명칭.
   */
  public String toSimpleName() {
    final String[] split = this.toString().split("_");
    return split[split.length - 1];
  }
}
