package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.ApplicationStatusLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.layout_loan_status_item.view.*
import javax.inject.Inject

@AndroidEntryPoint
class OverviewAppStatusFragment : BaseFragment(), AdapterClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private var _binding: ApplicationStatusLayoutBinding? = null
    private val binding get() = _binding!!
    private val viewModel: DetailViewModel by activityViewModels()
    private lateinit var rootTestView: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = ApplicationStatusLayoutBinding.inflate(inflater, container, false)
        rootTestView = binding.root
        //createList()
        val detailActivity = (activity as? DetailActivity)
        detailActivity?.hideFabIcons()
        observeAppStatusList()

        binding.backButtonImageView.setOnClickListener{
            viewModel.resetMileStoneToNull()
            findNavController().popBackStack()
        }
        activity?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, navigateToPreviousScreen)

        super.addListeners(binding.root)
        return rootTestView
    }

    private val navigateToPreviousScreen: OnBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            viewModel.resetMileStoneToNull()
            findNavController().popBackStack()
        }
    }

    private fun observeAppStatusList(){
        (activity as? DetailActivity)?.let {
            viewModel.getMilestoneForLoanCenter( loanApplicationId = it.loanApplicationId!!)
        }

        viewModel.appMileStoneResponse.observe(viewLifecycleOwner,{
            if (it != null) {
                if(it.status.equals("OK", true) || it.code == "200"){
                    val mileStoneList: ArrayList<MileStoneData> = ArrayList()
                    for(item in it.mileStoneData.indices){
                        mileStoneList.add(it.mileStoneData[item])
                    }
                    createAppStatusDesign(mileStoneList)
                }
            }
        })
    }

    private fun createAppStatusDesign(mileStoneList:ArrayList<MileStoneData>){
        val parentLayout = rootTestView.findViewById(R.id.layout_parent) as ViewGroup
        val layoutInflater = layoutInflater
        var view: View

        for (i in mileStoneList.indices) {
            view = layoutInflater.inflate(R.layout.layout_loan_status_item, null)
            val textView = view.findViewById<TextView>(R.id.app_status_title)
            textView.text = mileStoneList[i].name
            if(mileStoneList[i].createdDate!=null)
                view.app_status_date.text = AppSetting.getAppStatusDateFormat(mileStoneList[i].createdDate!!)
            else
                view.app_status_date.text = ""
            if(!mileStoneList[i].isCurrent && i<(mileStoneList.size-1)) {
                view.tick_status.visibility = View.GONE
                view.app_status_circle_blue.visibility = View.GONE
                view.app_status_line_blue.visibility = View.GONE
                view.app_status_circle_grey.visibility = View.VISIBLE
                view.app_status_line_grey.visibility = View.VISIBLE
            }
            else if(i==(mileStoneList.size-1))
            {
                view.tick_status.visibility = View.GONE
                view.app_status_circle_blue.visibility = View.GONE
                view.app_status_line_blue.visibility = View.GONE
                view.app_status_circle_grey.visibility = View.VISIBLE
                view.app_status_line_grey.visibility = View.GONE
            }
            parentLayout.addView(view, i)
        }
    }

    override fun getSingleItemIndex(position: Int) {}

    override fun navigateTo(position: Int) {}

}





/*
     private var list: ArrayList<String> = ArrayList()
    lateinit var circleBlue : ImageView
    lateinit var circleGrey : ImageView
    lateinit var circleRed: ImageView
    lateinit var lineBlue :View
    lateinit var lineGrey :View

    private fun createList() {
        list.add("Application Started")
        list.add("Application Submitted")
        list.add("Processing")
        list.add("Underwriting")
        list.add("Approvals")
        list.add("Closing")
        list.add("Application Completed")
        showApplicationStatus()
    }

    private fun showApplicationStatus() {
        val parentLayout = rootTestView.findViewById(R.id.layout_parent) as ViewGroup
        val layoutInflater = layoutInflater
        var view: View

        for (i in list.indices) {
            view = layoutInflater.inflate(R.layout.layout_loan_status_item, null)
            val textView = view.findViewById<TextView>(R.id.app_status_title)
            textView.text = list.get(i)

            if(list[i] == "Application Completed"){
                val lineBlue = view.findViewById<View>(R.id.app_status_line_blue)
                lineBlue.visibility = View.GONE
            }

            parentLayout.addView(view, i)
        }
    }


      private fun initViews(){
            circleBlue = rootTestView.findViewById(R.id.app_status_circle_blue)
            circleGrey = rootTestView.findViewById(R.id.app_status_circle_grey)
            circleRed = rootTestView.findViewById(R.id.app_status_circle_red)
            lineBlue = rootTestView.findViewById(R.id.app_status_line_blue)
            lineGrey = rootTestView.findViewById(R.id.app_status_line_grey)

        }

        private fun showStatusBar(blue:Boolean,grey:Boolean,red:Boolean){

            if(blue){
                binding.appStatusCircleBlue.visibility = View.VISIBLE
                binding.appStatusLineBlue.visibility = View.VISIBLE

                binding.appStatusCircleGrey.visibility = View.GONE
                binding.appStatusLineGrey.visibility = View.GONE
                binding.appStatusCircleRed.visibility = View.GONE
            }
            if(grey){
                binding.appStatusCircleGrey.visibility = View.VISIBLE
                binding.appStatusLineGrey.visibility = View.VISIBLE
                binding.appStatusCircleBlue.visibility = View.GONE
                binding.appStatusLineBlue.visibility = View.GONE
                binding.appStatusCircleRed.visibility = View.GONE
            }
        }

        private fun showOnlyCircle(blue:Boolean,grey:Boolean,red:Boolean){
            if(blue){
                binding.appStatusCircleBlue.visibility = View.VISIBLE
                binding.appStatusLineBlue.visibility = View.GONE
                binding.appStatusCircleGrey.visibility = View.GONE
                binding.appStatusLineGrey.visibility = View.GONE
                binding.appStatusCircleRed.visibility = View.GONE
            }
            if(grey){
                binding.appStatusCircleGrey.visibility = View.VISIBLE
                binding.appStatusLineGrey.visibility = View.GONE
                binding.appStatusCircleBlue.visibility = View.GONE
                binding.appStatusLineBlue.visibility = View.GONE
                binding.appStatusCircleRed.visibility = View.GONE
            }
            if(red){
                binding.appStatusCircleRed.visibility = View.VISIBLE
                binding.appStatusCircleGrey.visibility = View.GONE
                binding.appStatusLineGrey.visibility = View.GONE
                binding.appStatusCircleBlue.visibility = View.GONE
                binding.appStatusLineBlue.visibility = View.GONE
            }
        }
 */
