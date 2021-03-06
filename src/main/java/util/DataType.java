package util;

/**
 * 파일명의 구분자 정의
 */
public enum DataType {
    ECEF_EPHMEREDES("ECEF"),
    ECI_EPHMEREDES("ECI"),
    EVENT_PREDICTION("_EP_"),
    SKM("SKM"),
    SRM("SRM"),
    FOR("FOR"),
    WOL("_WOL_"),
    WHEEL_OFF_LOADING_TARGET("WOL_TARGET"),
    WHEEL_OFF_LOADING_TIME("WOL_TIME"),
    SAM("SAM"),
    ORBIT_TABLE_UPLOAD("OTB"),
    AMI_LMC("LMC"),
    AMI_TARGET_STAR("TARGET_STAR"),
    SCANNER_CALIBRATION("SCANNER_CAL_TM"),
    WOL_TARGET_MGMT_IMP("IMP_WOL_MGMT"),
    WOL_TIME_MGMT_IMP("IMP_WOL_MGMT"),
    EW_NS_SKM_MGMT_IMP("IMP_SKM_MGMT"),
    SA_SKM_MGMT_IMP("IMP_SKM_MGMT"),
    SR_SKM_MGMT_IMP("IMP_SKM_MGMT"),
    FOR_SKM_MGMT_IMP("IMP_SKM_MGMT"),
    ORBIT_UPLOAD_IMP("IMP_ORBIT_UPLOAD"),
    LAE_BURN_MGMT_IMP("IMP_LAE_BURN_MGMT"),
    AMI_MISSION_UPLOAD_IMP("IMP_AMI_MISSION_UPLOAD"),
    AMI_SOLAR_CALIBRATION_IMP("IMP_AMI_SOLAR_CALIBRATION"),
    AMI_TARGET_STAR_LIST_UPLOAD_IMP("IMP_AMI_TARGET_STAR_LIST_UPLOAD"),
    AMI_SCANNER_CALIBRATION_IMP("IMP_AMI_SCANNER_CALIBRATION"),
    AMI_LMC_DATA_UPLOAD_IMP("IMP_AMI_LOS_DATA_UPLOAD"),
    AMI_SCENE_OFFSET_IMP("IMP_AMI_SCENE_OFFSET"),
    MISSION_REQUEST("MR"),
    MISSION_SCHEDULE("MS"),
    GK2A_MISSION_EVENT("GK2A_EVENT"),
    GK2B_MISSION_EVENT("GK2B_EVENT"),

    // COMS
    COMS_EPHEMERIDES("EPHEMEREDES"),
    COMS_MANEUVER_PLAN_NSSK("NSSK_"),
    COMS_MANEUVER_PLAN_SR("SR_"),
    COMS_EVENT_PREDICTION("EP_"),

    // GK2A MPS
    AMI_SCENE_DEF("N/A"),
    AMI_TIMELINE_DEF("N/A"),
    MANEUVER_PLAN("MANEUVER_PLAN"),

    // GK2A MSS,
    UHRIT_SCHEDULE("UHRIT"),
    HRIT_SCHEDULE("HRIT"),
    LRIT_SCHEDULE("LRIT"),
    XRIT_OP_DATA(".opd"), // extension
    XRIT_AD_DATA(".add"), // extension
    DISSEMINATION_CONVERT_COMMAND("DCC"),
    DISSEMINATION_CONVERT_RESPONSE("DCR"),
    UNKNOWN("UNKNOWN"),

    // GK2A FDS
    LEOP_LAE_BURN_REPORT("LEOP_LAE_BURN_REPORT"),

    MOON_ACQUISITION_CANDIDATE("MAC"),
    GK2B_EPHEMERIDES("GK2B_EPHEMERIDES"),
    FUEL_DATA("FUEL_DT"),
    FOCUSLEOP_LAE_BURN("LAE"),
    FOCUSLEOP_STATION_ACQUISITION("SA_LEOP"),
    FOCUSLEOP_ORBIT_DATA("ORBIT"),
    RNG_GEOSC("RDGEOS"),
    TRK_GEOSC("LEOPTD"),
    ORBIT_DATA_TLE("TLE"),
    POLARIZATION("POLA"),
    RANGING_DATA_ASCII(""),

    // AMI DPS
    AMI_CHANNEL_PARA("AMI_CH_Parameter"),
    L1A_INTERMEDIATE_RESULT(""),
    IMAGE_PRODUCT("");


    private String keyword;

    DataType(String keyword) {
        this.keyword = keyword;
    }

    /**
     * Enum 명의 밑줄을 공백으로 바꾼 데이터 이름
     *
     * @return DataType 명.
     */
    public String toName() {
        return this.toString().replace("_", " ");
    }

    /**
     * 파일 명의 키워드를 이용해 Data Type 분류.
     *
     * @param filename 파일 명.
     * @return DataType Enum.
     */
    public static DataType fromFilename(String filename) {
        for (DataType dataType : values()) {
            if (filename.contains(dataType.keyword)) {
                return dataType;
            }
        }
        return UNKNOWN;
    }
}
