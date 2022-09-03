package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.RadioButton
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.DemographicDetailLayoutBinding
import timber.log.Timber

/**
 * Created by Anita Kiran on 10/28/2021.
 */
class EthnicityDetailFragment : BaseFragment() {

    private var _binding: DemographicDetailLayoutBinding? = null
    private val binding get() = _binding!!
    private var detailList : ArrayList<EthnicityDetails> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DemographicDetailLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        arguments?.let {
            detailList = it.getParcelableArrayList(AppConstant.ETHNICITY_DETAILS)!!
            //Timber.e("size******"+ detailList.size)

            if(detailList.size > 0) {
                for (i in 0 until detailList.size) {
                    val radio = RadioButton(requireContext())
                    radio.text = detailList.get(i).name
                    radio.setPadding(25, 25, 0, 0)
                    radio.id = detailList.get(i).id!!
                    //checkBox.setOnCheckedChangeListener(handleRaceCheck(checkBox,checkBox.id))
                    binding.layoutDetails.addView(radio)
                }
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }



        return root
    }
}