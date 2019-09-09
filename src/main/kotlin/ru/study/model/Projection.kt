package ru.study.model

sealed class Projection
object Asterisk : Projection()
data class ColumnProjection(val column: String, val table: String = "", val alias: String = ""): Projection()
data class Subquery(val query: Query, val alias: String = ""): Projection()
data class Function(val function: String, val columns: List<String>): Projection()