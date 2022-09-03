package com.rnsoft.colabademo

class CategoryDocsResponse : ArrayList<CategoryDocsResponseItem>()

data class CategoryDocsResponseItem(
        val catId: String,
        val catName: String,
        val documents: ArrayList<Document>
)

data class Document(
        val docMessage: String,
        val docType: String,
        val docTypeId: String,
        val isCommonlyUsed: Boolean,
        var locallySelected:Boolean = false
)