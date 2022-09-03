package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.RadioButton
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.rnsoft.colabademo.databinding.DemographicInfoLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import javax.inject.Inject

@AndroidEntryPoint
class DemoGraphicInfoFragment : BaseFragment() {

    private val viewModel: BorrowerApplicationViewModel by activityViewModels()
    private var _binding: DemographicInfoLayoutBinding? = null
    private val binding get() = _binding!!
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var newRaceList : ArrayList<RaceResponseModel> = ArrayList()
    private var newEthnicityList : ArrayList<EthnicityResponseModel> = ArrayList()
    private var raceBaseList : ArrayList<DemoGraphicRace> = ArrayList()
    private var userName:String? = null



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DemographicInfoLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        super.addListeners(binding.root)

        val li = layoutInflater
        val view = li.inflate(R.layout.independent_checkbox,null)


        // get race selection
        viewModel.demoGraphicInfo.observe(viewLifecycleOwner,{
            it.demoGraphicData?.let { demoGraphicData ->
                demoGraphicData.race?.let { race->
                    for(i in 0 until race.size){
                        race.get(i).raceId?.let { it1 ->
                            raceBaseList.add(DemoGraphicRace(raceId= it1,raceDetails = race.get(i).raceDetails)) // list for selecting race inner items
                        }
                    }
                }
            }
        })



        viewModel.raceList.observe(viewLifecycleOwner,{
            if(it.size > 0){
                for (i in 0 until it.size) {
                    val checkBox = CheckBox(requireContext())
                    checkBox.text = it.get(i).name
                    checkBox.setPadding(25, 20, 0, 0)
                    checkBox.id = it.get(i).id
                    // check selected
                    if(raceBaseList.size > 0) {
                        for(a in 0 until raceBaseList.size) {
                           if(it.get(i).id == raceBaseList.get(a).raceId){
                               checkBox.isChecked=true
                           }
                        }
                    }
                    checkBox.setOnCheckedChangeListener(handleRaceCheck(checkBox,checkBox.id))
                    newRaceList.add(RaceResponseModel(id= it.get(i).id,name= it.get(i).name, raceDetails = it.get(i).raceDetails))
                    binding.layoutRace.addView(checkBox)
                }
            }
        })

        viewModel.ethnicityList.observe(viewLifecycleOwner,{
            if(it.size >0 ){
                for (i in 0 until it.size) {
                    val radio = RadioButton(requireContext())
                    radio.text = it.get(i).name
                    radio.setPadding(25, 20, 0, 0)
                    radio.id = it.get(i).id
                    radio.setOnCheckedChangeListener(ethnicityClick(radio,radio.id))
                    newEthnicityList.add(EthnicityResponseModel(id= it.get(i).id, name= it.get(i).name, ethnicityDetails = it.get(i).ethnicityDetails))
                    binding.layoutEthnicity.addView(radio)
                }
            }
        })

        viewModel.genderList.observe(viewLifecycleOwner,{
            if(it.size >0 ){
                for (i in 0 until it.size) {
                    val radio = RadioButton(requireContext())
                    radio.text = it.get(i).name
                    radio.setPadding(25, 20, 0, 0)
                    radio.id = it.get(i).id
                    //checkBox.setOnCheckedChangeListener(handleRaceCheck(checkBox,checkBox.id))
                    //newRaceList.add(RaceResponseModel(id= it.get(i).id,name= it.get(i).name, raceDetails = it.get(i).raceDetails))
                    //binding.layoutSex.addView(radio)
                }
            }
        })

        return root
    }

    private fun handleRaceCheck(chk: CheckBox, chkBoxId: Int): CompoundButton.OnCheckedChangeListener? {
        return object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean){
                if(!isChecked) {
                } else {
                    for(i in 0 until newRaceList.size){
                        if(newRaceList.get(i).id == chkBoxId){
                            if(newRaceList.get(i).raceDetails.size > 0){
                                val bundle = Bundle()
                                bundle.putParcelableArrayList(AppConstant.RACE_DETAILS, newRaceList.get(i).raceDetails)
                                //bundle.putParcelableArrayList(AppConstant.RACE_BASE_LIST, raceBaseList.get(i).raceDetails)
                                findNavController().navigate(R.id.navigation_race_details , bundle)
                                break
                            }
                        }
                    }
                }
            }
        }
    }


    private fun ethnicityClick(chk: RadioButton, chkBoxId: Int): CompoundButton.OnCheckedChangeListener {
        return object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if (!isChecked) {

                } else {
                    for(i in 0 until newEthnicityList.size){
                        if(newEthnicityList.get(i).id == chkBoxId){
                            if(newEthnicityList.get(i).ethnicityDetails.size > 0) {
                                val bundle = Bundle()
                                bundle.putParcelableArrayList(AppConstant.ETHNICITY_DETAILS, newEthnicityList.get(i).ethnicityDetails)
                                findNavController().navigate(R.id.navigation_ethnicity_details , bundle)
                                break
                            }
                        }
                    }
                }
            }
        }
    }



    /*
    private fun setUpDemoGraphicScreen() {
        binding.asianCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.asianInnerConstraintLayout.visibility = View.VISIBLE
            }else{
                binding.asianInnerConstraintLayout.visibility = View.GONE
            }
        }

        binding.nativeHawaianOrOtherCheckBox.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.nativeHawaianInnerLayout.visibility = View.VISIBLE
            }else{
                binding.nativeHawaianInnerLayout.visibility = View.GONE
            }
        }


        binding.hispanicOrLatino.setOnClickListener {
            binding.hispanicOrLatinoLayout.visibility = View.VISIBLE
            binding.notHispanic.isChecked = false
            binding.notTellingEthnicity.isChecked = false
        }

        binding.notHispanic.setOnClickListener{


            binding.hispanicOrLatinoLayout.visibility = View.GONE
            binding.hispanicOrLatino.isChecked = false
            binding.notTellingEthnicity.isChecked = false
        }

        binding.notTellingEthnicity.setOnClickListener{

            binding.hispanicOrLatinoLayout.visibility = View.GONE
            binding.hispanicOrLatino.isChecked = false
            binding.notHispanic.isChecked = false
        }

        binding.otherAsianCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked)
                binding.otherAsianConstraintlayout.visibility = View.VISIBLE
            else
                binding.otherAsianConstraintlayout.visibility = View.GONE
        }

        binding.otherHispanicOrLatino.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked)
                binding.otherHispanicConstraintLayout.visibility = View.VISIBLE
            else
                binding.otherHispanicConstraintLayout.visibility = View.GONE
        }

        binding.otherPacificIslanderCheckBox.setOnCheckedChangeListener{ buttonView, isChecked ->
            if(isChecked)
                binding.otherPacificIslanderConstraintLayout.visibility = View.VISIBLE
            else
                binding.otherPacificIslanderConstraintLayout.visibility = View.GONE

        }
    }
     */
}