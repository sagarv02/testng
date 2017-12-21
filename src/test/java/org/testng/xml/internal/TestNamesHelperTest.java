package org.testng.xml.internal;

import org.testng.TestNGException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.testng.collections.CollectionUtils;
import org.testng.xml.XmlSuite;
import org.testng.xml.XmlTest;

import test.SimpleBaseTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class TestNamesHelperTest extends SimpleBaseTest {

    @Test
    public void testCloneIfContainsTestsWithNamesMatchingAny() {
        XmlSuite suite = createDummySuiteWithTestNamesAs("test1", "test2");
        TestNamesHelper testNamesHelper = new TestNamesHelper();
        testNamesHelper.cloneIfContainsTestsWithNamesMatchingAny(suite, Collections.singletonList("test2"));
        List<XmlTest> xmlTests = testNamesHelper.getMatchedTests();
        assertThat(suite.getTests()).hasSameElementsAs(xmlTests);
    }

    @Test(description = "GITHUB-1594", dataProvider = "getTestnames")
    public void testCloneIfContainsTestsWithNamesMatchingAnyChildSuites(String testname, boolean foundInParent, boolean foundInChildOfChild) {
        XmlSuite parentSuite = createDummySuiteWithTestNamesAs("test1", "test2");
        parentSuite.setName("parent_suite");
        XmlSuite childSuite = createDummySuiteWithTestNamesAs("test3", "test4");
        childSuite.setName("child_suite");
        parentSuite.getChildSuites().add(childSuite);
        XmlSuite childOfChildSuite = createDummySuiteWithTestNamesAs("test5", "test6");
        childSuite.getChildSuites().add(childOfChildSuite);
        TestNamesHelper testNamesHelper = new TestNamesHelper();
        testNamesHelper.cloneIfContainsTestsWithNamesMatchingAny(parentSuite, Collections.singletonList(testname));
        List<XmlTest> xmlTests = testNamesHelper.getMatchedTests();
        if (foundInParent) {
            assertThat(xmlTests).hasSameElementsAs(parentSuite.getTests());
        } else if (!foundInChildOfChild) {
            assertThat(xmlTests).hasSameElementsAs(childSuite.getTests());
        } else {
            assertThat(xmlTests).hasSameElementsAs(childOfChildSuite.getTests());
        }
    }

    @Test(expectedExceptions = TestNGException.class,
            expectedExceptionsMessageRegExp = "\nPlease provide a valid list of names to check.",
            dataProvider = "getData")
    public void testCloneIfContainsTestsWithNamesMatchingAnyNegativeCondition(XmlSuite xmlSuite, List<String> names) {
        TestNamesHelper testNamesHelper = new TestNamesHelper();
        testNamesHelper.cloneIfContainsTestsWithNamesMatchingAny(xmlSuite, names);
    }
    
    @Test
    public void testIfTestnamesComesFromDifferentSuite() {
        XmlSuite parentSuite = createDummySuiteWithTestNamesAs("test1", "test2");
        parentSuite.setName("parent_suite");
        XmlSuite childSuite = createDummySuiteWithTestNamesAs("test3", "test4");
        childSuite.setName("child_suite");
        parentSuite.getChildSuites().add(childSuite);
        XmlSuite childOfChildSuite = createDummySuiteWithTestNamesAs("test5", "test6");
        childSuite.getChildSuites().add(childOfChildSuite);
        TestNamesHelper testNamesHelper = new TestNamesHelper();
        testNamesHelper.cloneIfContainsTestsWithNamesMatchingAny(parentSuite,
                new ArrayList<>(Arrays.asList("test1", "test3", "test5")));
        List<String> matchedTestnames = testNamesHelper.getMatchedTestNames();
        assertThat(matchedTestnames).hasSameElementsAs(Arrays.asList("test1", "test3", "test5"));
    }
    
    @Test(expectedExceptions = TestNGException.class,
            expectedExceptionsMessageRegExp = "\nThe test\\(s\\) \\<\\[test3\\]\\> cannot be found.")
    public void testCloneIfContainsTestsWithNamesMatchingAnyWithoutMatch() {
        XmlSuite xmlSuite = createDummySuiteWithTestNamesAs("test1", "test2");
        TestNamesHelper testNamesHelper = new TestNamesHelper();
        testNamesHelper.cloneIfContainsTestsWithNamesMatchingAny(xmlSuite, Collections.singletonList("test3"));
        List<XmlSuite> clonedSuites = testNamesHelper.getCloneSuite();
        if (!CollectionUtils.hasElements(clonedSuites)) {
            throw new TestNGException(
                    "The test(s) <" + Collections.singletonList("test3").toString() + "> cannot be found.");
        }
    }

    @DataProvider(name = "getTestnames")
    public Object[][] getTestnameToSearchFor() {
        return new Object[][]{
            { "test4", false, false },
            { "test1", true, false },
            { "test5", false, true }
        };
    }

    @DataProvider(name = "getData")
    public Object[][] getTestData() {
        return new Object[][]{
                {new XmlSuite(), null},
                {new XmlSuite(), Collections.<String>emptyList()}
        };
    }
}
