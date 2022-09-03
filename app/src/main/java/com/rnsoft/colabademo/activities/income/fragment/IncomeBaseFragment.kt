package com.rnsoft.colabademo

open class IncomeBaseFragment : BaseFragment() {

    protected fun getSampleIncome():ArrayList<IncomeModelClass>{

        val incomeModelCell = IncomeModelClass( headerTitle = "Employment", headerAmount = "$0" , footerTitle = AppConstant.footerAddEmployment,
            incomeContentCell = arrayListOf(), navigateToPreviousEmployment)

        val incomeModelCell2 = IncomeModelClass( headerTitle = "Self Employment / Independent Contractor", headerAmount = "$0" , footerTitle = "Add Self Employment",
            incomeContentCell = arrayListOf(), navigateToSelfEmployment)

        val incomeModelCell3 = IncomeModelClass( headerTitle = "Business" ,headerAmount = "$0" , footerTitle = "Add Business",
            incomeContentCell = arrayListOf(), navigateToBusinessIncome)

        val incomeModelCell4 = IncomeModelClass( headerTitle = "Military Pay", headerAmount = "$0" , footerTitle = "Add Military Service",
            incomeContentCell = arrayListOf(), navigateToMilitaryPay)

        val incomeModelCell5 = IncomeModelClass( headerTitle = "Retirement", headerAmount = "$0" , footerTitle = "Add Retirement",
            incomeContentCell = arrayListOf(), navigateToRetirementIncome)

        val incomeModelCell6 = IncomeModelClass( headerTitle = "Other", headerAmount = "$0" , footerTitle = "Add Other Income",
            incomeContentCell = arrayListOf(), navigateToOtherIncome)

        val modelArrayList:ArrayList<IncomeModelClass> = arrayListOf()
        modelArrayList.add(incomeModelCell)
        modelArrayList.add(incomeModelCell2)
        modelArrayList.add(incomeModelCell3)
        modelArrayList.add(incomeModelCell4)
        modelArrayList.add(incomeModelCell5)
        modelArrayList.add(incomeModelCell6)

        return modelArrayList
    }


    private val navigateToPreviousEmployment = R.id.action_prev_employment
    private val navigateToSelfEmployment = R.id.action_self_employment
    private val navigateToBusinessIncome = R.id.action_income_business
    private val navigateToMilitaryPay = R.id.action_income_military_pay
    private val navigateToRetirementIncome = R.id.action_income_retirement
    private val navigateToOtherIncome = R.id.action_income_other

}