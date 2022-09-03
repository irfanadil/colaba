package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rnsoft.colabademo.databinding.DetailListLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.detail_list_layout.*
import javax.inject.Inject


@AndroidEntryPoint
class DocumentListFragment : BaseFragment(), DocsViewClickListener {
    private var _binding: DetailListLayoutBinding? = null
    private val binding get() = _binding!!
    private val detailViewModel: DetailViewModel by activityViewModels()
    private lateinit var docsRecycler: RecyclerView
    private var docsArrayList: ArrayList<SubFiles> = ArrayList()
    private lateinit var documentListAdapter: DocumentListAdapter
    private var downloadId: String? = null
    private var downloadRequestId: String? = null
    private var downloadDocId: String? = null
    private var docName: String? = null
    private var docMessage: String? = null
    private lateinit var tvDocName: TextView
    private lateinit var tvDocMsg: TextView
    private lateinit var doc_msg_layout: ConstraintLayout
    private lateinit var layoutNoDocUploaded: ConstraintLayout
    private  var downloadLoader: ProgressBar? = null

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DetailListLayoutBinding.inflate(inflater, container, false)
        val view: View = binding.root

        docsRecycler = view.findViewById(R.id.docs_detail_list_recycle_view)
        tvDocName = view.findViewById(R.id.doc_type_name)
        tvDocMsg = view.findViewById(R.id.doc_msg)
        //tvPercentage = view.findViewById(R.id.tv_percentage)
        doc_msg_layout = view.findViewById(R.id.layout_doc_msg)
        layoutNoDocUploaded = view.findViewById(R.id.layout_no_doc_upload)
        downloadLoader = view.findViewById(R.id.doc_download_loader)

        val layoutManager = LinearLayoutManager(activity , LinearLayoutManager.VERTICAL, false)
        documentListAdapter =
            DocumentListAdapter(docsArrayList, this@DocumentListFragment)
        docsRecycler.apply {
            this.layoutManager = layoutManager
            this.setHasFixedSize(true)
            this.adapter = documentListAdapter
            documentListAdapter.notifyDataSetChanged()
        }

        lifecycleScope.launchWhenStarted {

            docName = arguments?.getString(AppConstant.docName)
            //Log.e("doc_name", doc_name.toString())
            docMessage = arguments?.getString(AppConstant.docMessage)
            docsArrayList = arguments?.getParcelableArrayList(AppConstant.docObject)!!
            //val filesNames = arguments?.getString(AppConstant.innerFilesName)
            downloadId = arguments?.getString(AppConstant.download_id).toString()
            downloadRequestId = arguments?.getString(AppConstant.download_requestId).toString()
            downloadDocId = arguments?.getString(AppConstant.download_docId).toString()

            // set name and message
            tvDocName.text = docName
            if (docMessage?.isNotEmpty() == true) {
                tvDocMsg.text = docMessage
                doc_msg_layout.visibility = View.VISIBLE
            } else {
                doc_msg_layout.visibility = View.GONE
            }


            observeDownloadProgress()

             // populate recyclerview
            if (docsArrayList.size > 0) {
                docsRecycler.visibility = View.VISIBLE
                layoutNoDocUploaded.visibility = View.GONE
                documentListAdapter =
                    DocumentListAdapter(docsArrayList, this@DocumentListFragment)
                docsRecycler.adapter = documentListAdapter
                documentListAdapter.notifyDataSetChanged()

            } else {
                docsRecycler.visibility = View.GONE
                layoutNoDocUploaded.visibility = View.VISIBLE
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

       // (activity as DetailActivity).showFabIcons()
        super.addListeners(binding.root)
        return view
    }


    private fun observeDownloadProgress(){
        detailViewModel.progressGlobal.observe(viewLifecycleOwner, {
                if (it != null && it.size > 0) {
                    val percentage = ((it[0]* 100) / it[1]).toInt()
                    //Log.e("Ui-percentage--", ""+percentage)
                    loader_percentage.text = "$percentage%"
                }
        })
    }

    override fun navigateTo(position: Int, docName:String) {
        //Log.e("param", " downloadId: " + download_id + " downRequeId: " + download_requestId + " downDocId: " + download_docId)
        sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
            val selectedFile = docsArrayList[position]

            if (downloadDocId != null && downloadRequestId != null && downloadId != null) {
                downloadLoader?.visibility = View.VISIBLE
                loader_percentage.text = "0%"
                loader_percentage.visibility = View.VISIBLE

                detailViewModel.downloadFile(
                    token = authToken,
                    id = downloadId!!,
                    requestId = downloadRequestId!!,
                    docId = downloadDocId!!,
                    fileId = selectedFile.id,
                    fileName = docName
                )
            }
            else
                SandbarUtils.showRegular(requireActivity(), "File can not be downloaded...")
        }
    }


    override fun getCardIndex(position: Int) {

    }



    /*
    We do not have to listen here as we are already listening in the previous screen....
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
       findNavController().navigate(R.id.pdf_view_fragment_id, bundle)
   }

   private fun goToImageViewFragment(imageFileName:String){
       val imageViewFragment = ImageViewFragment()
       val bundle = Bundle()
       bundle.putString(AppConstant.downloadedFileName, imageFileName)
       imageViewFragment.arguments = bundle
       findNavController().navigate(R.id.image_view_fragment_id, imageViewFragment.arguments)
   }
    */


}