package com.rnsoft.colabademo

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.constraintlayout.widget.ConstraintLayout
import com.rnsoft.colabademo.databinding.*
import kotlinx.android.synthetic.main.lo_main_cell_layout.view.*
import com.bumptech.glide.Glide

class AssignLoanCoordinatorFragment : BaseFragment() {

    private lateinit var binding: LoMainCellParentLayoutBinding

    private lateinit var test: ConstraintLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LoMainCellParentLayoutBinding.inflate(inflater, container, false)
        setupLayout()
        super.addListeners(binding.root)
        return binding.root
    }


    private fun setupLayout() {
        val sampleLoList = getSampleLoDetails()
        var mainCell: ConstraintLayout = layoutInflater.inflate(R.layout.lo_main_cell_layout, null) as ConstraintLayout

        /*
        val options: RequestOptions = RequestOptions()
            .centerCrop()
            .placeholder(R.mipmap.rount)
            .error(R.mipmap.ic_launcher_round)

         */


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
    }

    private fun setUpTabs() {}
}