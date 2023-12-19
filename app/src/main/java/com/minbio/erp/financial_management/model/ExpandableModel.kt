package com.minbio.erp.financial_management.model

data class ExpandableModel(
    var title: String,
    var isTitleClickable : Boolean,
    var subTitles: ArrayList<ExpandableItems>
)

data class ExpandableItems(
    var key : String,
    var name : String
)