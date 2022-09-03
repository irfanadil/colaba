package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.rnsoft.colabademo.databinding.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.bottom_borrower_common_layout.view.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class BottomLoanOfficer : BottomBorrowerBaseFragment() {

    private lateinit var binding: BottomBorrowerCommonLayoutBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: StartNewAppViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = BottomBorrowerCommonLayoutBinding.inflate(inflater, container, false)
        setupLayout()
        super.addListeners(binding.root)
        return binding.root
    }


    private fun setupLayout() {
        setUpTabs()
        binding.fourthLoImage.setOnClickListener {
            EventBus.getDefault().post(OnDismissBottomDialogEvent())
        }

        val layouts = arrayListOf(binding.userLayout1, binding.userLayout2, binding.userLayout3)
        val layoutsImageView = arrayListOf(binding.firstLoImage, binding.secondLoImage, binding.thirdLoImage)
        val layoutsName = arrayListOf(binding.firstLoName, binding.secondLoName, binding.thirdLoName)
        val layoutsDetail = arrayListOf(binding.firstLoDetail, binding.secondLoDetail, binding.thirdLoDetail)


        // Pre-call Loan officer service..
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                viewModel.getMcusByRoleId(authToken, filterLoanOfficer = true)
            }
        }
        viewModel.getLoanOfficerResponse.observe(viewLifecycleOwner, {
            if (it.code == "200" || it.status.equals("OK", true)) {
                if (it.loData != null) {
                    val rolesMcus = it.loData.roles[0].mcus
                    for(item in 0 until layouts.size){
                        layouts[item].visibility = View.VISIBLE
                        layouts[item].setOnClickListener {
                            //EventBus.getDefault().post(LoanOfficerSelectedEvent(rolesMcus[item]))
                            viewModel.setMcu(rolesMcus[item])
                            EventBus.getDefault().post(DismissBoxEvent())

                        }
                        Glide.with(requireActivity())
                            .load(rolesMcus[item].profileimageurl) // Uri of the picture
                            .circleCrop()
                            .into(layoutsImageView[item])
                        layoutsName[item].text = rolesMcus[item].fullName
                        layoutsDetail[item].text = rolesMcus[item].branchName
                    }
                }
                else
                    Timber.e("data is null....")
            }
        })
    }

    private fun setUpTabs() {}
}