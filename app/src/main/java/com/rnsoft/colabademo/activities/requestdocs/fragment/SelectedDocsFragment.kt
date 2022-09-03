package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.os.bundleOf
import androidx.core.view.get
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.SelectedDocsLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.select_doc_main_cell.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

import javax.inject.Inject
@AndroidEntryPoint
class SelectedDocsFragment:DocsTypesBaseFragment() {

    private var _binding: SelectedDocsLayoutBinding? = null
    private val binding get() = _binding!!



    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = SelectedDocsLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpLayout()
        super.addListeners(binding.root)
        return root
    }

    private fun setUpLayout(){
        updateUI()

        binding.btnNext.setOnClickListener {
            findNavController().navigate(R.id.action_send_email_request)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
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
        val layout = binding.selectDocContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            mainCell.removeAllViews()
        }
        layout.removeAllViewsInLayout()

        updateUI()
    }

    private fun updateUI(){
        var mainCell: ConstraintLayout = layoutInflater.inflate(R.layout.select_doc_main_cell, null) as ConstraintLayout
        for (item in combineDocList) {
            mainCell = layoutInflater.inflate(R.layout.select_doc_main_cell, null) as ConstraintLayout
            mainCell.selectedDocTitle.text = item.docType
            mainCell.setOnClickListener {
                val bundle = bundleOf(AppConstant.docTypeObject to item)
                findNavController().navigate(R.id.action_doc_detail_fragment , bundle)
            }
            if(item.docMessage.isNotEmpty() && item.docMessage.isNotBlank()) {
                mainCell.selectedDocDetail.visibility = View.VISIBLE
                mainCell.selectedDocDetail.text = item.docMessage
            }
            else
                mainCell.selectedDocDetail.visibility = View.GONE

            binding.selectDocContainer.addView(mainCell)
        }
    }

}