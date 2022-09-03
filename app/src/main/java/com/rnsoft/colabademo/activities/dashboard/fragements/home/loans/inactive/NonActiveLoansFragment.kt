package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rnsoft.colabademo.databinding.NonActiveFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class NonActiveLoansFragment : LoanBaseFragment() , AdapterClickListener , LoanFilterInterface {
    private var _binding: NonActiveFragmentBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences


    private var rowLoading: ProgressBar? = null
    private lateinit var nonActiveRecycler: RecyclerView
    private var nonActiveLoansList: ArrayList<LoanItem> = ArrayList()
    private lateinit var nonActiveAdapter: LoansAdapter
    private var shimmerContainer: ShimmerFrameLayout?=null
    //private lateinit var loading: ProgressBar
    private val linearLayoutManager = LinearLayoutManager(activity , LinearLayoutManager.VERTICAL, false)
    ////////////////////////////////////////////////////////////////////////////
    //private var stringDateTime: String = ""

    private val pageSize: Int = 20
    private val loanFilter: Int = 2

    private var pageNumber: Int = 1
    //private var orderBy: Int = 0
    //private var assignedToMe: Boolean = false

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = NonActiveFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        shimmerContainer?.startShimmer()

        shimmerContainer = view.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout
        rowLoading = view.findViewById(R.id.non_active_row_loader)
        nonActiveRecycler = view.findViewById(R.id.inactive_loan_recycler_view)
        nonActiveAdapter = LoansAdapter(nonActiveLoansList, this@NonActiveLoansFragment)

        nonActiveRecycler.apply {
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(true)
            this.adapter =nonActiveAdapter
        }

        loanViewModel.nonActiveLoansArrayList.observe(viewLifecycleOwner, {
            rowLoading?.visibility = View.INVISIBLE
            if(it.size>0) {
                shimmerContainer?.stopShimmer()
                shimmerContainer?.isVisible = false
                if(oldListDisplaying){
                    oldListDisplaying = false
                    nonActiveLoansList.clear()
                }
                nonActiveLoansList.addAll(it)
                nonActiveAdapter.notifyDataSetChanged()
            }
            else{
                Log.e("no-record", " found....")
                shimmerContainer?.stopShimmer()
                shimmerContainer?.isVisible = false
                if(pageNumber == 1) {
                    nonActiveLoansList.clear()
                    nonActiveRecycler.addOnScrollListener(scrollListener)
                    nonActiveAdapter.notifyDataSetChanged()
                }
            }

        })

        nonActiveRecycler.addOnScrollListener(scrollListener)

        return view
    }

    private val scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to the bottom of the list
            Log.e("calling--", "scroller--")
            rowLoading?.visibility = View.VISIBLE
            pageNumber++
            loadNonActiveApplications()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onResume() {
        super.onResume()
        tabSwitched()
    }

    private fun tabSwitched(){
        Log.e("onResume - NON ACTIVE ", " "+oldListDisplaying)
        rowLoading?.visibility = View.INVISIBLE
        nonActiveLoansList.clear()
        pageNumber = 1
        //loadDataFromCache()
        loadNonActiveApplications()
    }

    private var oldListDisplaying:Boolean = false

    private fun loadDataFromCache(){
        // load data from cache first...
        if(!NetworkSetting.isNetworkAvailable(requireContext())) {
            val token: TypeToken<ArrayList<LoanItem>> = object : TypeToken<ArrayList<LoanItem>>() {}
            if (sharedPreferences.contains(AppConstant.oldNonActiveLoans)) {
                sharedPreferences.getString(AppConstant.oldNonActiveLoans, "")?.let { oldNonActiveLoans ->
                    val oldJsonList: ArrayList<LoanItem> = Gson().fromJson(oldNonActiveLoans, token.type)
                    shimmerContainer?.stopShimmer()
                    shimmerContainer?.isVisible = false
                        if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                        if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                        if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                        oldListDisplaying = true
                        nonActiveLoansList.clear()
                        nonActiveLoansList.addAll(oldJsonList)
                        nonActiveAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    private fun loadNonActiveApplications(){
        if(NetworkSetting.isNetworkAvailable(requireContext())) {
            Log.e("NON-ACTIVE-LOAN--", "globalAssignToMe = $globalAssignToMe")
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (AppSetting.nonActiveloanApiDateTime.isEmpty())
                    AppSetting.nonActiveloanApiDateTime =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(Date())
                loanViewModel.getNonActiveLoans(
                    token = authToken,
                    dateTime = AppSetting.nonActiveloanApiDateTime, pageNumber = pageNumber,
                    pageSize = pageSize, loanFilter = loanFilter,
                    orderBy = globalOrderBy, assignedToMe = globalAssignToMe
                )
            }
        }
        else {
            rowLoading?.visibility = View.INVISIBLE
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getSingleItemIndex(position: Int) {
        val borrowerBottomSheet = SheetBottomBorrowerCardFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.borrowerParcelObject, nonActiveLoansList[position])
        borrowerBottomSheet.arguments = bundle
        borrowerBottomSheet.show(childFragmentManager, SheetBottomBorrowerCardFragment::class.java.canonicalName)
    }

    override fun navigateTo(position: Int) {
        if(nonActiveLoansList.size>=position) {
            val borrowerDetailIntent = Intent(requireActivity(), DetailActivity::class.java)
            val test = nonActiveLoansList[position]
            Log.e("Before", test.loanApplicationId.toString())
            //borrowerDetailIntent.putExtra(AppConstant.borrowerParcelObject, allLoansArrayList[position])
            borrowerDetailIntent.putExtra(AppConstant.loanApplicationId, test.loanApplicationId)
            borrowerDetailIntent.putExtra(AppConstant.loanPurpose, test.loanPurpose)
            borrowerDetailIntent.putExtra(AppConstant.firstName, test.firstName)
            borrowerDetailIntent.putExtra(AppConstant.lastName, test.lastName)
            borrowerDetailIntent.putExtra(AppConstant.bPhoneNumber, test.cellNumber)
            borrowerDetailIntent.putExtra(AppConstant.bEmail, test.email)
            borrowerDetailIntent.putExtra(AppConstant.milestone, test.milestone)
            startActivity(borrowerDetailIntent)
        }
    }

   override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        //nonActiveLoansList.clear()
        //nonActiveRecycler.addOnScrollListener(scrollListener)
        //nonActiveAdapter.notifyDataSetChanged()
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorReceived(event: WebServiceErrorEvent) {
        rowLoading?.visibility = View.INVISIBLE
        shimmerContainer?.stopShimmer()
        shimmerContainer?.isVisible = false
        if(event.isInternetError)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG )
        else
        if(event.errorResult!=null)
            SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onTabSwitched(event: OnTabSwitchedEvent) {
        if(event.loanFilter == loanFilter) {
            Timber.e(" Running from Non active loan fragment...")
            tabSwitched()
        }
    }

    override fun setOrderId(passedOrderBy: Int) {
        nonActiveLoansList.clear()
        nonActiveAdapter.notifyDataSetChanged()
        //orderBy = passedOrderBy
        globalOrderBy = passedOrderBy
        pageNumber = 1
        loadNonActiveApplications()
    }

    override fun setAssignToMe(passedAssignToMe: Boolean) {
        Log.e("setAssignToMe = ", passedAssignToMe.toString())
        nonActiveLoansList.clear()
        nonActiveAdapter.notifyDataSetChanged()
        globalAssignToMe = passedAssignToMe
        //assignedToMe = passedAssignToMe
        pageNumber = 1
        loadNonActiveApplications()
    }
}