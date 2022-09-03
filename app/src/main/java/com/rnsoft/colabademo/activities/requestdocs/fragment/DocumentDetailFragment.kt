package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.DocDetailLayoutBinding
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class DocumentDetailFragment : DocsTypesBaseFragment() {

    private lateinit var binding : DocDetailLayoutBinding

    private var docTypeObject:Doc? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DocDetailLayoutBinding.inflate(inflater, container, false)
        //val title = arguments?.getString(AppConstant.docTypeName).toString()
        docTypeObject = arguments?.getParcelable(AppConstant.docTypeObject)!!
        docTypeObject?.let {
            binding.toolbarTitle.text = it.docType
            binding.etMsg.setText(it.docMessage)
        }
        setupUI()
        return binding.root
     }

    private fun setupUI(){
       binding.btnTopDelete.setOnClickListener {
            docTypeObject?.let {
                DeleteDocumentDialogFragment.newInstance(it.docType).show(childFragmentManager, DeleteDocumentDialogFragment::class.java.canonicalName)
            }
        }
        binding.btnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnNext.setOnClickListener {
            docTypeObject?.let {
                it.docMessage =  binding.etMsg.text.toString()
                it.docType =  binding.toolbarTitle.text.toString()
            }
            findNavController().navigate(R.id.navigation_selected_doc_fragment)
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
    fun deleteCurrentDocument(delEvent: DeleteCurrentDocumentEvent) {
        docTypeObject?.let {
            combineDocList.remove(it)
        }
        findNavController().popBackStack()
    }


}