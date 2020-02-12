package ru.study

import grammar.SqlLexer
import grammar.SqlParser
import grammar.SqlParser.*
import grammar.SqlParserBaseVisitor
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import ru.study.model.*

class Parser {
    companion object {
        fun parse(text: String): Query {
            val lexer = SqlLexer(CharStreams.fromString(text.toUpperCase()))
            val tokens = CommonTokenStream(lexer)
            val parser = SqlParser(tokens)
            parser.addErrorListener(ErrorListener)
            return getQuery(parser.select_statement())
        }

        private fun getQuery(ctx: Select_statementContext): Query {
            val querySpecification = ctx.query_expression().query_specification()
            val tableSourceItemJoined = querySpecification.table_sources()?.table_source()
                ?.map { it.table_source_item_joined() }
            return Query(
                querySpecification.select_list().select_list_elem().map(ProjectionVisitor::visit),
                tableSourceItemJoined?.map { it.table_source_item() }?.map(TableSourcesVisitor::visit) ?: emptyList(),
                querySpecification.search_condition()?.accept(RestrictionVisitor) ?: All,
                tableSourceItemJoined?.flatMap { it.join_part() }?.map(JoinVisitor::visit) ?: emptyList(),
                querySpecification.group_by_item()?.map(GroupByVisitor::visit) ?: emptyList(),
                ctx.order_by_clause()?.order_by_expression()?.map(OrderByVisitor::visit) ?: emptyList(),
                ctx.limitClause()?.limit?.text,
                ctx.limitClause()?.offset?.text
            )
        }

        private fun getJoinType(ctx: Join_partContext) = when (ctx.join_type?.text) {
            "LEFT" -> JoinType.LEFT
            "RIGHT" -> JoinType.RIGHT
            "FULL" -> JoinType.FULL
            else -> JoinType.INNER
        }
    }

    private object ProjectionVisitor : SqlParserBaseVisitor<Projection>() {
        override fun visitAsterisk(ctx: AsteriskContext) = Asterisk

        override fun visitColumn_elem(ctx: Column_elemContext) = ColumnProjection(
            ctx.column_name.text,
            ctx.table_name()?.text ?: "",
            ctx.as_column_alias()?.column_alias()?.text ?: ""
        )

        override fun visitExpression_elem(ctx: Expression_elemContext) = getVisitor(ctx).visit(ctx.expression())!!

        private fun getVisitor(parent: Expression_elemContext) = object : SqlParserBaseVisitor<Projection>() {
            override fun visitFunction_call(ctx: Function_callContext): Projection = Function(
                ctx.ID().text,
                ctx.full_column_name().map { it.text }
            )

            override fun visitBracket_expression(ctx: Bracket_expressionContext) = visit(ctx.subquery())

            override fun visitSubquery(ctx: SubqueryContext): Projection = Subquery(
                getQuery(ctx.select_statement()),
                parent.as_column_alias()?.column_alias()?.text ?: ""
            )
        }
    }

    private object TableSourcesVisitor : SqlParserBaseVisitor<TableSource>() {
        override fun visitTable_source_item(ctx: Table_source_itemContext) = TableSource(
            ctx.full_table_name().text,
            ctx.as_table_alias()?.table_alias()?.text ?: ""
        )
    }

    private object RestrictionVisitor : SqlParserBaseVisitor<Restriction>() {
        override fun visitSearch_condition(ctx: Search_conditionContext) =
            if (ctx.search_condition_and().size == 1) {
                visit(ctx.search_condition_and()[0])!!
            } else {
                Disjunction(ctx.search_condition_and().map(this::visit))
            }

        override fun visitSearch_condition_and(ctx: Search_condition_andContext) =
            if (ctx.search_condition_not().size == 1) {
                visit(ctx.search_condition_not()[0])!!
            } else {
                Conjunction( ctx.search_condition_not().map(this::visit))
            }

        override fun visitComparison(ctx: ComparisonContext): Restriction {
            val expression1 = ctx.expression()[0].text
            val expression2 = ctx.expression()[1].text
            return when (ctx.comparison_operator().text) {
                "=" -> Eq(expression1, expression2)
                ">" -> Gt(expression1, expression2)
                "<" -> Lt(expression1, expression2)
                ">=" -> Ge(expression1, expression2)
                "<=" -> Le(expression1, expression2)
                else -> All
            }
        }

        override fun visitBrackets(ctx: BracketsContext?) = visit(ctx?.search_condition())!!
    }

    private object JoinVisitor : SqlParserBaseVisitor<Join>() {
        override fun visitJoin_part(ctx: Join_partContext) = Join(
            getJoinType(ctx),
            TableSourcesVisitor.visit(ctx.table_source()),
            RestrictionVisitor.visit(ctx.search_condition())
        )
    }

    private object GroupByVisitor : SqlParserBaseVisitor<Groupping>() {
        override fun visitGroup_by_item(ctx: Group_by_itemContext) = Groupping(ctx.text)
    }

    private object OrderByVisitor : SqlParserBaseVisitor<Ordering>() {
        override fun visitOrder_by_expression(ctx: Order_by_expressionContext) = Ordering(
            ctx.full_column_name().text,
            ctx.DESC() != null
        )
    }
}