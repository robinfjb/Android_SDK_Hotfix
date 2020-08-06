package robin.sdk.service_dynamic.net;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class DyInfoTest {
    String data = "{\n" +
            "    \"checksum\": {\n" +
            "        \"TYPE\": \"md5\",\n" +
            "        \"value\": \"315a298e0c94f476d9ada70fe462c1b7\"\n" +
            "    },\n" +
            "    \"version\": 2,\n" +
            "    \"subVersion\": 0,\n" +
            "    \"package\": \"http://robinfjb.github.io/classes.jar\",\n" +
            "    \"className\": \"robin.sdk.sdk_impl.ServiceImpl2\"\n" +
            "}";

    @Test
    public void toJsonStr() {
        String dyStr = null;
        try {
            DyInfo dyInfo = new DyInfo(new JSONObject(data));
            dyStr = dyInfo.toJsonStr();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertEquals(dyStr, data);
    }

    @Test
    public void testToString() {


    }
}