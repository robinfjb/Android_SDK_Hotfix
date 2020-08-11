package robin.sdk.service_dynamic.net;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class DyInfo implements Serializable {
    public String targetVersion;//目标版本号
    public int subVersion;//补丁版本号
    public String packageUrl;
    public String checksumType;
    public String checksumValue;
    public String className;
    public String channel;

    public DyInfo(JSONObject jsonObject) {
        targetVersion = jsonObject.optString("version");
        subVersion = jsonObject.optInt("subVersion", -1);
        packageUrl = jsonObject.optString("package");
        className = jsonObject.optString("className");
        channel = jsonObject.optString("channel");
        JSONObject checksum = jsonObject.optJSONObject("checksum");
        if (checksum != null) {
            checksumType = checksum.optString("TYPE");
            checksumValue = checksum.optString("value");
        }
    }

    public String toJsonStr() {
        JSONObject object = new JSONObject();
        try {
            object.putOpt("version", targetVersion);
            object.putOpt("subVersion", subVersion);
            object.putOpt("package", packageUrl);
            object.putOpt("className", className);
            object.putOpt("channel", channel);
            JSONObject checksum = new JSONObject();
            checksum.put("TYPE", checksumType);
            checksum.put("value", checksumValue);
            object.putOpt("checksum", checksum);
        } catch (JSONException e) {
        }
        return object.toString();
    }

    @Override
    public String toString() {
        return "DyInfo{" +
                "version=" + targetVersion +
                ", subVersion=" + subVersion +
                ", packageUrl='" + packageUrl + '\'' +
                ", checksumType='" + checksumType + '\'' +
                ", checksumValue='" + checksumValue + '\'' +
                ", className='" + className + '\'' +
                '}';
    }
}