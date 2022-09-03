package com.rnsoft.colabademo

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.get
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.DocsFilesLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.docs_type_header_cell.view.*
import kotlinx.android.synthetic.main.docs_type_middle_cell.view.*
import javax.inject.Inject


@AndroidEntryPoint
class DocsListFragment:DocsTypesBaseFragment() {

    private var _binding: DocsFilesLayoutBinding? = null
    private val binding get() = _binding!!
    private val requestDocsViewModel: RequestDocsViewModel by activityViewModels()
    private var onceLoaded = true
    private lateinit var categoryDocsList:ArrayList<CategoryDocsResponseItem>

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DocsFilesLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setUpUI()
        super.addListeners(binding.root)
        return root
    }

    private fun setUpUI(){
        binding.customDocConstraintLayout.setOnClickListener {
            findNavController().navigate(R.id.action_custom_doc)
        }
        requestDocsViewModel.getCategoryDocuments.observe(viewLifecycleOwner, { templatesList->
            if(onceLoaded) {
                templatesList?.let {
                    categoryDocsList = it
                    for (i in 0 until it.size) {
                        val modelData = it[i]
                        val mainCell: LinearLayoutCompat = createMainCell(modelData.catName, modelData.documents.size)
                        addContentToMainCell(modelData.documents, mainCell)
                        addBottomToMainCell(mainCell)

                        mainCell.visibility = View.INVISIBLE
                        binding.docsTypeParentContainer.addView(mainCell)
                        binding.docsTypeParentContainer.postDelayed({
                            hideOtherBoxes()
                            binding.docsTypeParentContainer.postDelayed({ mainCell.visibility = View.VISIBLE }, 250)
                        }, 50)
                    }
                    onceLoaded = false
                }
            }
        })

        setupSearchFunctionality()
    }

    private fun returnTopCell(mainCell: LinearLayoutCompat):View?{
        for(j in 0 until mainCell.childCount) {
            val contentCell = mainCell[j] as ConstraintLayout
            if (contentCell.tag == R.string.docs_top_cell)
                return contentCell
        }
        return null
    }

    private fun createMainCell(mainCellTitle:String , totalDocs:Int):LinearLayoutCompat{
        val mainCell: LinearLayoutCompat = layoutInflater.inflate(R.layout.docs_type_top_main_cell, null) as LinearLayoutCompat
        val topCell: View = layoutInflater.inflate(R.layout.docs_type_header_cell, null)
        topCell.tag = R.string.docs_top_cell
        topCell.cell_header_title.text =  mainCellTitle

        //topCell.total_selected.text = totalDocs.toString()
        topCell.total_selected.visibility = View.GONE
        topCell.items_selected_imageview.visibility = View.GONE


        mainCell.addView(topCell)
        // add listeners to the top cell....
        topCell.setOnClickListener { hideAllAndOpenedSelectedCell(topCell, mainCell) }
        topCell.docs_arrow_up.setOnClickListener { hideCurrentlyOpenedCell(topCell, mainCell) }

        val emptyCellStart: View = layoutInflater.inflate(R.layout.docs_type_empty_space_cell, null)
        //emptyCell.visibility = View.GONE
        mainCell.addView(emptyCellStart)
        return mainCell
    }

    private fun addContentToMainCell(templatesList:ArrayList<Document>, mainCell: LinearLayoutCompat){
        for (j in 0 until templatesList.size) {
            val modelData = templatesList[j]
            val contentCell: View = layoutInflater.inflate(R.layout.docs_type_middle_cell, null)
            contentCell.tag = R.string.docs_search_cell
            contentCell.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                modelData.locallySelected = isChecked
                setTopCellCount(mainCell, isChecked)
                if (isChecked) {
                    buttonView.setTypeface(null, Typeface.BOLD) //only text style(only bold)
                    combineDocList.add(Doc(docType = modelData.docType , docMessage = modelData.docMessage , docTypeId = modelData.docTypeId))
                }
                else {
                    buttonView.setTypeface(null, Typeface.NORMAL) //only text style(only bold)
                    combineDocList.remove(Doc(docType = modelData.docType , docMessage = modelData.docMessage , docTypeId = modelData.docTypeId))
                }
            }
            contentCell.checkbox.text = modelData.docType
            contentCell.visibility = View.VISIBLE
            contentCell.info_imageview.visibility = View.GONE
            mainCell.addView(contentCell)
        }
    }

    private fun addSearchContentToMainCell(templatesList:ArrayList<Document>, mainCell: LinearLayoutCompat,  searchKeyword: String){
        for (j in 0 until templatesList.size) {
            val modelData = templatesList[j]
            var foundWord = false
            if(modelData.docType.contains(searchKeyword, true) || searchKeyword.isEmpty() || searchKeyword.isBlank())
                foundWord = true
            if(foundWord) {
                val contentCell: View = layoutInflater.inflate(R.layout.docs_type_middle_cell, null)
                contentCell.tag = R.string.docs_search_cell
                contentCell.checkbox.setOnCheckedChangeListener { buttonView, isChecked ->
                    modelData.locallySelected = isChecked
                    setTopCellCount(mainCell, isChecked)
                    if (isChecked)
                        buttonView.setTypeface(null, Typeface.BOLD) //only text style(only bold)
                    else
                        buttonView.setTypeface(null, Typeface.NORMAL) //only text style(only bold)
                }
                contentCell.checkbox.text = modelData.docType
                contentCell.checkbox.isChecked = modelData.locallySelected
                contentCell.visibility = View.VISIBLE

                contentCell.info_imageview.visibility = View.GONE
                //contentCell.info_imageview.setOnClickListener(DocsTemplateFragment.DocsShowClickListener(modelData.name, modelData.docs, childFragmentManager))
                mainCell.addView(contentCell)
            }
        }

    }

    private fun setTopCellCount(mainCell: LinearLayoutCompat, isChecked:Boolean){
        val topCell = returnTopCell(mainCell)
        topCell?.let {
            var alreadyTicked = topCell.total_selected.text.toString()
            var totalCount = 0
            if(alreadyTicked.isNotBlank() && alreadyTicked.isNotEmpty())
                totalCount = alreadyTicked.toInt()
            if (isChecked)
                topCell.total_selected.text = (totalCount + 1).toString()
            else
                topCell.total_selected.text = (totalCount - 1).toString()

            totalCount = topCell.total_selected.text.toString().toInt()

            if (totalCount == 0) {
                topCell.total_selected.visibility = View.GONE
                topCell.items_selected_imageview.visibility = View.GONE
            } else {
                topCell.total_selected.visibility = View.VISIBLE
                topCell.items_selected_imageview.visibility = View.VISIBLE
            }
        }
    }

    private fun performSearch2(searchKeyword: String){
        val layout = binding.docsTypeParentContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            mainCell.removeAllViews()
        }
        layout.removeAllViewsInLayout()

        for (i in 0 until categoryDocsList.size) {
            val modelData = categoryDocsList[i]
            val mainCell: LinearLayoutCompat = createMainCell(modelData.catName, modelData.documents.size)
            addSearchContentToMainCell(modelData.documents, mainCell,  searchKeyword)
            addBottomToMainCell(mainCell)

            //mainCell.visibility = View.INVISIBLE
            if(mainCell.childCount>3)
                binding.docsTypeParentContainer.addView(mainCell)
            binding.docsTypeParentContainer.postDelayed({
                //hideOtherBoxes()
                //binding.docsTypeParentContainer.postDelayed({ mainCell.visibility = View.VISIBLE },250)
                                                        },50)
        }
    }


    private fun performSearch(searchKeyword:String){
        val layout = binding.docsTypeParentContainer
        var mainCell: LinearLayoutCompat?
        for (i in 0 until layout.childCount) {
            mainCell = layout[i] as LinearLayoutCompat
            for(j in 0 until mainCell.childCount) {
                val contentCell = mainCell[j] as ConstraintLayout
                if (contentCell.tag == R.string.docs_search_cell) {
                    mainCell.removeView(contentCell)
                    if(contentCell.checkbox.text.contains(searchKeyword, true))
                        contentCell.visibility = View.VISIBLE
                    else
                        contentCell.visibility = View.VISIBLE
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

    private fun addBottomToMainCell(mainCell: LinearLayoutCompat){
        val emptyCellEnd: View = layoutInflater.inflate(R.layout.docs_type_empty_space_cell, null)
        mainCell.addView(emptyCellEnd)
    }


    private fun setupSearchFunctionality(){
        /*
        binding.searchEditTextField.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.searchEditTextField.clearFocus()
                binding.searchEditTextField.hideKeyboard()
                val bundle = bundleOf(AppConstant.search_word to binding.searchEditTextField.text.toString())
                findNavController().navigate(R.id.action_search_doc_fragment , bundle)
                return@OnEditorActionListener true
            }
            false
        })
        binding.searchImageView.setOnClickListener{
            if(binding.searchEditTextField.text.toString().isNotEmpty()){
                val bundle = bundleOf(AppConstant.search_word to binding.searchEditTextField.text.toString())
                findNavController().navigate(R.id.action_search_doc_fragment , bundle)
            }
        }
        */

        binding.searchEditTextField.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable) {
                val str: String = binding.searchEditTextField.text.toString()
                if(str.isNotEmpty()) {
                    binding.searchcrossImageView.visibility = View.VISIBLE
                    performSearch2(str)
                }
                else {
                    binding.searchcrossImageView.visibility = View.INVISIBLE
                    performSearch2("")
                }
            }
        })
        binding.searchcrossImageView.setOnClickListener{
            binding.searchEditTextField.setText("")
            binding.searchEditTextField.clearFocus()
            binding.searchEditTextField.hideKeyboard()
            binding.searchcrossImageView.visibility = View.INVISIBLE
        }

    }

    private fun View.hideKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(windowToken, 0)
    }

    private fun hideAllAndOpenedSelectedCell(topCell:View, mainCell:LinearLayoutCompat){
        hideOtherBoxes() // if you want to hide other boxes....
        topCell.docs_arrow_up.visibility = View.VISIBLE
        topCell.docs_arrow_down.visibility = View.INVISIBLE
        toggleContentCells(mainCell, View.VISIBLE)
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