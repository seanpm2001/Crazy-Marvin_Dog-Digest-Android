package info.frangor.android;

import android.test.ActivityInstrumentationTestCase2;

/**
 * TODO
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class info.frangor.rocks.poopjournal.dogdigest.android.LaiCareTest \
 * info.frangor.rocks.poopjournal.dogdigest.android.tests/android.test.InstrumentationTestRunner
 */
public class LaiCareTest extends ActivityInstrumentationTestCase2<LaiCare> {

    public LaiCareTest() {
        super("info.frangor.rocks.poopjournal.dogdigest.android", LaiCare.class);
    }

}
