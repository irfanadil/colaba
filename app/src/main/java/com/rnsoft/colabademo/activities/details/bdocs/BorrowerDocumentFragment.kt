package com.rnsoft.colabademo


import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.gson.Gson
import com.rnsoft.colabademo.databinding.BorrowerDocLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_list_layout.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber
import javax.inject.Inject



@AndroidEntryPoint
class BorrowerDocumentFragment : BaseFragment(), AdapterClickListener, DownloadClickListener, View.OnClickListener {

    private var _binding: BorrowerDocLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var docsRecycler: RecyclerView
    private var docsArrayList: ArrayList<BorrowerDocsModel> = ArrayList()
    private var filterDocsList :ArrayList<BorrowerDocsModel> = ArrayList()
    private lateinit var borrowerDocumentAdapter: BorrowerDocumentAdapter
    private var shimmerContainer: ShimmerFrameLayout? = null
    lateinit var btnAll: AppCompatTextView
    lateinit var btnInDraft: AppCompatTextView
    lateinit var btnToDo: AppCompatTextView
    lateinit var btnFilterStarted: AppCompatTextView
    lateinit var btnFilterPending: AppCompatTextView
    lateinit var btnFilterCompleted: AppCompatTextView
    lateinit var btnFilterManullayAdded: AppCompatTextView
    var isStart: Boolean = true
    var filter : String = "All"
    lateinit var layout_noDocFound : ConstraintLayout
    lateinit var layout_docData : ConstraintLayout
    var state: Parcelable? = null

    private val detailViewModel: DetailViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = BorrowerDocLayoutBinding.inflate(inflater, container, false)
        val view: View = binding.root

        shimmerContainer = view.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout
        shimmerContainer?.startShimmer()
        docsRecycler = view.findViewById(R.id.docs_recycle_view)
        layout_noDocFound = view.findViewById(R.id.layout_no_documents)
        layout_docData = view.findViewById(R.id.layout_doc_data)
        val linearLayoutManager = LinearLayoutManager(activity)
        downloadLoader = view.findViewById(R.id.doc_download_loader)

        //(activity as DetailActivity).showFabIcons()

        borrowerDocumentAdapter =
            BorrowerDocumentAdapter(docsArrayList, this@BorrowerDocumentFragment , this@BorrowerDocumentFragment)

        docsRecycler.apply {
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(true)
            this.adapter = borrowerDocumentAdapter
            populateRecyclerview(docsArrayList)
        }

        //Log.e("Doc created on", "$docsArrayList")

        detailViewModel.borrowerDocsModelList.observe(viewLifecycleOwner, {
            //Timber.e("observing Data")
            if (isStart) {
                //Log.e("isStart",""+isStart)
                if (it != null && it.size > 0) {
                    docsArrayList = it
                    isStart = false
                    filter = AppConstant.filter_all
                    showHideLayout(true)
                    populateRecyclerview(docsArrayList)
                } else{
                    //Timber.e("list is null", "isStart" +isStart)
                    showHideLayout((false))
                }
            }
        })

        btnAll = view.findViewById(R.id.btn_all)
        btnAll.setOnClickListener(this)
        btnAll.isActivated = true

        btnInDraft = view.findViewById(R.id.btn_filter_indraft)
        btnInDraft.setOnClickListener(this)

        btnToDo = view.findViewById(R.id.btn_filter_todo)
        btnToDo.setOnClickListener(this)

        btnFilterStarted = view.findViewById(R.id.btn_filter_started)
        btnFilterStarted.setOnClickListener(this)

        btnFilterPending = view.findViewById(R.id.btn_filter_pending)
        btnFilterPending.setOnClickListener(this)

        btnFilterCompleted = view.findViewById(R.id.btn_filter_completed)
        btnFilterCompleted.setOnClickListener(this)

        btnFilterManullayAdded = view.findViewById(R.id.btn_filter_manullayAdded)
        btnFilterManullayAdded.setOnClickListener(this)

        (activity as DetailActivity).binding.requestDocFab.setOnClickListener{
            val intent = Intent(requireActivity(), RequestDocsActivity::class.java)
            val activity = (activity as? DetailActivity)
            val nameBuilder = StringBuilder()
            activity?.borrowerFirstName?.let {
                if(it != "null" && it.isNotEmpty())
                    nameBuilder.append(it)
            }
            activity?.borrowerLastName?.let {
                if(it != "null" && it.isNotEmpty())
                    nameBuilder.append(" ").append(it)
            }
            activity?.loanApplicationId?.let {
                intent.putExtra(AppConstant.loanApplicationId, it)
                intent.putExtra(AppConstant.fullName, nameBuilder.toString())
                startActivity(intent)
            }
        }

