package com.rnsoft.colabademo


import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import android.widget.ArrayAdapter
import com.rnsoft.colabademo.databinding.*


@AndroidEntryPoint
class TestFieldsFragment : BaseFragment() {

    private var _binding: TestFieldsLayoutBinding? = null
    private val binding get() = _binding!!

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = TestFieldsLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val stateAdapter = ArrayAdapter(requireContext(), R.layout.autocomplete_text_view,  AppSetting.states)
        binding.stateCompleteTextView.setAdapter(stateAdapter)

        binding.stateCompleteTextView.setOnFocusChangeListener { _, _ ->
            binding.stateCompleteTextView.showDropDown()
        }
        binding.stateCompleteTextView.setOnClickListener{
            binding.stateCompleteTextView.showDropDown()
        }

        super.addListeners(binding.root)

        return root
    }

}