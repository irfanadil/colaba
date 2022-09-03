package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import com.rnsoft.colabademo.databinding.DocsTemplateLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.docs_type_header_cell.view.*
import kotlinx.android.synthetic.main.docs_type_middle_cell.view.*
import timber.log.Timber

import javax.inject.Inject
@AndroidEntryPoint
class DocsTemplateFragment:DocsTypesBaseFragment() {

    private var _binding: DocsTemplateLayoutBinding? = null
    private val binding get() = _binding!!

    private var onceLoaded = true

    private val requestDocsViewModel: RequestDocsViewModel by activityViewModels()

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DocsTemplateLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        return root
    }

    private fun setUpUI(){

        requestDocsViewModel.getTemplatesResponse.observe(viewLifecycleOwner, { templatesList->
            if(onceLoaded) {
                templatesList?.let {
                    // Custom Cell....
                    val customTemplateMainCell: LinearLayoutCompat = createMainCell("My Templates")
                    addContentToMainCell(it, customTemplateMainCell, "My Templates")
                    addBottomToMainCell(customTemplateMainCell)

                    customTemplateMainCell.visibility = View.INVISIBLE
                    binding.docsTypeParentContainer.addView(customTemplateMainCell)
                    binding.docsTypeParentContainer.postDelayed({
                        hideOtherBoxes()
                        customTemplateMainCell.visibility = View.VISIBLE
                    }, 500
                    )

                    // System Cell....
                    val systemMainCell: LinearLayoutCompat = createMainCell("System Templates")
                    addContentToMainCell(it, systemMainCell, "Tenant Template")
                    addBottomToMainCell(systemMainCell)

                    systemMainCell.visibility = View.INVISIBLE
                    binding.docsTypeParentContainer.addView(systemMainCell)
                    binding.docsTypeParentContainer.postDelayed({
                        hideOtherBoxes()
                        systemMainCell.visibility = View.VISIBLE }, 500)
                    onceLoaded = false
                }
            }
        } )

        //val sampleDocs = getSampleDocsTemplate()

    }

    private fun createMainCell(mainCellTitle:String):LinearLayoutCompat{
        val mainCell: LinearLayoutCompat = layoutInflater.inflate(R.layout.docs_type_top_main_cell, null) as LinearLayoutCompat
        val topCell: View = layoutInflater.inflate(R.layout.docs_type_header_cell, null)
        topCell.cell_header_title.text =  mainCellTitle
        //topCell.total_selected.text = modelData.totalSelected

        topCell.total_selected.visibility = View.GONE
        topCell.items_selected_imageview.visibility = View.GONE
        topCell.tag = R.string.docs_top_cell
        mainCell.addView(topCell)
        // add listeners to the top cell....
        topCell.setOnClickListener { hideAllAndOpenedSelectedCell(topCell, mainCell) }
        topCell.docs_arrow_up.setOnClickListener { hideCurrentlyOpenedCell(topCell, mainCell) }


        val emptyCellStart: View = layoutInflater.inflate(R.layout.docs_type_empty_space_cell, null)
        //emptyCell.visibility = View.GONE
        mainCell.addView(emptyCellStart)
        return mainCell
    }

    private fun addContentToMainCell(templatesList:ArrayList<GetTemplatesResponseItem>, mainCell: LinearLayoutCompat, filterString :String){
        for (j in 0 until templatesList.size) {
            val modelData = templatesList[j]
            if(modelData.type != filterString) continue
            val contentCell: View = layoutInflater.inflate(R.layout.docs_type_middle_cell, null)
            contentCell.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    for(item in modelData.docs){
                        combineDocList.add(Doc(docType = item.docType , docMessage = item.docMessage , docTypeId = item.docTypeId))
                    }
                    buttonView.setTypeface(null, Typeface.BOLD) //only text style(only bold)
                }
                else {
                    for(item in modelData.docs){
                        combineDocList.remove(item)
                    }
                    buttonView.setTypeface(null, Typeface.NORMAL) //only text style(only bold)
                }
            }
            contentCell.checkbox.text = modelData.name
            contentCell.visibility = View.VISIBLE
            contentCell.info_imageview.setOnClickListener{
                //(DocsShowClickListener(modelData.name, modelData.docs,  childFragmentManager))

                StandardChecklistDialogFragment.newInstance(modelData.name, modelData.docs).show(childFragmentManager, StandardChecklistDialogFragment::class.java.canonicalName)
            }
            mainCell.addView(contentCell)
        }
    }

    private fun addBottomToMainCell(mainCell: LinearLayoutCompat){
        val emptyCellEnd: View = layoutInflater.inflate(R.layout.docs_type_empty_space_cell, null)
        mainCell.addView(emptyCellEnd)
    }



    private fun hideAllAndOpenedSelectedCell(topCell:View, mainCell:LinearLayoutCompat){
        hideOtherBoxes() // if you want to hide other boxes....
        if(mainCell.childCount>3) { // if there are no contents, by default 3 children are added to the maincell....
            topCell.docs_arrow_up.visibility = View.VISIBLE
            topCell.docs_arrow_down.visibility = View.INVISIBLE
            Timber.e("total children are = " + mainCell.childCount.toString())
            toggleContentCells(mainCell, View.VISIBLE)
        }
        else
            SandbarUtils.showRegular(requireActivity(), "No data to display")
        //bottomCell.visibility = View.VISIBLE
    }

    private fun hideCurrentlyOpenedCell(topCell:View, mainCell:LinearLayoutCompat){
        topCell.docs_arrow_up.visibility = View.INVISIBLE
        topCell.docs_arrow_down.visibility = View.VISIBLE
        //contentCell.visibility = View.GONE
        toggleContentCells(mainCell , View.GONE)
        //bottomCell.visibility = View.GONE
    }

    private fun toggleContentCells(mainCell: LinearLayoutCompat, display:Int){
        for (j in 0 until mainCell.childCount){
            if(mainCell[j].tag != R.string.docs_top_cell)
                mainCell[j].visibility = display
        }
    }

    private fun hideOtherBoxes(){
        val layout = binding.docsTypeParentContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            for(j in 0 until mainCell.childCount) {
                val innerCell = mainCell[j] as ConstraintLayout
                if (innerCell.tag == R.string.docs_top_cell) {
                    innerCell.docs_arrow_up.visibility = View.GONE
                    innerCell.docs_arrow_down.visibility = View.VISIBLE
                } else {
                    innerCell.visibility = View.GONE
                }
            }

            /*
            val topCell = mainCell.getTag(R.string.asset_top_cell) as ConstraintLayout
            val middleCell = mainCell.getTag(R.string.asset_middle_cell) as ConstraintLayout
            val bottomCell = mainCell.getTag(R.string.asset_bottom_cell) as ConstraintLayout
            topCell.arrow_up.visibility = View.GONE
            topCell.arrow_down.visibility = View.VISIBLE
            middleCell.visibility = View.GONE
            bottomCell.visibility = View.GONE
             */
        }
    }




}


    /*
    class DocsShowClickListener(private val dialogTitle: String, private val dialogValues: ArrayList<Doc>,   private val childFragmentManager: FragmentManager) :
        View.OnClickListener {
        override fun onClick(v: View?) {
            StandardChecklistDialogFragment.newInstance(dialogTitle, dialogValues).show(childFragmentManager, StandardChecklistDialogFragment::class.java.canonicalName)
        }
    }
    */


    /*
        private val getTemplatesObserver = Observer<GetTemplatesResponse> { onNewTemplateReceived(it) }
        private fun onNewTemplateReceived(templatesList: GetTemplatesResponse?) {}
        private fun <T> LiveData<T>.removeObserver(emailValidObserver: Observer<GetTemplatesResponse>) {}
        private fun <T> LiveData<T>.observe(viewLifecycleOwner: LifecycleOwner, emailValidObserver: Observer<GetTemplatesResponse>) {}
     */
