package robin.sdk.service_dynamic.net;

import org.json.JSONObject;

public class DyInfo {
    public int version;
    public int minSupportVersion;
    public String releaseTime;
    public String packageUrl;
    public String checksumType;
    public String checksumValue;
    public String packageName;

    public DyInfo(JSONObject jsonObject) {
        version = jsonObject.optInt("version", -1);
        minSupportVersion = jsonObject.optInt("minSupportVersion", -1);
        releaseTime = jsonObject.optString("releaseTime");
        packageUrl = jsonObject.optString("package");
        packageName = jsonObject.optString("packageName");
        JSONObject checksum = jsonObject.optJSONObject("checksum");
        if (checksum != null) {
            checksumType = checksum.optString("TYPE");
            checksumValue = checksum.optString("value");
        }
    }

    @Override
    public String toString() {
        return "DyInfo{" +
                "version=" + version +
                ", minSupportVersion=" + minSupportVersion +
                ", releaseTime='" + releaseTime + '\'' +
                ", packageUrl='" + packageUrl + '\'' +
                ", checksumType='" + checksumType + '\'' +
                ", checksumValue='" + checksumValue + '\'' +
                ", packageName='" + packageName + '\'' +
                '}';
    }
}