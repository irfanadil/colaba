package com.rnsoft.colabademo

data class LoModelClass(
    val loImage: String = "https://via.placeholder.com/150",
    val loName: String = "$0",
    val loAffiliation: String  = "Footer Title"
    //val listenerAttached: View.OnClickListener= View.OnClickListener {  }
)

fun getSampleLoDetails():ArrayList<LoModelClass>{
    val loModelClass = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "Adil" , loAffiliation = "Add Bank Account")
    val loModelClass2 = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "Sajid" , loAffiliation = "Add Retirement Account")
    val loModelClass3 = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "Muzamil" , loAffiliation = "Add Financial Assets")
    val loModelClass4 = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "kashif" , loAffiliation = "Add Proceeds From Transaction")
    val loModelClass5 = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "Jahanzeb" , loAffiliation = "Add Gifts Account")
    val loModelClass6 = LoModelClass( loImage = "https://via.placeholder.com/150", loName = "Noman" , loAffiliation = "Add Other Assets")

    val modelArrayList:ArrayList<LoModelClass> = arrayListOf()
    modelArrayList.add(loModelClass)
    modelArrayList.add(loModelClass2)
    modelArrayList.add(loModelClass3)
    modelArrayList.add(loModelClass4)
    modelArrayList.add(loModelClass5)
    modelArrayList.add(loModelClass6)
    return modelArrayList
}