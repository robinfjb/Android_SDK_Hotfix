package robin.sdk.sdk_impl;

public class BizTest {
    BizSubTest bizSubTest;
    public BizTest() {
        bizSubTest = new BizSubTest();
    }

    public String testStr() {
        return bizSubTest.testStr();
    }

    public static String testStatic() {
        return BizSubTest.testStatic();
    }
}
