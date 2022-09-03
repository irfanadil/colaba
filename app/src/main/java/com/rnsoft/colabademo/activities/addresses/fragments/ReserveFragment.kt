package com.rnsoft.colabademo


import android.content.SharedPreferences
import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.text.capitalize
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.dialog_add_emplyment.view.*
import javax.inject.Inject


@AndroidEntryPoint
class ReserveFragment : BaseFragment() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    //private val viewModel : PrimaryBorrowerViewModel by viewModels()
    private var _binding: ReserveLayoutBinding? = null
    private val binding get() = _binding!!
    var firstName : String? = null
    var lastName : String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ReserveLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        super.addListeners(binding.root)

        val activity = (activity as? BorrowerAddressActivity)
        activity?.firstName?.let {
            firstName = it
        }
        activity?.lastName?.let {
            lastName = it
        }

        try {
            if (firstName != null && lastName != null) {
                val name = firstName!!.capitalize().plus(" ").plus(lastName!!.capitalize())
                if (name.isNotEmpty() && name.isNotBlank() && name.length > 0) {
                    binding.borrowerName.setText(name)
                    binding.tvQues.text = "Was ".plus(name) + " ever activated during their tour of duty?"
                }
            }
        } catch (e:Exception){}

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.radioButtonYes.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.radioButtonYes.setTypeface(null, Typeface.BOLD)
            else
                binding.radioButtonYes.setTypeface(null, Typeface.NORMAL)
        }

        binding.radioButton2No.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked)
                binding.radioButton2No.setTypeface(null, Typeface.BOLD)
            else
                binding.radioButton2No.setTypeface(null, Typeface.NORMAL)
        }

        binding.btnSave.setOnClickListener {
            var activated :String? = null
            if(binding.radioButtonYes.isChecked)
                activated = "Yes"

            if(binding.radioButton2No.isChecked)
                activated = "No"


            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.RESERVE_ACTIVATED, activated)
            findNavController().popBackStack()
        }

        setData()

        return root
    }

    private fun setData(){
        if(arguments != null){
            val reserve = arguments?.getBoolean(AppConstant.RESERVE_ACTIVATED)
            reserve.let {
                if(it == true)
                    binding.radioButtonYes.isChecked = true
                else
                    binding.radioButton2No.isChecked = true
            }
        }
    }

}