package gigadot.semsci.converters.chem.uri;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.junit.Assert;
import org.junit.Test;

public class DefaultURIUUIDGeneratorTest {

    private static String hexChars(int n) {
        return HEXCHAR + "{" + n + "}";
    }
    private static String HEXCHAR = "[0123456789abcdefABCDEF]";
    private static Pattern uuidRegex = Pattern.compile("uri:uuid:"
            + hexChars(8) + "-" + hexChars(4) + "-" + hexChars(4) + "-"
            + hexChars(4) + "-" + hexChars(12));

    @Test
    public void checkSyntax() {
        DefaultURIUUIDGenerator uriGen = new DefaultURIUUIDGenerator();
        String uuid = uriGen.createUUIDURI(null).toString();
        Assert.assertTrue("UUID should match regex, but was " + uuid, uuidRegex.matcher(uuid).matches());
    }

    @Test
    public void checkRandom() {
        long t0 = System.currentTimeMillis();
        int testSize = 10000;
        Set<String> results = new HashSet<String>(testSize);
        DefaultURIUUIDGenerator uriGen = new DefaultURIUUIDGenerator();
        for (int i = 0; i < testSize; i++) {
            String uuid = uriGen.createUUIDURI(null).toString();
            Assert.assertTrue("UUID should match regex", uuidRegex.matcher(uuid).matches());
            Assert.assertTrue(results.add(uuid));
        }
        System.err.println("Test for " + testSize + " took "
                + (System.currentTimeMillis() - t0));
    }
}
