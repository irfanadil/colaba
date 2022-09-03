package com.rnsoft.colabademo

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rnsoft.colabademo.databinding.ActiveLoanFragmentBinding
import dagger.hilt.android.AndroidEntryPoint
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

private const val pageSize: Int = 20
private const val loanFilter: Int = 1

@AndroidEntryPoint
class ActiveLoansFragment : LoanBaseFragment() , AdapterClickListener  ,  LoanFilterInterface {
    private var _binding: ActiveLoanFragmentBinding? = null
    private val binding get() = _binding!!


    private  var rowLoading: ProgressBar? = null
    private lateinit var activeRecycler: RecyclerView
    private var activeLoansList: ArrayList<LoanItem> = ArrayList()
    private lateinit var activeAdapter: LoansAdapter
    //private lateinit var loading: ProgressBar
    private var shimmerContainer: ShimmerFrameLayout?=null
    private val linearLayoutManager = LinearLayoutManager(activity , LinearLayoutManager.VERTICAL, false)
    ////////////////////////////////////////////////////////////////////////////


    private var pageNumber: Int = 1
    //private var orderBy: Int = 0
    //private var assignedToMe: Boolean = false

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = ActiveLoanFragmentBinding.inflate(inflater, container, false)
        val view = binding.root
        shimmerContainer = view.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout
        shimmerContainer?.startShimmer()
        rowLoading = view.findViewById(R.id.active_row_loader)
        activeRecycler = view.findViewById(R.id.active_recycler)

        activeAdapter = LoansAdapter(activeLoansList, this@ActiveLoansFragment)
        activeRecycler.apply {
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(true)
            activeAdapter = LoansAdapter(activeLoansList, this@ActiveLoansFragment)
            this.adapter = activeAdapter
        }

        loanViewModel.activeLoansArrayList.observe(viewLifecycleOwner, Observer {
            rowLoading?.visibility = View.INVISIBLE
            shimmerContainer?.stopShimmer()
            shimmerContainer?.isVisible = false
            if(it.size>0) {
                if(oldListDisplaying){
                    oldListDisplaying = false
                    activeLoansList.clear()
                }
                activeLoansList.addAll(it)
                activeAdapter.notifyDataSetChanged()
            }
            else {
                Timber.e(" found....")
                if (pageNumber == 1) {
                    activeLoansList.clear()
                    activeRecycler.addOnScrollListener(scrollListener)
                    activeAdapter.notifyDataSetChanged()
                }
            }

        })

        activeRecycler.addOnScrollListener(scrollListener)

        return view
    }

    private val scrollListener = object : EndlessRecyclerViewScrollListener(linearLayoutManager) {
        override fun onLoadMore(page: Int, totalItemsCount: Int, view: RecyclerView?) {
            // Triggered only when new data needs to be appended to the list
            // Add whatever code is needed to append new items to the bottom of the list
            Timber.e("scroller--")
            rowLoading?.visibility = View.VISIBLE
            pageNumber++
            loadActiveApplications()
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////
    override fun onResume() {
        super.onResume()
        tabSwitched()
    }

    private fun tabSwitched() {
        Timber.e(""+oldListDisplaying)
        rowLoading?.visibility = View.INVISIBLE
        activeLoansList.clear()
        pageNumber = 1
        //loadDataFromCache()
        loadActiveApplications()
    }

    private var oldListDisplaying:Boolean = false

    private fun loadDataFromCache(){
        // load data from cache first...
        if(!NetworkSetting.isNetworkAvailable(requireContext())) {
            val token: TypeToken<ArrayList<LoanItem>> = object : TypeToken<ArrayList<LoanItem>>() {}
            if (sharedPreferences.contains(AppConstant.oldActiveLoans)) {
                sharedPreferences.getString(AppConstant.oldActiveLoans, "")?.let { oldActiveLoans ->
                    val oldJsonList: ArrayList<LoanItem> = Gson().fromJson(oldActiveLoans, token.type)
                    if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                    if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                    if(oldJsonList.size>1) oldJsonList.removeAt(oldJsonList.size-1)
                    shimmerContainer?.stopShimmer()
                    shimmerContainer?.isVisible = false
                    oldListDisplaying = true
                    activeLoansList.clear()
                    activeLoansList.addAll(oldJsonList)
                    activeAdapter.notifyDataSetChanged()
                    //loanRecycleView?.removeOnScrollListener(scrollListener)
                }
            }
        }
    }

    private fun loadActiveApplications() {
        if(NetworkSetting.isNetworkAvailable(requireContext())) {
            Timber.e("globalAssignToMe = " + globalAssignToMe)
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                if (AppSetting.activeloanApiDateTime.isEmpty())
                    AppSetting.activeloanApiDateTime =
                        SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault()).format(
                            Date()
                        )
                loanViewModel.getActiveLoans(
                    token = authToken,
                    dateTime = AppSetting.activeloanApiDateTime, pageNumber = pageNumber,
                    pageSize = pageSize, loanFilter = loanFilter,
                    orderBy = globalOrderBy, assignedToMe = globalAssignToMe
                )
            }
        }
        else{
            rowLoading?.visibility = View.INVISIBLE
        }
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    override fun getSingleItemIndex(position: Int) {
        val borrowerBottomSheet = SheetBottomBorrowerCardFragment.newInstance()
        val bundle = Bundle()
        bundle.putParcelable(AppConstant.borrowerParcelObject, activeLoansList[position])
        borrowerBottomSheet.arguments = bundle
        borrowerBottomSheet.show(childFragmentManager, SheetBottomBorrowerCardFragment::class.java.canonicalName)
    }

    override fun navigateTo(position: Int) {
        if(activeLoansList.size>=position) {
            val borrowerDetailIntent = Intent(requireActivity(), DetailActivity::class.java)
            val test = activeLoansList[position]
            Timber.e(test.loanApplicationId.toString())
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
        //activeLoansList.clear()
        //activeRecycler.addOnScrollListener(scrollListener)
        //activeAdapter.notifyDataSetChanged()
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
            Timber.e(" Running from Active loan fragment...")
            tabSwitched()
        }
    }

    override fun setOrderId(passedOrderBy: Int) {
        activeLoansList.clear()
        activeAdapter.notifyDataSetChanged()
        //orderBy = passedOrderBy
        globalOrderBy = passedOrderBy
        pageNumber = 1
        loadActiveApplications()
    }

    override fun setAssignToMe(passedAssignToMe: Boolean) {
        Timber.e(passedAssignToMe.toString())
        activeLoansList.clear()
        activeAdapter.notifyDataSetChanged()
        globalAssignToMe = passedAssignToMe
        //assignedToMe = passedAssignToMe
        pageNumber = 1
        loadActiveApplications()
    }


}