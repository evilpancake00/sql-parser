import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import ru.study.Parser
import ru.study.model.*

class ParserTest {
    private var text: String = ""

    private var projections: List<Projection> = emptyList()
    private var tableSources: List<TableSource> = emptyList()
    private var restriction: Restriction = All
    private var joins: List<Join> = emptyList()
    private var grouppings: List<Groupping> = emptyList()
    private var orderings: List<Ordering> = emptyList()
    private var limit: String? = null
    private var offset: String? = null

    @BeforeEach
    fun before() {
        projections = listOf(Asterisk)
        tableSources = listOf(TableSource("TBL1"))
    }

    @Test
    fun test_asterisk() {
        text = "SELECT * FROM TBL1"
    }

    @Test
    fun test_select() {
        text = "SELECT COL1, COL2, COL3 FROM TBL1"
        projections = listOf(
            ColumnProjection("COL1"),
            ColumnProjection("COL2"),
            ColumnProjection("COL3")
        )
    }

    @Test
    fun test_column_alias() {
        text = "SELECT COL1 AS ALIAS1, COL2 AS ALIAS2, COL3 AS ALIAS3 FROM TBL1"
        projections = listOf(
            ColumnProjection("COL1", alias = "ALIAS1"),
            ColumnProjection("COL2", alias = "ALIAS2"),
            ColumnProjection("COL3", alias = "ALIAS3")
        )
    }

    @Test
    fun test_function() {
        text = "SELECT SUM(COL1, COL2) FROM TBL1"
        projections = listOf(Function("SUM", listOf("COL1", "COL2")))
    }

    @Test
    fun test_select_tables() {
        text = "SELECT * FROM TBL1, TBL2, TBL3"
        tableSources = listOf(TableSource("TBL1"), TableSource("TBL2"), TableSource("TBL3"))
    }

    @Test
    fun test_table_name() {
        text = "SELECT TBL1.COL1, TBL2.COL2 FROM TBL1, TBL2"
        projections = listOf(
            ColumnProjection("COL1", table = "TBL1"),
            ColumnProjection("COL2", table = "TBL2")
        )
        tableSources = listOf(
            TableSource("TBL1"),
            TableSource("TBL2")
        )
    }

    @Test
    fun test_table_alias() {
        text = "SELECT * FROM TBL1 TABLE_ALIAS_1, TBL2 AS TABLE_ALIAS_2"
        tableSources = listOf(
            TableSource("TBL1", "TABLE_ALIAS_1"),
            TableSource("TBL2", "TABLE_ALIAS_2")
        )
    }

    @Test
    fun test_eq() {
        text = "SELECT * FROM TBL1 WHERE COL1 = \"111\""
        restriction = Eq("COL1", "\"111\"")
    }

    @Test
    fun test_lt() {
        text = "SELECT * FROM TBL1 WHERE COL1 < \"111\""
        restriction = Lt("COL1", "\"111\"")
    }

    @Test
    fun test_gt() {
        text = "SELECT * FROM TBL1 WHERE COL1 > \"111\""
        restriction = Gt("COL1", "\"111\"")
    }

    @Test
    fun test_le() {
        text = "SELECT * FROM TBL1 WHERE COL1 <= \"111\""
        restriction = Le("COL1", "\"111\"")
    }

    @Test
    fun test_ge() {
        text = "SELECT * FROM TBL1 WHERE COL1 >= \"111\""
        restriction = Ge("COL1", "\"111\"")
    }

    @Test
    fun test_and() {
        text = "SELECT * FROM TBL1 WHERE COL1 = \"111\" AND COL2 = \"222\""
        restriction = And(listOf(Eq("COL1", "\"111\""), Eq("COL2", "\"222\"")))
    }

    @Test
    fun test_or() {
        text = "SELECT * FROM TBL1 WHERE COL1 = \"111\" OR COL2 = \"222\""
        restriction = Or(listOf(Eq("COL1", "\"111\""), Eq("COL2", "\"222\"")))
    }

    @Test
    fun test_complex() {
        text = "SELECT * FROM TBL1 WHERE COL1 = \"111\" AND COL2 = \"222\" OR COL3 = \"333\""
        restriction = Or(listOf(
            And(listOf(
                Eq("COL1", "\"111\""),
                Eq("COL2", "\"222\"")
            )),
            Eq("COL3", "\"333\"")
        ))
    }

