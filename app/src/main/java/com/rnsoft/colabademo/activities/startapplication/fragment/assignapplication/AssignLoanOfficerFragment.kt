package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rnsoft.colabademo.databinding.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.lo_main_cell_layout.view.*
import org.greenrobot.eventbus.EventBus
import timber.log.Timber
import javax.inject.Inject


@AndroidEntryPoint
class AssignLoanOfficerFragment : BaseFragment() {

    private lateinit var binding: LoMainCellParentLayoutBinding

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private val viewModel: StartNewAppViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = LoMainCellParentLayoutBinding.inflate(inflater, container, false)
        setupLayout()
        super.addListeners(binding.root)
        return binding.root
    }


    private fun setupLayout() {
        val sampleLoList = getSampleLoDetails()
        var mainCell: ConstraintLayout = layoutInflater.inflate(R.layout.lo_main_cell_layout, null) as ConstraintLayout


        // Pre-call Loan officer service..
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                //viewModel.getMcusByRoleId(authToken, filterLoanOfficer = true)
            }
        }
        viewModel.getLoanOfficerResponse.observe(viewLifecycleOwner, {
            if (it.code == "200" || it.status.equals("OK", true)) {
                if (it.loData != null) {
                    val rolesMcus = it.loData.roles[0].mcus

                    for(i in 0 until rolesMcus.size){
                        if(i%4==0) {
                            mainCell = layoutInflater.inflate(R.layout.lo_main_cell_layout, null) as ConstraintLayout
                            mainCell.first_container.visibility = View.VISIBLE
                            mainCell.first_container.first_lo_name.text = rolesMcus[i].fullName
                            mainCell.first_container.first_lo_detail.text = rolesMcus[i].fullName
                            Glide.with(requireActivity()).load(rolesMcus[i].profileimageurl).circleCrop().into(mainCell.first_container.first_lo_image)
                            mainCell.first_container.setOnClickListener {
                                //EventBus.getDefault().post(DismissBoxEvent())
                                viewModel.setMcu(rolesMcus[i])
                                findNavController().popBackStack()
                                //EventBus.getDefault().post(LoanOfficerSelectedEvent(rolesMcus[i]))
                            }
                            binding.loParentContainer.addView(mainCell)
                        }
                        else
                        if(i%4==1) {
                            mainCell.second_container.visibility = View.VISIBLE
                            mainCell.second_container.second_lo_name.text = rolesMcus[i].fullName
                            mainCell.second_container.second_lo_detail.text = rolesMcus[i].branchName
                            Glide.with(this).load(rolesMcus[i].profileimageurl).circleCrop().into(mainCell.second_container.second_lo_image)
                            mainCell.second_container.setOnClickListener {
                                viewModel.setMcu(rolesMcus[i])
                                findNavController().popBackStack()

                                //EventBus.getDefault().post(LoanOfficerSelectedEvent(rolesMcus[i]))
                            }
                        }
                        else
                        if(i%4==2) {
                            mainCell.third_container.visibility = View.VISIBLE
                            mainCell.third_container.third_lo_name.setText(rolesMcus[i].fullName)
                            mainCell.third_container.third_lo_detail.setText(rolesMcus[i].branchName)
                            Glide.with(this).load(rolesMcus[i].profileimageurl).circleCrop().into(mainCell.third_container.third_lo_image)
                            mainCell.third_container.setOnClickListener {
                                //EventBus.getDefault().post(DismissBoxEvent())
                                viewModel.setMcu(rolesMcus[i])
                                findNavController().popBackStack()

                                //EventBus.getDefault().post(LoanOfficerSelectedEvent(rolesMcus[i]))
                            }
                        }
                        else
                        if(i%4==3) {
                            mainCell.fourth_container.visibility = View.VISIBLE
                            mainCell.fourth_container.fourth_lo_name.setText(rolesMcus[i].fullName)
                            mainCell.fourth_container.fourth_lo_detail.setText(rolesMcus[i].branchName)
                            Glide.with(this).load(rolesMcus[i].profileimageurl).circleCrop().into(mainCell.fourth_container.fourth_lo_image)
                            mainCell.fourth_container.setOnClickListener {
                                //EventBus.getDefault().post(DismissBoxEvent())
                                viewModel.setMcu(rolesMcus[i])
                                findNavController().popBackStack()

                                //EventBus.getDefault().post(LoanOfficerSelectedEvent(rolesMcus[i]))
                            }
                        }
                    }


                }
                else
                    Timber.e("data is null....")
            }
        })

        /*
        for (i in 0 until sampleLoList.size) {

            val modelData = sampleLoList[i]

            if(i%4==0) {
                mainCell = layoutInflater.inflate(R.layout.lo_main_cell_layout, null) as ConstraintLayout
                //mainCell.tag = R.string.ass
                mainCell.first_container.visibility = View.VISIBLE
                mainCell.first_container.first_lo_name.setText(modelData.loName)
                mainCell.first_container.first_lo_detail.setText(modelData.loAffiliation)
                Glide.with(requireActivity()).load(R.drawable.b1).into(mainCell.first_container.first_lo_image)
                binding.loParentContainer.addView(mainCell)
            }
            else
            if(i%4==1) {
                mainCell.second_container.visibility = View.VISIBLE
                mainCell.second_container.second_lo_name.setText(modelData.loName)
                mainCell.second_container.second_lo_detail.setText(modelData.loAffiliation)
                Glide.with(this).load(R.drawable.b2).into(mainCell.second_container.second_lo_image)
            }
            else
            if(i%4==2) {
                mainCell.third_container.visibility = View.VISIBLE
                mainCell.third_container.third_lo_name.setText(modelData.loName)
                mainCell.third_container.third_lo_detail.setText(modelData.loAffiliation)
                Glide.with(this).load(R.drawable.b3).into(mainCell.third_container.third_lo_image)
            }
            else
            if(i%4==3) {
                mainCell.fourth_container.visibility = View.VISIBLE
                mainCell.fourth_container.fourth_lo_name.setText(modelData.loName)
                mainCell.fourth_container.fourth_lo_detail.setText(modelData.loAffiliation)
                Glide.with(this).load(R.drawable.b1).into(mainCell.fourth_container.fourth_lo_image)
            }


        }
        setUpTabs()

         */
    }

    private fun setUpTabs() {}
}