package ru.study.model

data class Join(val joinType: JoinType, val tableSource: TableSource, val restriction: Restriction)