    @Test
    fun test_brackets() {
        text = "SELECT * FROM TBL1 WHERE (COL1 = \"111\" OR COL2 = \"222\") AND COL3 = \"333\""
        restriction = And(listOf(
            Or(listOf(
                Eq("COL1", "\"111\""),
                Eq("COL2", "\"222\"")
            )),
            Eq("COL3", "\"333\"")
        ))
    }

    @Test
    fun test_join() {
        text = "SELECT * FROM TBL1 JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.INNER, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_inner_join() {
        text = "SELECT * FROM TBL1 INNER JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.INNER, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_left_join() {
        text = "SELECT * FROM TBL1 LEFT JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.LEFT, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_right_join() {
        text = "SELECT * FROM TBL1 RIGHT JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.RIGHT, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_full_join() {
        text = "SELECT * FROM TBL1 FULL JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.FULL, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_outer_join() {
        text = "SELECT * FROM TBL1 LEFT OUTER JOIN TBL2 ON COL1 = \"111\""
        joins = listOf(Join(JoinType.LEFT, TableSource("TBL2"), Eq("COL1", "\"111\"")))
    }

    @Test
    fun test_join_multiple() {
        text = "SELECT * FROM TBL1 JOIN TBL2 ON COL1 = \"111\" JOIN TBL3 ON COL2 = \"222\" JOIN TBL4 ON COL3 = \"333\""
        joins = listOf(
            Join(JoinType.INNER, TableSource("TBL2"), Eq("COL1", "\"111\"")),
            Join(JoinType.INNER, TableSource("TBL3"), Eq("COL2", "\"222\"")),
            Join(JoinType.INNER, TableSource("TBL4"), Eq("COL3", "\"333\""))
        )
    }

    @Test
    fun test_group_by() {
        text = "SELECT * FROM TBL1 GROUP BY COL1, COL2, COL3"
        grouppings = listOf(
            Groupping("COL1"),
            Groupping("COL2"),
            Groupping("COL3")
        )
    }

    @Test
    fun test_order_by_multiple() {
        text = "SELECT * FROM TBL1 ORDER BY COL1, COL2 ASC, COL3 DESC"
        orderings = listOf(
            Ordering("COL1"),
            Ordering("COL2"),
            Ordering("COL3", true)
        )
    }

    @Test
    fun test_limit() {
        text = "SELECT * FROM TBL1 LIMIT 1"
        limit = "1"
    }

    @Test
    fun test_limit_offset() {
        text = "SELECT * FROM TBL1 LIMIT 1 OFFSET 10"
        limit = "1"
        offset = "10"
    }

    @Test
    fun test_limit_offset_comma() {
        text = "SELECT * FROM TBL1 LIMIT 10, 1"
        limit = "1"
        offset = "10"
    }

    @Test
    fun test_subquery() {
        text =
            "SELECT COL1, (" +
                "SELECT COL4 FROM TBL3 JOIN TBL4 ON COL5 = COL6 " +
                "WHERE COL4 = \"222\" GROUP BY COL4 ORDER BY COL4 LIMIT 2 OFFSET 20" +
            ") AS ALIAS1 FROM TBL1 JOIN TBL2 ON COL2 = COL3 " +
            "WHERE COL1 = \"111\" GROUP BY COL1 ORDER BY COL1 LIMIT 1 OFFSET 10"
        tableSources = listOf(TableSource("TBL1"))
        joins = listOf(Join(JoinType.INNER, TableSource("TBL2"), Eq("COL2", "COL3")))
        restriction = Eq("COL1", "\"111\"")
        limit = "1"
        offset = "10"
        grouppings = listOf(Groupping("COL1"))
        orderings = listOf(Ordering("COL1"))
        projections = listOf(ColumnProjection("COL1"), Subquery(Query(
            listOf(ColumnProjection("COL4")),
            listOf(TableSource("TBL3")),
            Eq("COL4", "\"222\""),
            listOf(Join(JoinType.INNER, TableSource("TBL4"), Eq("COL5", "COL6"))),
            listOf(Groupping("COL4")),
            listOf(Ordering("COL4")),
            "2",
            "20"
        ), "ALIAS1"))
    }

    @AfterEach
    fun after() {
        assertEquals(
            Query(
                projections,
                tableSources,
                restriction,
                joins,
                grouppings,
                orderings,
                limit,
                offset
            ),
            Parser().parse(text)
        )
    }
}