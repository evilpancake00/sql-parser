import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.opentest4j.AssertionFailedError
import ru.study.Parser.Companion.parse

class ErrorsTest {
    private var text: String = ""

    @Test
    fun test_multiple_select() {
        text = "SELECT SELECT * FROM TBL1"
    }

    @Test
    fun test_wrong_order() {
        text = "FROM TBL1 SELECT *"
    }

    @Test
    fun test_empty_col() {
        text = "SELECT COL1, , COL3 FROM TBL1"
    }

    @Test
    fun test_empty_last_col() {
        text = "SELECT COL1, , FROM TBL1"
    }

    @Test
    fun test_no_closing_brackets() {
        text = "SELECT COL1, (SELECT COL2 FROM TBL2 WHERE COL2 = \"111\""
    }

    @Test
    fun test_no_closing_brackets_multiple() {
        text = "SELECT COL1, (((SELECT COL2 FROM TBL2 WHERE COL2 = \"111\""
    }

    @Test
    fun test_no_opening_brackets() {
        text = "SELECT COL1, COL2) FROM TBL1 WHERE COL1 = \"111\""
    }

    @AfterEach
    fun after() {
        try {
            parse(text)
        } catch (throwable: Throwable) {
            return
        }
        throw AssertionFailedError()
    }
}