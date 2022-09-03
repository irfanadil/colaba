package com.rnsoft.colabademo

import android.view.View
import androidx.fragment.app.FragmentManager


open class DocsTypesBaseFragment:BaseFragment() {
    companion object {
        @JvmStatic
        protected var combineDocList: ArrayList<Doc> = arrayListOf()
    }
    protected fun getSampleDocsTemplate():ArrayList<DocTypeModelClass>{

            val infoTitle ="My Standard Checklist"
            //val infoArrayList:ArrayList<String> = arrayListOf("Earnest Money Deposit", "Financial Statements" , "Profit and Loss Statement", "Form 1099 (Miscellaneous Income)", "Earnest Money Deposit", "Financial Statements")
            val infoArrayList:ArrayList<Doc> = arrayListOf()


            val docTypeModelClass = DocTypeModelClass( headerTitle = "My Templates", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Income Template", "Checking"),
                DocTypeContentCell("My Standard Checklist", "Saving"),
                DocTypeContentCell("Assets template", "Saving")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val docTypeModelClass2 = DocTypeModelClass( headerTitle = "System Templates", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("FHA Full Doc Refinance - W2", "Checking"),
                DocTypeContentCell("VA Cash Out - W-2", "Saving"),
                DocTypeContentCell("FHA Full Doc Refinance", "Saving"),
                DocTypeContentCell("Conventional Refinance - SE", "Saving"),
                DocTypeContentCell("VA Purchase - W-2", "Saving"),
                DocTypeContentCell("Additional Questions", "Saving"),
                DocTypeContentCell("Auto Loan", "Saving"),
                DocTypeContentCell("Construction Loan-Phase 1", "Saving")

            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val modelArrayList:ArrayList<DocTypeModelClass> = arrayListOf()
        modelArrayList.add(docTypeModelClass)
        modelArrayList.add(docTypeModelClass2)
        return modelArrayList

    }

    private val openBottomFragment = View.OnClickListener {
        //StandardChecklistDialogFragment.newInstance().show(childFragmentManager, StandardChecklistDialogFragment::class.java.canonicalName)
    }

    class MyLovelyOnClickListener(private val dialogTitle: String, private val dialogValues:ArrayList<Doc>, private val childFragmentManager:FragmentManager) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            //read your lovely variable
            StandardChecklistDialogFragment.newInstance(dialogTitle, dialogValues).show(childFragmentManager, StandardChecklistDialogFragment::class.java.canonicalName)
        }
    }

    protected fun getSampleDocsFiles():ArrayList<DocTypeModelClass>{

        val infoTitle ="My Standard Checklist"
        //val infoArrayList:ArrayList<String> = arrayListOf("Earnest Money Deposit", "Financial Statements" , "Profit and Loss Statement", "Form 1099 (Miscellaneous Income)", "Earnest Money Deposit", "Financial Statements")
        val infoArrayList:ArrayList<Doc> = arrayListOf()
        val docTypeModelClass = DocTypeModelClass( headerTitle = "Assets", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Credit Report", ""),
                DocTypeContentCell("Earnest Money Deposit", ""),
                DocTypeContentCell("Financial Statement", ""),
                DocTypeContentCell("Form 1099", ""),
                DocTypeContentCell("Government-issued ID", ""),
                DocTypeContentCell("Letter of Explanation - General", ""),
                DocTypeContentCell("Mortgage Statement", ""),
                DocTypeContentCell("Form 1099", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val docTypeModelClass2 = DocTypeModelClass( headerTitle = "Income", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Tax Returns with Schedules (Business - Two Years)", ""),
                DocTypeContentCell("Tax Returns with Schedules (Personal - Two Years)", ""),
                DocTypeContentCell("Paystubs - Most Recent", ""),
                DocTypeContentCell("W-2s - Last Two years", ""),
                DocTypeContentCell("Rental Agreement - Real Estate Owned", ""),
                DocTypeContentCell("Income - Miscellaneous", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val docTypeModelClass3 = DocTypeModelClass( headerTitle = "Liabilities", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("HOA or Condo Association Fee Statements", ""),
                DocTypeContentCell("Liabilities - Miscellaneous", ""),
                DocTypeContentCell("Rental Agreement", ""),
                DocTypeContentCell("Bankruptcy Papers", ""),
                DocTypeContentCell("Property Tax Statement", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val docTypeModelClass4 = DocTypeModelClass( headerTitle = "Personal", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Government Issued Identification", ""),
                DocTypeContentCell("Permanent Resident Card", ""),
                DocTypeContentCell("Work Visa - Work Permit", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))


        val docTypeModelClass5 = DocTypeModelClass( headerTitle = "Property", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Purchase Contract", ""),
                DocTypeContentCell("Condo HO6 Interior Insurance", ""),
                DocTypeContentCell("Flood Insurance Policy", ""),
                DocTypeContentCell("Survey Affidavit", ""),
                DocTypeContentCell("Property Survey", ""),
                DocTypeContentCell("Homeowner's Association Certificate", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))



        val docTypeModelClass6= DocTypeModelClass( headerTitle = "Disclosure", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Tax Returns with Schedules (Business - Two Years)", ""),
                DocTypeContentCell("Tax Returns with Schedules (Personal - Two Years)", ""),
                DocTypeContentCell("Paystubs - Most Recent", ""),
                DocTypeContentCell("W-2s - Last Two years", ""),
                DocTypeContentCell("Rental Agreement - Real Estate Owned", ""),
                DocTypeContentCell("Income - Miscellaneous", "")
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))



        val docTypeModelClass7 = DocTypeModelClass( headerTitle = "Other", totalSelected = "0" , footerTitle = "",
            contentCell = arrayListOf(
                DocTypeContentCell("Trust - Family Trust", ""),
                DocTypeContentCell("Divorce Decree", ""),
                DocTypeContentCell("Mortgage Statement", ""),
                DocTypeContentCell("Homebuyer Education", ""),
                DocTypeContentCell("Credit Explanation", ""),
                DocTypeContentCell("Letter of Explanation", ""),
                DocTypeContentCell("Power of Attorney (POA)", ""),
                DocTypeContentCell("Purpose of Cash Out", ""),
                DocTypeContentCell("Page 5 of 5 of bank statements", ""),
            ), MyLovelyOnClickListener(infoTitle, infoArrayList , childFragmentManager ))

        val modelArrayList:ArrayList<DocTypeModelClass> = arrayListOf()
        modelArrayList.add(docTypeModelClass)
        modelArrayList.add(docTypeModelClass2)
        modelArrayList.add(docTypeModelClass3)
        modelArrayList.add(docTypeModelClass4)
        modelArrayList.add(docTypeModelClass5)
        modelArrayList.add(docTypeModelClass6)
        modelArrayList.add(docTypeModelClass7)
        return modelArrayList

    }


}