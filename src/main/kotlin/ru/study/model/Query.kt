package ru.study.model

data class Query(
    val projections: List<Projection>,
    val tableSources: List<TableSource>,
    val restriction: Restriction = All,
    val joins: List<Join>,
    val groupings: List<Groupping>,
    val orderings: List<Ordering>,
    val limit: String?,
    val offset: String?
)