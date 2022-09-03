package com.rnsoft.colabademo


data class TabBorrowerList(
     val id: Int? = null,
     val name: String? = null,
     val coName: String? = null,
     val isFooter:Boolean = false
)


data class TabRealStateList(
     val id: Int? = null,
     val propertyAddress: String? = null,
     val propertyType: String? = null,
     val isFooter:Boolean = false
)


data class TabGovtQuestionList(
     val id: Int? = null,
     val questionTitle: String? = null,
     val question: String? = null,
     val questionYes: String? = null,
     val questionNo: String? = null,
     val questionNa: String? = null,
     val questionValueYes: String? = null,
     val questionValueNo: String? = null,
)