package wyvern.tools.tests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import wyvern.stdlib.Globals;
import wyvern.target.corewyvernIL.support.BreakException;
import wyvern.target.corewyvernIL.support.Util;
import wyvern.tools.errors.ErrorMessage;
import wyvern.tools.imports.extensions.WyvernResolver;
import wyvern.tools.parsing.coreparser.ParseException;
import wyvern.tools.tests.suites.RegressionTests;

@Category(RegressionTests.class)
public class AlgebraicTests {

    private static final String BASE_PATH = TestUtil.BASE_PATH;
    private static final String ALGEBRAIC_PATH = BASE_PATH + "algebraic/";

    @Before
    public void setup() {
        Globals.resetState();
    }

    @BeforeClass
    public static void setupResolver() {
        TestUtil.setPaths();
        WyvernResolver.getInstance().addPath(ALGEBRAIC_PATH);
    }

    @Test
    public void testNonStablePrevention() throws ParseException, BreakException {
        TestUtil.doTestScriptModularlyFailing( "notStableTypePrevention", ErrorMessage.MISTAKEN_DSL); // TODO make this error message match
    }

}
