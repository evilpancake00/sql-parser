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

    @Test
    public void test8() {
        test("SELECT col1, col2 FROM SOME_TABLE123 tbl1, SOME_TABLE456 tbl2");
    }

    @Test
    public void test9() {
        test("SELECT col1, col2 FROM SOME_TABLE123 tbl1, SOME_TABLE456, SOME_TABLE789");
    }

    @Test
    public void test10() {
        test("SELECT col1, col2 FROM SOME_TABLE123 JOIN SOME_TABLE456 ON a = b");
    }

    @Test
    public void test11() {
        test("SELECT col1, col2 FROM SOME_TABLE123 INNER JOIN SOME_TABLE456 ON a = b");
    }

    @Test
    public void test12() {
        test("SELECT col1, col2 FROM SOME_TABLE123 LEFT JOIN SOME_TABLE456 ON a = b");
    }

    @Test
    public void test13() {
        test("SELECT col1, col2 FROM SOME_TABLE123 RIGHT JOIN SOME_TABLE456 ON a = b");
    }

    @Test
    public void test14() {
        test("SELECT col1, col2 FROM SOME_TABLE123 tbl1 JOIN SOME_TABLE456 ON a = b");
    }

    @Test
    public void test15() {
        test("SELECT * FROM TABLE1 JOIN TABLE2 ON a = b JOIN TABLE3 ON c = d");
    }

    @Test
    public void test16() {
        test("SELECT * FROM TABLE1 JOIN TABLE2 ON a = b INNER JOIN TABLE3 ON c = d");
    }

    @Test
    public void test17() {
        test("SELECT * FROM TABLE1 JOIN TABLE2 ON a = b LEFT JOIN TABLE3 ON c = d");
    }

    @Test
    public void test18() {
        test("SELECT * FROM TABLE1 JOIN TABLE2 ON a = b RIGHT JOIN TABLE3 ON c = d");
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