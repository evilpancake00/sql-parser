package ru.study.model

sealed class Restriction
object All: Restriction()
data class And(val restrictions: List<Restriction>): Restriction()
data class Or(val restrictions: List<Restriction>): Restriction()
data class Eq(val column: String, val value: String): Restriction()
data class Lt(val column: String, val value: String): Restriction()
data class Gt(val column: String, val value: String): Restriction()
data class Le(val column: String, val value: String): Restriction()
data class Ge(val column: String, val value: String): Restriction()