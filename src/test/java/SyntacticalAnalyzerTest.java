import com.company.business.LexicalAnalyzer;
import com.company.business.SyntacticalAnalyzer;
import com.company.exception.ParserException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class SyntacticalAnalyzerTest {

    private LexicalAnalyzer lexicalAnalyzer;

    @Before
    public void init() {
        SyntacticalAnalyzer syntacticalAnalyzer = new SyntacticalAnalyzer();
        lexicalAnalyzer = new LexicalAnalyzer(syntacticalAnalyzer);
    }

    @Test
    public void test1() {
        test("SELECT * FROM SOME_TABLE123");
    }

    @Test
    public void test2() {
        test("SELECT col1 FROM SOME_TABLE123");
    }

    @Test
    public void test3() {
        test("SELECT col1, col2 FROM SOME_TABLE123");
    }

    @Test
    public void test4() {
        test("SELECT col1 AS alias1, col2 AS alias2 FROM SOME_TABLE123");
    }

    @Test
    public void test5() {
        test("SELECT col1, col2 AS alias2 FROM SOME_TABLE123");
    }

    @Test
    public void test6() {
        test("SELECT col1 AS alias1, col2 FROM SOME_TABLE123");
    }

    @Test
    public void test7() {
        test("SELECT col1, col2 FROM SOME_TABLE123, SOME_TABLE456");
    }

    private void test(String query) {
        try {
            lexicalAnalyzer.parse(query);
        } catch (ParserException ex) {
            throw new RuntimeException(ex);
        }
    }

    private void testError(String query) {
        try {
            lexicalAnalyzer.parse(query);
        } catch (Throwable throwable) {
            return;
        }
        Assert.fail("Incorrect query executed: " + query);
    }
}