package robin.sdk.sdk_impl;

public class BizSubTest {
    public String testStr() {
        return BizSubTest.class.getName();
    }

    public static String testStatic() {
        return "static " + BizSubTest.class.getName();
    }
}