        observeDownloadProgress()
        super.addListeners(binding.root)
        return view

    }

    private fun populateRecyclerview(arrayList: ArrayList<BorrowerDocsModel>){
        if(arrayList.size >0) {
            //Timber.e("size:" + arrayList.size)
            borrowerDocumentAdapter =
                BorrowerDocumentAdapter(
                    arrayList,
                    this@BorrowerDocumentFragment,
                    this@BorrowerDocumentFragment
                )
            docsRecycler.adapter = borrowerDocumentAdapter
            borrowerDocumentAdapter.notifyDataSetChanged()
            showHideLayout(true)
        } else{
            showHideLayout(false)
        }
    }

    override fun onClick(v: View){
        when (v.id) {
            R.id.btn_all -> {
                filter = AppConstant.filter_all

                btnAll.isActivated = true
                btnInDraft.isActivated = false
                btnToDo.isActivated = false
                btnFilterStarted.isActivated = false
                btnFilterPending.isActivated = false
                btnFilterCompleted.isActivated = false
                btnFilterManullayAdded.isActivated = false

                populateRecyclerview(docsArrayList)
                layout_docData.visibility = View.VISIBLE
                docsRecycler.visibility = View.VISIBLE

            }
            R.id.btn_filter_indraft -> {
                filter = AppConstant.filter_inDraft

                btnAll.isActivated = false
                btnInDraft.isActivated = true
                btnToDo.isActivated = false
                btnFilterStarted.isActivated = false
                btnFilterPending.isActivated = false
                btnFilterCompleted.isActivated = false
                btnFilterManullayAdded.isActivated = false

                getDocItems(AppConstant.filter_inDraft)
            }
            R.id.btn_filter_todo -> {
                filter = AppConstant.filter_borrower_todo
                selectStatusFilter(
                    false, false, true,
                    false, false, false, false)
                getDocItems(AppConstant.filter_borrower_todo)
            }
            R.id.btn_filter_started -> {
                filter = AppConstant.filter_started

                selectStatusFilter(
                    false, false, false,
                    true, false, false, false
                )
                getDocItems(AppConstant.filter_started)
            }
            R.id.btn_filter_pending -> {
                filter = AppConstant.filter_pending_review
                selectStatusFilter(
                    false, false, false,

                    false, true, false, false
                )
                getDocItems(AppConstant.filter_pending_review)
            }
            R.id.btn_filter_completed -> {
                filter = AppConstant.filter_completed
                selectStatusFilter(
                    false, false, false,
                    false, false, true, false
                )
                getDocItems(AppConstant.filter_completed)
            }
            R.id.btn_filter_manullayAdded -> {
                filter = AppConstant.filter_manuallyAdded
                selectStatusFilter(
                    false, false, false,
                    false, false, false, true
                )
                getDocItems(AppConstant.filter_manuallyAdded)
            }
            else -> {
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////////\\
    private  var downloadLoader: ProgressBar? = null

    private fun observeDownloadProgress(){
        detailViewModel.progressGlobal.observe(viewLifecycleOwner, {
            if (it != null && it.size > 0) {
                var percentage = ((it[0]* 100) / it[1]).toInt()
                //Log.e("Ui-percentage--", ""+percentage)
                loader_percentage.text = "$percentage%"
            }
        })
    }

    override fun fileClicked(fileName: String , fileId:String, position: Int) {
        sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
            val selected = docsArrayList[position]

            if (selected.docId != null && selected.requestId != null &&  selected.id != null) {
                downloadLoader?.visibility = View.VISIBLE
                loader_percentage.text = "0%"
                loader_percentage.visibility = View.VISIBLE

                detailViewModel.downloadFile(
                    token = authToken,
                    id = selected.id,
                    requestId = selected.requestId,
                    docId = selected.docId,
                    fileId = fileId,
                    fileName = fileName
                )
            }
            else
                SandbarUtils.showRegular(requireActivity(), "File can not be downloaded...")
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DetailActivity).binding.requestDocFab.visibility = View.VISIBLE

        if(docsArrayList.size ==0){
            showHideLayout(false)
        }
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onErrorReceived(event: WebServiceErrorEvent) {
        downloadLoader?.visibility = View.GONE
        loader_percentage.visibility = View.GONE
        //tvPercentage.visibility = View.GONE

        if(event.isInternetError)
            SandbarUtils.showError(requireActivity(), AppConstant.INTERNET_ERR_MSG )
        else
            if(event.errorResult!=null)
                SandbarUtils.showError(requireActivity(), AppConstant.WEB_SERVICE_ERR_MSG )
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onFileDownloadCompleted(event: FileDownloadEvent) {
        if (activity is DetailActivity) {
            (activity as DetailActivity).checkIfUnreadFileOpened()
        }
        downloadLoader?.visibility = View.GONE
        loader_percentage.visibility = View.GONE
        //tvPercentage.visibility = View.GONE
        event.docFileName?.let {
            if (!it.isNullOrBlank() && it.isNotEmpty()) {
                if(it.contains(".pdf"))
                    goToPdfFragment(it)
                else
                    goToImageViewFragment(it)
            }
        }
    }

    private fun goToPdfFragment(pdfFileName:String){
        val pdfViewFragment = PdfViewFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.downloadedFileName, pdfFileName)
        pdfViewFragment.arguments = bundle
        findNavController().navigate(R.id.pdf_view_fragment_id, pdfViewFragment.arguments)
    }

    private fun goToImageViewFragment(imageFileName:String){
        val imageViewFragment = ImageViewFragment()
        val bundle = Bundle()
        bundle.putString(AppConstant.downloadedFileName, imageFileName)
        imageViewFragment.arguments = bundle
        findNavController().navigate(R.id.image_view_fragment_id, imageViewFragment.arguments)
    }

    override fun navigateTo(position: Int) {
        val selectedDocumentType = if(filter == AppConstant.filter_all) docsArrayList[position] else filterDocsList[position]
        val listFragment = DocumentListFragment()
        val bundle = Bundle()
        val fileNames = Gson().toJson(selectedDocumentType.subFiles)
        bundle.putString(AppConstant.docName, selectedDocumentType.docName)
        bundle.putString(AppConstant.docMessage, selectedDocumentType.message)
        bundle.putParcelableArrayList(AppConstant.docObject,selectedDocumentType.subFiles)
        bundle.putString(AppConstant.innerFilesName, fileNames)
        bundle.putString(AppConstant.download_id, selectedDocumentType.id)
        bundle.putString(AppConstant.download_requestId, selectedDocumentType.requestId)
        bundle.putString(AppConstant.download_docId, selectedDocumentType.docId)
        listFragment.arguments = bundle
        //Timber.e("  fileNames $fileNames")
        //Timber.e(" docName = "+selectedDocumentType.docName)
        //Timber.e(" message = "+selectedDocumentType.message+"  ")
        //Timber.e(" subFiles = "+selectedDocumentType.subFiles)
        //Timber.e(" id = "+selectedDocumentType.id)
        //Timber.e("  requestId = "+selectedDocumentType.requestId)
       // Timber.e("  docId = "+selectedDocumentType.docId)
        findNavController().navigate(R.id.docs_list_inner_fragment, bundle)
    }



    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private fun selectStatusFilter(
        statusAll: Boolean,
        statusInDraft: Boolean,
        statusToDo: Boolean,
        statusStarted: Boolean,
        statusPending: Boolean,
        statusCompleted: Boolean,
        statusManuallyAdded: Boolean
    ) {
        if (statusAll) {
            btnAll.isActivated = true
            btnInDraft.isActivated = false
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = false

        } else if (statusInDraft) {
            btnAll.isActivated = false
            btnInDraft.isActivated = true
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = false

        } else if (statusToDo) {
            btnAll.isActivated = false
            btnInDraft.isActivated = false
            btnToDo.isActivated = true
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = false

        } else if (statusStarted) {
            btnAll.isActivated = false
            btnInDraft.isActivated = false
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = true
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = false

        } else if (statusPending) {
            btnAll.isActivated = false
            btnInDraft.isActivated = false
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = true
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = false

        } else if (statusCompleted) {
            btnAll.isActivated = false
            btnInDraft.isActivated = false
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = true
            btnFilterManullayAdded.isActivated = false

        } else if (statusManuallyAdded) {
            btnAll.isActivated = false
            btnInDraft.isActivated = false
            btnToDo.isActivated = false
            btnFilterStarted.isActivated = false
            btnFilterPending.isActivated = false
            btnFilterCompleted.isActivated = false
            btnFilterManullayAdded.isActivated = true
        }

    }

    private fun showHideLayout(dataLayout: Boolean){
        //Timber.e("datalayout "+ dataLayout)
        if(dataLayout){
            layout_docData.visibility = View.VISIBLE
            layout_noDocFound.visibility = View.GONE
            //(activity as DetailActivity).binding.requestDocFab.visibility = View.VISIBLE
        }
        else {
            layout_docData.visibility = View.GONE
            layout_noDocFound.visibility = View.VISIBLE
            //(activity as DetailActivity).binding.requestDocFab.visibility = View.VISIBLE
        }
    }

    private fun getDocItems(docFilter: String) {
        filterDocsList = ArrayList()
        for (i in docsArrayList.indices) {
            if (docFilter.equals(docsArrayList.get(i).status, ignoreCase = true)) {

                val doc = BorrowerDocsModel(
                    docsArrayList.get(i).createdOn,
                    docsArrayList.get(i).docId,
                    docsArrayList.get(i).docName,
                    docsArrayList.get(i).subFiles,
                    docsArrayList.get(i).id,
                    docsArrayList.get(i).requestId,
                    docsArrayList.get(i).status,
                    docsArrayList.get(i).typeId,
                    docsArrayList.get(i).userName,
                    docsArrayList.get(i).message
                )
                filterDocsList.add(doc)
            }
        }
        //Log.e("Filterlist size", ""+filterDocsList.size)
        if(filterDocsList.size > 0) {
            layout_noDocFound.visibility = View.GONE
            //(activity as DetailActivity).binding.requestDocFab.visibility = View.VISIBLE
            docsRecycler.visibility=View.VISIBLE
            populateRecyclerview(filterDocsList)
        } else{
            docsRecycler.visibility=View.GONE
            layout_noDocFound.visibility = View.VISIBLE
            //(activity as DetailActivity).binding.requestDocFab.visibility = View.VISIBLE
        }
    }

    override fun getSingleItemIndex(position: Int) {
    }

}