package com.rnsoft.colabademo

import com.rnsoft.AssetTypesByCategory
import com.rnsoft.colabademo.activities.assets.fragment.model.*
import com.rnsoft.colabademo.activities.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServerApi{

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // LOGIN SCREEEN API...........
    /*
    @Headers(
        "Content-Type: application/json;charset=utf-8",
        "Accept: application/json;charset=utf-8",
        "Cache-Control: max-age=640000"
    )
     */
//
//    @GET("api/mcu/mobile/DocumentManagement/Template/GetTemplates")
//    suspend fun getEmailTemplates(
//        @Header("Authorization") Authorization:String) : TemplatesModel
//
//
//

    // subject property
    @GET("api/mcu/mobile/loanapplication/Loan/GetAllPropertyTypeDropDown")
    suspend fun getPropertyTypes(@Header("Authorization" )Authorization:String) : ArrayList<DropDownResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetPropertyUsageDropDown")
    suspend fun getOccupancyType(@Header("Authorization" )Authorization:String) : ArrayList<DropDownResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetAllPropertyStatusDropDown")
    suspend fun getPropertyStatus(@Header("Authorization" )Authorization:String) : ArrayList<DropDownResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetCountries")
    suspend fun getCountries(@Header("Authorization" )Authorization:String) : ArrayList<CountriesModel>

    @GET("api/mcu/mobile/loanapplication/Loan/GetStates")
    suspend fun getStates(@Header("Authorization" )Authorization:String) : ArrayList<StatesModel>

    @GET("api/mcu/mobile/loanapplication/Loan/GetCounties")
    suspend fun getCounties(@Header("Authorization" )Authorization:String) : ArrayList<CountiesModel>

    @GET("api/mcu/mobile/loanapplication/Borrower/GetBorrowerDetails")
    suspend fun getPrimaryBorrowerDetail(
        @Header("Authorization")  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("borrowerId")  borrowerId:Int
    ) : PrimaryBorrowerResponse

    @POST("api/mcu/mobile/loanapplication/Borrower/AddOrUpdateBorrowerDetails")
    suspend fun addUpdateBorrowerInfo(@Header("Authorization")  Authorization:String, @Body data: PrimaryBorrowerData): AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/Borrower/DeleteBorrowerPreviousAddress")
    suspend fun deletePreviousAddress(
        @Header("Authorization") Authorization: String,
        @Query("loanApplicationId") loanApplicationId:Int,
        @Query("id") addressId:Int) : AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/Loan/GetHousingStatus")
    suspend fun getHousingStatus(
        @Header("Authorization") Authorization:String) : ArrayList<OptionsResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetRelationshipTypes")
    suspend fun getRelationshipTypes(
        @Header("Authorization") Authorization:String) : ArrayList<DropDownResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetCitizenship")
    suspend fun getCitizenship(
        @Header("Authorization") Authorization:String) : ArrayList<OptionsResponse>

    @GET("https://devmobilegateway.rainsoftfn.com/api/mcu/mobile/loanapplication/Loan/GetVisaStatus")
    suspend fun getVisaStatus(
        @Header("Authorization") Authorization : String,
        @Query("residencyTypeId") residencyType : Int
        ) : ArrayList<OptionsResponse>

    @GET("api/mcu/mobile/loanapplication/Loan/GetMilitaryAffiliation")
    suspend fun getMilitaryAffiliation(
        @Header("Authorization") Authorization:String) : ArrayList<OptionsResponse>

    @GET("api/mcu/mobile/loanapplication/SubjectProperty/GetSubjectPropertyDetails")
    suspend fun getSubjectPropertyDetails(
        @Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int) : SubjectPropertyDetails

    //@Headers("Content-Type: application/json")
    @POST("api/mcu/mobile/loanapplication/SubjectProperty/AddOrUpdateSubjectPropertyDetail")
    suspend fun addOrUpdateSubjectPropertyDetail(
        @Header("Authorization")  Authorization:String,
        @Body data: SubPropertyData
    ) : AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/SubjectProperty/AddOrUpdateRefinanceSubjectPropertyDetail")
    suspend fun addOrUpdateRefinanceDetail(@Header("Authorization")  Authorization:String, @Body data: SubPropertyRefinanceData) :AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/SubjectProperty/AddOrUpdateCoBorrowerOccupancyStatus")
    suspend fun addCoBorrowerOccupancy(@Header("Authorization")  Authorization:String, @Body data: AddCoBorrowerOccupancy) : AddUpdateDataResponse


    @GET("api/mcu/mobile/loanapplication/SubjectProperty/GetRefinanceSubjectPropertyDetail")
    suspend fun getSubjectPropertyRefinance(
        @Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int) : SubjectPropertyRefinanceDetails

    @GET("api/mcu/mobile/loanapplication/SubjectProperty/GetCoBorrowersOccupancyStatus")
    suspend fun getCoBorrowerOccupancyStatus(
        @Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int) : CoBorrowerOccupancyStatus

    @GET("api/mcu/mobile/loanapplication/Loan/GetLoanInfoDetails")
    suspend fun getLoanInfoDetails(
        @Query("loanApplicationId")  loanApplicationId:Int) : LoanInfoDetailsModel

    @GET("api/mcu/mobile/loanapplication/Loan/GetAllLoanGoals")
    suspend fun getLoanGoals(
        @Query("loanpurposeid") loanPurpuseId:Int) : ArrayList<LoanGoalModel>


    @POST("api/mcu/mobile/loanapplication/Loan/AddOrUpdateLoanInformation")
    suspend fun addUpdateLoanInfo(
        //@Header("Authorization")  Authorization:String,
        @Body data: AddLoanInfoModel
    ) : AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/Loan/AddOrUpdateLoanInformation")
    suspend fun addUpdateLoanRefinance(
        //@Header("Authorization")  Authorization:String,
        @Body data: UpdateLoanRefinanceModel
    ) : AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/RealEstate/GetRealEstateDetails")
    suspend fun getRealEstateDetails(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerPropertyId") borrowerPropertyId:Int)
    : RealEstateResponse

    @POST("api/mcu/mobile/loanapplication/RealEstate/AddOrUpdateRealEstateDetails")
    suspend fun addRealEstateDetails(@Header("Authorization")  Authorization:String, @Body data: AddRealEstateResponse) :AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/RealEstate/GetFirstMortgageDetails")
    suspend fun getFirstMortgageDetails(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerPropertyId") borrowerPropertyId:Int) : RealEstateFirstMortgageModel

    @GET("api/mcu/mobile/loanapplication/RealEstate/GetSecondMortgageDetails")
    suspend fun getSecMortgageDetails(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerPropertyId") borrowerPropertyId:Int) : RealEstateSecondMortgageModel

    @DELETE("api/mcu/mobile/loanapplication/RealEstate/DeleteRealEstateDetails")
    suspend fun deleteRealEstate(
        @Header("Authorization") Authorization: String,
        @Query("BorrowerPropertyId")borrowerId:Int) : AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetIncomeDetails")
    suspend fun getBorrowerIncomeDetail(
        @Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("borrowerId")  borrowerId:Int): IncomeDetailsResponse

    // assets apis

    @GET("api/mcu/mobile/loanapplication/Assets/GetBankAccountDetails")
    suspend fun getBankAccountDetails(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): BankAccountResponse

    @DELETE("/api/mcu/mobile/loanapplication/Assets/DeleteIncome")
    suspend fun deleteIncome(
        @Header("Authorization") Authorization: String,
        @Query("IncomeInfoId")incomeInfoId: Int,
        @Query("borrowerId")borrowerId:Int,
        @Query("loanApplicationId") loanApplicationId:Int ): GenericAddUpdateAssetResponse



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Assets Add Update Api....

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateBankAccount")
    suspend fun addUpdateBankDetails(
        //@Header("Authorization") Authorization:String,
        @Body addUpdateParams:BankAddUpdateParams): GenericAddUpdateAssetResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateOtherAssetsInfo")
    suspend fun addUpdateOtherAsset(
        //@Header("Authorization") Authorization:String,
        @Body ootherAssetAddUpdateParams: OtherAssetAddUpdateParams): GenericAddUpdateAssetResponse


    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateFinancialAsset")
    suspend fun addUpdateStockBonds(
        //@Header("Authorization") Authorization:String,
        @Body stocksBondsAddUpdateParams:StocksBondsAddUpdateParams): GenericAddUpdateAssetResponse


    @DELETE("/api/mcu/mobile/loanapplication/Assets/DeleteAsset")
    suspend fun deleteAsset(//@Header("Authorization" )  Authorization:String,
                            @Query("AssetId" )  AssetId:Int,
                            @Query("borrowerId")  borrowerId:Int,
                            @Query("loanApplicationId")  loanApplicationId:Int ):
            GenericAddUpdateAssetResponse







    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateRetirementAccount")
    suspend fun addUpdateRetirement(
        //@Header("Authorization") Authorization:String,
        @Body retirementAddUpdateParams: RetirementAddUpdateParams): GenericAddUpdateAssetResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateGiftAsset")
    suspend fun addUpdateGift(
        //@Header("Authorization") Authorization:String,
        @Body giftAddUpdateParams: GiftAddUpdateParams): GenericAddUpdateAssetResponse


    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateProceedsfromloan")
    suspend fun addUpdateProceedFromLoan(//@Header("Authorization") Authorization:String,
                                         @Body addUpdateProceedLoanParams: AddUpdateProceedLoanParams) : GenericAddUpdateAssetResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateProceedsfromloanOther")
    suspend fun addUpdateProceedFromLoanOther(//@Header("Authorization") Authorization:String,
                                              @Body addUpdateProceedFromLoanOtherParams: AddUpdateProceedFromLoanOtherParams) : GenericAddUpdateAssetResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateAssestsRealState")
    suspend fun addUpdateAssetsRealState(
        //@Header("Authorization") Authorization:String,
        @Body addUpdateRealStateParams: AddUpdateRealStateParams) : GenericAddUpdateAssetResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateAssestsNonRealState")
    suspend fun addUpdateAssetsNonRealState(
        //@Header("Authorization") Authorization:String,
        @Body addUpdateRealStateParams: AddUpdateRealStateParams) : GenericAddUpdateAssetResponse

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @GET("api/mcu/mobile/loanapplication/Assets/GetBankAccountType")
    suspend fun getBankAccountType(
        //@Header("Authorization") Authorization:String
    ) : ArrayList<DropDownResponse>


    @GET("api/mcu/mobile/loanapplication/Assets/GetAssetTypesByCategory")
    suspend fun fetchAssetTypesByCategoryItemList(
        //@Header("Authorization") Authorization:String,
        @Query("categoryId") categoryId:Int,
        @Query("loanPurposeId") loanPurposeId:Int) : ArrayList<GetAssetTypesByCategoryItem>




    @GET("api/mcu/mobile/loanapplication/Assets/GetProceedsfromloanDetails")
    suspend fun getProceedsFromLoan(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanApplicationId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int
    ) : ProceedFromLoanModel

    @GET("api/mcu/mobile/loanapplication/Assets/GetFromLoanNonRealStateDetails")
    suspend fun getFromLoanNonRealStateDetails(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanApplicationId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int
    ) : ProceedFromLoanModel

    @GET("api/mcu/mobile/loanapplication/Assets/GetFromLoanRealStateDetails")
    suspend fun getFromLoanRealStateDetails(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanApplicationId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int
    ) : ProceedFromLoanModel


    // post add or update bank account


    @GET("api/mcu/mobile/loanapplication/Assets/GetRetirementAccountDetails")
    suspend fun getRetirementAccountDetails(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): RetirementAccountResponse

    // add or update retirement account details

    @GET("api/mcu/mobile/loanapplication/Assets/GetFinancialAssetDetails")
    suspend fun getFinancialAssetDetails(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): FinancialAssetResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetAllFinancialAsset")
    suspend fun getFinancialAsset(
        //@Header("Authorization") Authorization:String
    ) : ArrayList<DropDownResponse>

    // add or update financial asset


    @GET("api/mcu/mobile/loanapplication/Assets/GetFromLoanNonRealStateDetails")
    suspend fun getFromLoanNonRealEstateDetail(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): AssetsRealEstateResponse

    //AddOrUpdateAssestsNonRealState

    @GET("api/mcu/mobile/loanapplication/Assets/GetFromLoanRealStateDetails")
    suspend fun getFromLoanRealEstateDetail(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): AssetsRealEstateResponse

    //AddOrUpdateAssestsRealState

    @GET("api/mcu/mobile/loanapplication/Assets/GetAssetTypesByCategory")
    suspend fun getAssetTransactionType(
        //@Header("Authorization") Authorization : String,
        @Query("categoryId") loanPurpuseId : Int,
        @Query("loanPurposeId") borrowerId : Int ) : ArrayList<AssetTypesByCategory>

    // AddOrUpdateAssestsNonRealState

    @GET("api/mcu/mobile/loanapplication/Assets/GetProceedsfromloanDetails")
    suspend fun getProceedFromLoan(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("AssetTypeId") AssetTypeId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): ProceedFromLoanResponse

 //AddOrUpdateProceedsfromloan
    //AddOrUpdateProceedsfromloanOther

    @GET("api/mcu/mobile/loanapplication/Assets/GetGiftAssetDetails")
    suspend fun getGiftAssetDetail(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): GiftAssetResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetAllGiftSources")
    suspend fun getAllGiftSources(
        //@Header("Authorization") Authorization : String
    ) : ArrayList<GiftSourcesResponse>

    //AddOrUpdateGiftAsset

    @GET("api/mcu/mobile/loanapplication/Assets/GetOtherAssetDetails")
    suspend fun getOtherAssetDetails(
        //@Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("borrowerAssetId") borrowerAssetId:Int): OtherAssetResponse


    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // income apis

    @GET("api/mcu/mobile/loanapplication/Assets/GetEmploymentDetail")
    suspend fun getEmploymentDetail(
        @Header("Authorization") Authorization:String,
        @Query("loanApplicationId") loanPurpuseId:Int,
        @Query("borrowerId") borrowerId:Int,
        @Query("incomeInfoId") incomeInfoId:Int):EmploymentDetailResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateCurrentEmploymentDetail")
    suspend fun addCurrentEmployment(@Header("Authorization")  Authorization:String, @Body currentEmploymentData: AddCurrentEmploymentModel) : AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdatePreviousEmploymentDetail")
    suspend fun addPreviousEmployment(@Header("Authorization")  Authorization:String, @Body prevEmploymentData: PreviousEmploymentData) : AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetSelfBusinessIncome")
    suspend fun getSelfEmploymentContractor(
        @Header("Authorization") Authorization:String,
        @Query("borrowerId") borrowerId:Int,
        @Query("incomeInfoId") incomeInfoId:Int): SelfEmploymentResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateSelfBusiness")
    suspend fun addOrUpdateSelfBusiness(
        @Header("Authorization")  Authorization:String,
        @Body selfBusinessData: SelfEmploymentData
    ) : AddUpdateDataResponse


    @GET("api/mcu/mobile/loanapplication/Assets/GetBusinessIncome")
    suspend fun getBusinessIncome(
        @Header("Authorization") Authorization:String,
        @Query("borrowerId") borrowerId:Int,
        @Query("incomeInfoId") incomeInfoId:Int): BusinessIncomeResponse


    @GET("api/mcu/mobile/loanapplication/Assets/GetAllBusinessTypes")
    suspend fun getAllBusinessTypes(
        @Header("Authorization") Authorization : String) : ArrayList<DropDownResponse>

    //AddOrUpdateBusiness
    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateBusiness")
    suspend fun addUpdateBusiness(
        @Header("Authorization")  Authorization:String,
        @Body data: BusinessData
    ) : AddUpdateDataResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetMilitaryIncome")
    suspend fun getMilitaryIncome(
        @Header("Authorization") Authorization:String,
        @Query("borrowerId") borrowerId:Int,
        @Query("incomeInfoId") incomeInfoId:Int): MilitaryIncomeResponse

    // AddOrUpdateMilitaryIncome

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateMilitaryIncome")
    suspend fun addUpdateMilitaryIncome(
        @Header("Authorization")  Authorization:String,
        @Body data: MilitaryIncomeData
    ) : AddUpdateDataResponse


    @GET("api/mcu/mobile/loanapplication/Assets/GetRetirementIncomeInfo")
    suspend fun getRetirementIncome(
        @Header("Authorization") Authorization:String,
        @Query("borrowerId") borrowerId:Int,
        @Query("incomeInfoId") incomeInfoId:Int): RetirementIncomeResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetRetirementIncomeTypes")
    suspend fun getRetirementIncomeTypes(
        @Header("Authorization") Authorization : String): ArrayList<DropDownResponse>

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateRetirementIncomeInfo")
    suspend fun addUpdateRetirement(@Header("Authorization")  Authorization:String, @Body data: RetirementIncomeData) : AddUpdateDataResponse

    @POST("api/mcu/mobile/loanapplication/Assets/AddOrUpdateOtherIncome")
    suspend fun addUpdateOtherIncome(@Header("Authorization") Authorization:String, @Body data: AddOtherIncomeInfo) : AddUpdateDataResponse



    @GET("api/mcu/mobile/loanapplication/Assets/GetOtherIncomeInfo")
    suspend fun getOtherIncomeInfo(
        @Header("Authorization") Authorization:String,
        @Query("incomeInfoId") incomeInfoId:Int): OtherIncomeResponse

    @GET("api/mcu/mobile/loanapplication/Assets/GetOtherIncomeTypes")
    suspend fun getOtherIncomeTypes(
        @Header("Authorization") Authorization : String) : ArrayList<DropDownResponse>


    @POST("api/mcu/mobile/identity/mcuaccount/signin")
    suspend fun login(@Body loginRequest: LoginRequest ,  @Header("dontAskTwoFaIdentifier")  dontAskTwoFaIdentifier:String=""): Response<LoginResponse>
    //suspend fun loginTwo(@Query("Email") Email:String, @Query("Password") Password:String): Response<LoginResponse>

    @GET("api/mcu/mobile/identity/mcuaccount/GetMcuTenantTwoFaValues")
    suspend fun getMcuTenantTwoFaValuesService( @Header("IntermediateToken")  IntermediateToken:String): TenantConfigurationResponse

    @POST("api/mcu/mobile/identity/mcuaccount/SendTwoFa")
    suspend fun sendTwoFa( @Header("IntermediateToken")  IntermediateToken:String): SendTwoFaResponse

    @GET("api/mcu/mobile/identity/mcuaccount/GetTwoFaSettings")
    suspend fun getOtpSetting(@Header("IntermediateToken")  IntermediateToken:String) :OtpSettingResponse

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // FORGOT SCREEN API....
    @POST("api/mcu/mobile/identity/mcuaccount/ForgotPasswordRequest")
    suspend fun forgotPasswordRequest(@Body forgotPasswordEmail: ForgotRequest): ForgotPasswordResponse


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // PHONE NUMBER SCREEN API................
    @POST("api/mcu/mobile/identity/mcuaccount/SkipTwoFa")
    suspend fun skipTwoFactorApi(@Header("IntermediateToken")  IntermediateToken:String): SkipTwoFactorResponse

    @POST("api/mcu/mobile/identity/mcuaccount/SendTwoFaToNumber") // Also used in OTP Screen and Phone Screen....
    suspend fun sendTwoFaToNumber(@Header("IntermediateToken")  IntermediateToken:String,
        @Query("PhoneNumber")  PhoneNumber: String
    ) : OtpSentResponse


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OTP SCREEN API....

    @POST("api/mcu/mobile/identity/mcuaccount/VerifyTwoFa")
    suspend fun verifyOtpCode(@Header("IntermediateToken")  IntermediateToken:String,
                              @Body otpRequest: OtpRequest
    ) :OtpVerificationResponse

    @POST("api/mcu/mobile/identity/mcuaccount/DontAskTwoFa")
    suspend fun notAskForOtpAgain(@Header("Authorization")  Authorization:String) :NotAskForOtpResponse


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // OTP DASHBOARD API....
    @POST("api/mcu/mobile/identity/mcuaccount/Logout")
    suspend fun logoutUser() :LogoutResponse



    @POST("api/mcu/mobile/identity/mcuaccount/RefreshAccessToken")
    fun refreshToken(@Body refreshTokenRequest: RefreshTokenRequest) : Call<LoginResponse>

    @GET("api/mcu/mobile/loanapplication/loan/GetListForPipeline")
    suspend fun loadAllLoansFromApi(
        //@Header("Authorization" )  Authorization:String,
        @Query("dateTime" , encoded = true)  dateTime:String,
        @Query("pageNumber")  pageNumber:Int,
        @Query("pageSize")  pageSize:Int ,
        @Query("loanFilter") loanFilter:Int ,
        @Query("orderBy") orderBy:Int ,
        @Query("assignedToMe")  assignedToMe:Boolean)
    :ArrayList<LoanItem>


    @GET("api/mcu/mobile/loanapplication/loan/search")
    suspend fun searchByText(
        //@Header("Authorization" )  Authorization:String,
        @Query("pageNumber")  pageNumber:Int,
        @Query("pageSize")  pageSize:Int ,
        @Query("searchTerm")  searchTerm:String)
            :ArrayList<SearchItem>


    @GET("api/mcu/mobile/loanapplication/loan/FindBorrowerContact")
    suspend fun searchByBorrowerContact(
        //@Header("Authorization" )  Authorization:String,
        @Query("keyword")  keyword:String)
            :SearchResultResponse


    @GET("api/mcu/mobile/loanapplication/loan/LookUpBorrowerContact")
    suspend fun lookUpBorrowerContact(
        //@Header("Authorization" )  Authorization:String,
        @Query("email")  email:String,
        @Query("phone")  phone:String?=null)
            :LookUpBorrowerContactResponse

    @POST("api/mcu/mobile/loanapplication/loan/CreateApplication")
    suspend fun createApplication(
        //@Header("Authorization" )  Authorization:String,
        @Body createNewApplicationParams: CreateNewApplicationParams
    ):CreateNewApplicationResponse



    @GET(" api/mcu/mobile/loanapplication/MCU/GetMcusByRoleId")
    suspend fun getMcusByRoleId(
        //@Header("Authorization" )  Authorization:String,
        @Query("filterLoanOfficer")  filterLoanOfficer:Boolean
    ):LoanOfficerApiResponse



    @GET("api/mcu/mobile/Notification/notification/GetPaged")
    suspend fun getNotificationListing(
        //@Header("Authorization" )  Authorization:String,
        @Query("pageSize")  pageSize:Int,
        @Query("lastId")  lastId:Int ,
        @Query("mediumId")  mediumId:Int
      ):ArrayList<NotificationItem>


    @GET("api/mcu/mobile/Notification/notification/getcount")
    suspend fun getNotificationCount(
        //@Header("Authorization" )  Authorization:String
    ):TotalNotificationCount


    @PUT("api/mcu/mobile/Notification/notification/Read")
    suspend fun readNotifications(
        //@Header("Authorization" )  Authorization:String,
        @Body putParams:PutParameters):Response<Any>

    @PUT("api/mcu/mobile/Notification/notification/Seen")
    suspend fun seenNotifications(
        //@Header("Authorization" )  Authorization:String,
         @Body putParams:PutParameters):Any

    @PUT("api/mcu/mobile/Notification/notification/Delete")
    suspend fun deleteNotifications(
        //@Header("Authorization" )  Authorization:String,
         @Body putParams:PutParameters):Response<Any>


    @GET("api/mcu/mobile/loanapplication/loan/getloaninfo")
    suspend fun getLoanInfo(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int):BorrowerOverviewModel


    @GET("api/mcu/mobile/Milestone/Milestone/GetMilestoneForLoanCenter")
    suspend fun getMilestoneForLoanCenter(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int):AppMileStoneResponse


    @GET("api/mcu/mobile/DocumentManagement/AdminDashboard/GetDocuments")
    suspend fun getBorrowerDocuments(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("pending")  pending:Boolean = true):ArrayList<BorrowerDocsModel>

    @GET("api/mcu/mobile/loanapplication/Loan/GetAllEthnicityList")
    suspend fun getEthnicityList(
        //@Header("Authorization" )  Authorization:String
    ) :ArrayList<EthnicityResponseModel>

    @GET("api/mcu/mobile/loanapplication/Loan/GetGenderList")
    suspend fun getGenderList(
        //@Header("Authorization" )  Authorization:String
    ) :ArrayList<GenderResponseModel>

    @GET("/api/mcu/mobile/loanapplication/Loan/GetAllRaceList")
    suspend fun getRaceList(
        //@Header("Authorization" )  Authorization:String
    ) :ArrayList<RaceResponseModel>

    @Streaming
    @GET("api/mcu/mobile/DocumentManagement/MCUDocument/view")
    suspend fun downloadFile(
        //@Header("Authorization" )  Authorization:String,
        @Query("id")  id:String,
        @Query("requestId")  requestId:String,
        @Query("docId")  docId:String,
        @Query("fileId")  fileId:String):Response<ResponseBody>


    @GET("api/mcu/mobile/loanapplication/Loan/GetLoanApplicationSummary")
    suspend fun getBorrowerApplicationTabData(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int):BorrowerApplicationTabModel


    @GET("api/mcu/mobile/loanapplication/Assets/GetAssetsDetails")
    suspend fun getBorrowerAssetsDetail(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("borrowerId")  borrowerId:Int
    ): MyAssetBorrowerDataClass


    @GET("api/mcu/mobile/loanapplication/GovtQuestions/GetGovernmentQuestions")
    suspend fun getGovernmentQuestions(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("ownTypeId")  ownTypeId:Int,
        @Query("borrowerId")  borrowerId:Int
    ):GovernmentQuestionsModelClass

    @POST("api/mcu/mobile/loanapplication/GovtQuestions/AddOrUpdateGovernmentQuestions")
    suspend fun addOrUpdateGovernmentQuestions(
        @Header("Authorization" )  Authorization:String,
        @Body questionParams: GovernmentParams
    ):GovernmentAddUpdateDataResponse



    @POST("api/mcu/mobile/loanapplication/GovtQuestions/AddOrUpdateDemogrhphicInfo")
    suspend fun addOrUpdateDemoGraphic(
        //@Header("Authorization" )  Authorization:String,
        @Body demoGraphicData: DemoGraphicData
    ):AddUpdateDemoGraphicResponse


    @GET("api/mcu/mobile/loanapplication/GovtQuestions/GetDemographicInformation")
    suspend fun getDemoGraphicInfo(
        //@Header("Authorization" )  Authorization:String,
        @Query("loanApplicationId")  loanApplicationId:Int,
        @Query("borrowerId")  borrowerId:Int
    ):DemoGraphicResponseModel

    @GET("api/mcu/mobile/DocumentManagement/EmailTemplate/GetEmailTemplates")
    suspend fun getEmailTemplates(
        @Header("Authorization") Authorization:String): ArrayList<EmailTemplatesResponse>

    @GET("api/mcu/mobile/DocumentManagement/EmailTemplate/GetRenderEmailTemplateById")
    suspend fun getEmailBody(
        @Header("Authorization") Authorization:String,
        @Query("loanapplicationid") loanApplicationId: Int,
        @Query("Id") templateId: String
    ): EmailTemplatesResponse

    @GET("api/mcu/mobile/DocumentManagement/Template/GetCategoryDocumentMcu")
    suspend fun getCategoryDocumentMcu(
        //@Header("Authorization" )  Authorization:String
    ):CategoryDocsResponse


    @GET("api/mcu/mobile/DocumentManagement/Template/GetTemplates")
    suspend fun getTemplates(
        //@Header("Authorization")  Authorization:String
    ): GetTemplatesResponse

    @POST("api/mcu/mobile/DocumentManagement/Request/Save")
    suspend fun sendDocRequest(@Header("Authorization")  Authorization:String, @Body data: SendDocRequestModel): Response<Unit>




}