package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.rnsoft.colabademo.activities.model.StatesModel
import com.rnsoft.colabademo.databinding.AddressIncomeEmploymentBinding
import com.rnsoft.colabademo.databinding.AppHeaderWithCrossBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

/**
 * Created by Anita Kiran on 9/15/2021.
 */
@AndroidEntryPoint
class AddressPrevEmployment : BaseFragment(), PlacePredictionAdapter.OnPlaceClickListener {

    @Inject
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var binding: AddressIncomeEmploymentBinding
    private lateinit var toolbar: AppHeaderWithCrossBinding
    private lateinit var predictAdapter: PlacePredictionAdapter
    private lateinit var token: AutocompleteSessionToken
    private lateinit var placesClient: PlacesClient
    private var predicationList: ArrayList<String> = ArrayList()
    private val viewModel : CommonViewModel by activityViewModels()
    private var employerAddress = AddressData()
    private var countyList: ArrayList<CountiesModel> = arrayListOf()
    private var countryList: ArrayList<CountriesModel> = arrayListOf()
    private var stateList: ArrayList<StatesModel> = arrayListOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = AddressIncomeEmploymentBinding.inflate(inflater, container, false)
        toolbar = binding.headerIncomeAddress

        val title = arguments?.getString(AppConstant.TOOLBAR_TITLE)
        toolbar.titleTextView.setText(title)

        setInputFields()
        getDropDownData()
        setUpCompleteViewForPlaces()
        initializeUSAstates()


        val params: ViewGroup.LayoutParams = binding.searchSeparator.getLayoutParams()
        params.height = 1
        binding.searchSeparator.layoutParams = params

        binding.addressParentLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.addressParentLayout)
            super.removeFocusFromAllFields(binding.addressLayout)
        }

        binding.addressLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.addressLayout)
            super.removeFocusFromAllFields(binding.addressLayout)
        }

        binding.btnSave.setOnClickListener {
            saveAddress()
        }

        toolbar.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        super.addListeners(binding.root)
        return binding.root

    }

    private fun getDropDownData(){
        lifecycleScope.launchWhenStarted {
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                binding.loaderCurrentEmpAddress.visibility = View.VISIBLE
                coroutineScope {
                    viewModel.getStates(authToken)
                    // get countries
                    viewModel.getCountries(authToken)
                    // get county
                    viewModel.getCounty(authToken)
                }
            }
        }

        lifecycleScope.launchWhenStarted {

            viewModel.states.observe(viewLifecycleOwner, {  states ->
                if (states != null && states.size > 0) {
                    val itemList: ArrayList<String> = arrayListOf()
                    for (item in states) {
                        itemList.add(item.name)
                        stateList.add(item)
                    }
                    val stateAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1,itemList)
                    binding.tvState.setAdapter(stateAdapter)

                    /*binding.tvState.setOnFocusChangeListener { _, _ ->
                        binding.tvState.showDropDown()
                        HideSoftkeyboard.hide(requireActivity(), binding.layoutCounty)
                    }
                    binding.tvState.setOnClickListener {
                        binding.tvState.showDropDown()
                        HideSoftkeyboard.hide(requireActivity(), binding.layoutState)
                    } */

                    binding.tvState.onItemClickListener =
                        object : AdapterView.OnItemClickListener {
                            override fun onItemClick(
                                p0: AdapterView<*>?,
                                p1: View?,
                                position: Int,
                                id: Long
                            ) {
                                binding.layoutState.defaultHintTextColor =
                                    ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.grey_color_two
                                        )
                                    )
                                HideSoftkeyboard.hide(requireActivity(), binding.layoutState)
                            }
                        }
                }
            })

            viewModel.countries.observe(viewLifecycleOwner, { countries ->
                if (countries != null && countries.size > 0) {
                    val itemList: ArrayList<String> = arrayListOf()
                    for (item in countries) {
                        itemList.add(item.name)
                        countryList.add(item)
                    }
                    val countryAdapter =
                        ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, itemList)
                    binding.tvCountry.setAdapter(countryAdapter)

//                    binding.tvCountry.setOnFocusChangeListener { _, _ ->
//                        binding.tvCountry.showDropDown()
//                        HideSoftkeyboard.hide(requireActivity(), binding.layoutCountry)
//                    }
//                    binding.tvCountry.setOnClickListener {
//                        binding.tvCountry.showDropDown()
//                        HideSoftkeyboard.hide(requireActivity(), binding.layoutCountry)
//                    }

                    binding.tvCountry.onItemClickListener =
                        object : AdapterView.OnItemClickListener {
                            override fun onItemClick(
                                p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                                binding.layoutCountry.defaultHintTextColor =
                                    ColorStateList.valueOf(
                                        ContextCompat.getColor(requireContext(), R.color.grey_color_two))
                                HideSoftkeyboard.hide(requireActivity(), binding.layoutCountry)
                            }
                        }
                }
            })

            viewModel.counties.observe(viewLifecycleOwner, { counties ->
                if (counties != null && counties.size > 0) {
                    val itemList: ArrayList<String> = arrayListOf()
                    for (item in counties) {
                        itemList.add(item.name)
                        countyList.add(item)
                    }
                    val countyAdapter = ArrayAdapter(
                        requireContext(),
                        android.R.layout.simple_list_item_1,
                        itemList
                    )
                    binding.tvCounty.setAdapter(countyAdapter)

//                    binding.tvCounty.setOnFocusChangeListener { _, _ ->
//                        binding.tvCounty.showDropDown()
//                        HideSoftkeyboard.hide(requireActivity(), binding.layoutCounty)
//                    }
//
//                    binding.tvCounty.setOnClickListener {
//                        binding.tvCounty.showDropDown()
//                        HideSoftkeyboard.hide(requireActivity(), binding.layoutCounty)
//                    }

                    binding.tvCounty.onItemClickListener =
                        object : AdapterView.OnItemClickListener {
                            override fun onItemClick(
                                p0: AdapterView<*>?,
                                p1: View?,
                                position: Int,
                                id: Long
                            ) {
                                binding.layoutCounty.defaultHintTextColor =
                                    ColorStateList.valueOf(
                                        ContextCompat.getColor(
                                            requireContext(),
                                            R.color.grey_color_two
                                        )
                                    )
                                HideSoftkeyboard.hide(requireActivity(), binding.layoutCounty)
                            }
                        }
                }
            })

            binding.loaderCurrentEmpAddress.visibility = View.GONE

            setData()

        }
    }

    private fun setData() {
        employerAddress = arguments?.getParcelable(AppConstant.address)!!
        employerAddress.let { data->
            data.street?.let {
                binding.tvSearch.setText(it.toString())
                CustomMaterialFields.setColor(binding.layoutSearchAddress, R.color.grey_color_two, requireActivity())
                binding.edStreetAddress.setText(it)
            }
            data.city?.let {
                binding.edCity.setText(it)
                CustomMaterialFields.setColor(binding.layoutCity, R.color.grey_color_two, requireActivity())
            }
            data.countryName?.let {
                binding.tvCountry.setText(it)
                binding.layoutCountry.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two))
            }
            data.zipCode?.let { binding.edZipcode.setText(it)
            }
            data.stateName?.let {
                binding.tvState.setText(it)
                binding.layoutState.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two))
            }
            data.countyName?.let {
                binding.tvCounty.setText(it)
                CustomMaterialFields.setColor(binding.layoutCounty, R.color.grey_color_two, requireActivity())
            }
            data.unit?.let { binding.edUnitAtpNo.setText(it) }
            visibleAllFields()
        }
    }

    private fun setInputFields() {
        // set lable focus
        binding.tvSearch.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.searchSeparator.layoutParams.height = 3
                binding.searchSeparator.setBackgroundColor(resources.getColor(R.color.colaba_apptheme_blue, requireActivity().theme))
                binding.tvSearch.addTextChangedListener(placeTextWatcher)
            } else {
                binding.tvSearch.removeTextChangedListener(placeTextWatcher)
                binding.searchSeparator.layoutParams.height = 1
                binding.searchSeparator.setBackgroundColor(resources.getColor(R.color.grey_color_four, requireActivity().theme))

                val search: String = binding.tvSearch.text.toString()
                if (search.length == 0) {
                    setError()
                    CustomMaterialFields.setColor(binding.layoutSearchAddress, R.color.grey_color_three, requireActivity())
                } else {
                    removeError()
                    CustomMaterialFields.setColor(binding.layoutSearchAddress, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.tvState.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                //binding.tvState.showDropDown()
                binding.tvState.addTextChangedListener(stateTextWatcher)
                CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())

            } else {
                binding.tvState.removeTextChangedListener(stateTextWatcher)
                val state: String = binding.tvState.text.toString()
                if (state.length == 0) {
                    CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
                    CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.layoutState,requireActivity())
                    CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.tvCountry.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
              //  binding.tvCountry.showDropDown()
                binding.tvCountry.addTextChangedListener(countryTextWatcher)
                CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_two, requireActivity())

            } else {
                binding.tvCountry.removeTextChangedListener(countryTextWatcher)
                val country: String = binding.tvCountry.text.toString()
                if (country.length == 0) {
                    CustomMaterialFields.setError(binding.layoutCountry,getString(R.string.error_field_required),requireActivity())
                    CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.layoutCountry,requireActivity())
                    CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.tvCounty.setOnFocusChangeListener{ _, hasFocus: Boolean ->
            if(!hasFocus){
                if (binding.tvCounty.text.toString().length == 0) {
                    CustomMaterialFields.setColor(binding.layoutCounty, R.color.grey_color_three, requireActivity())
                    //CustomMaterialFields.setError(binding.layoutCounty,getString(R.string.error_field_required),requireActivity())
                } else {
                    CustomMaterialFields.setColor(binding.layoutCounty, R.color.grey_color_two, requireActivity())
                   // CustomMaterialFields.clearError(binding.layoutCounty, requireActivity())
                }
            }
        }

        binding.edUnitAtpNo.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edUnitAtpNo,binding.layoutUnitAptNo, requireContext()))
        binding.edStreetAddress.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edStreetAddress, binding.layoutStreetAddress, requireContext(),getString(R.string.error_field_required)))
        binding.edCity.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edCity, binding.layoutCity, requireContext(),getString(R.string.error_field_required)))
        //binding.edCounty.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edCounty, binding.layoutCounty,requireContext(),getString(R.string.error_field_required)))
        binding.edZipcode.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.edZipcode, binding.layoutZipCode,requireContext(),getString(R.string.error_field_required)))
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edUnitAtpNo, binding.layoutUnitAptNo)
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edUnitAtpNo, binding.layoutUnitAptNo)
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edStreetAddress, binding.layoutStreetAddress)
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edCity, binding.layoutCity)
        //CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edCounty,binding.layoutCounty)
        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.edZipcode, binding.layoutZipCode)

    }

    private fun saveAddress() {
        val searchBar: String = binding.tvSearch.text.toString()
        val country: String = binding.tvCountry.text.toString()
        val state: String = binding.tvState.text.toString()
        val street = binding.edStreetAddress.text.toString()
        val city = binding.edCity.text.toString()
        val county = binding.tvCounty.text.toString()
        val zipCode = binding.edZipcode.text.toString()


        if (searchBar.isEmpty() || searchBar.length == 0) {
            setError()
        }

        if(binding.layoutStreetAddress.visibility == View.VISIBLE){
            if(street.isEmpty() || street.length == 0) {
               CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
            }
            if(city.isEmpty() || city.length == 0) {
                CustomMaterialFields.setError(binding.layoutCity,getString(R.string.error_field_required),requireActivity())
            }

            if(zipCode.isEmpty() || zipCode.length == 0) {
                CustomMaterialFields.setError(binding.layoutZipCode,getString(R.string.error_field_required),requireActivity())
            }
            if(country.isEmpty() || country.length == 0) {
                CustomMaterialFields.setError(binding.layoutCountry,getString(R.string.error_field_required),requireActivity())
            }
            if(state.isEmpty() || state.length == 0) {
                CustomMaterialFields.setError(binding.layoutState,getString(R.string.error_field_required),requireActivity())
            }
            // clear error
            if(street.isNotEmpty() || street.length > 0) {
                CustomMaterialFields.clearError(binding.layoutStreetAddress,requireActivity())
            }
            if(city.isNotEmpty() || city.length > 0) {
                CustomMaterialFields.clearError(binding.layoutCity,requireActivity())
            }

            if(zipCode.isNotEmpty() || zipCode.length > 0) {
                CustomMaterialFields.clearError(binding.layoutZipCode,requireActivity())
            }
            if(country.isNotEmpty() || country.length > 0) {
                CustomMaterialFields.clearError(binding.layoutCountry,requireActivity())
            }
            if(state.isNotEmpty() || state.length > 0) {
                CustomMaterialFields.clearError(binding.layoutState,requireActivity())
            }
        }
        if (searchBar.length > 0 && street.length > 0 && city.length > 0 && state.length > 0   && country.length > 0 && zipCode.length > 0) {
            val unit =
                if (binding.edUnitAtpNo.text.toString().length > 0) binding.edUnitAtpNo.text.toString() else null

            val countyName : String = binding.tvCounty.getText().toString().trim()
            val matchedCounty =  countyList.filter { p -> p.name.equals(countyName,true)}
            val countyId = if(matchedCounty.size > 0)
                matchedCounty.get(0).id else null

            val countryName : String = binding.tvCountry.getText().toString().trim()
            val matchedCountry =  countryList.filter { p -> p.name.equals(countryName,true)}
            val countryId = if(matchedCountry.size > 0) matchedCountry.get(0).id else null

            val stateName : String = binding.tvState.getText().toString().trim()
            val matchedState =  stateList.filter { p -> p.name.equals(stateName,true)}
            val stateId = if(matchedState.size > 0)
                matchedState.get(0).id else null


            val address = AddressData(
                street = street,
                unit = unit,
                city = city,
                stateName = state,
                countryName = country,
                countyName = county,
                countyId = countyId,
                stateId = stateId,
                countryId = countryId,
                zipCode = zipCode
            )



            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.address, address)
            findNavController().popBackStack()

        }
    }

    private fun setStateAndCountyDropDown() {

        val countryAdapter = ArrayAdapter(requireContext(), R.layout.autocomplete_text_view, AppSetting.countries)
        binding.tvCountry.setAdapter(countryAdapter)
        binding.tvCountry.addTextChangedListener(countryTextWatcher)
        binding.tvCountry.setOnClickListener {
            binding.tvCountry.showDropDown()
        }

        binding.tvCountry.onItemClickListener =
            object : AdapterView.OnItemClickListener { override fun onItemClick(p0: AdapterView<*>?,p1: View?,position: Int, id: Long) {
                    binding.layoutCountry.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two))
                }
            }


        val stateAdapter = ArrayAdapter(requireContext(), R.layout.autocomplete_text_view, AppSetting.states)
        binding.tvState.setAdapter(stateAdapter)
        binding.tvState.addTextChangedListener(stateTextWatcher)
        binding.tvState.setOnClickListener {
            binding.tvState.showDropDown()
        }
        binding.tvState.onItemClickListener =
            object : AdapterView.OnItemClickListener {
                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                    binding.layoutState.defaultHintTextColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.grey_color_two))
                }
            }

    }

    private fun setUpCompleteViewForPlaces() {

        binding.recyclerviewPlaces.apply {
            this.setHasFixedSize(true)
            predictAdapter = PlacePredictionAdapter(this@AddressPrevEmployment)
            this.adapter = predictAdapter
        }
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        token = AutocompleteSessionToken.newInstance()
        Places.initialize(requireContext(), "AIzaSyBzPEiQOTReBzy6W1UcIyHApPu7_5Die6w")
        placesClient = Places.createClient(requireContext())

        binding.tvSearch.dropDownHeight = 0

    }

    private fun searchForGooglePlaces(queryPlace: String) {

        val TAG = "SubProperty-Search"
        binding.recyclerviewPlaces.visibility = View.VISIBLE

        val request =
            FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(LatLng(37.0902, 95.7129))
                //s.setCountries("USA")
                .setTypeFilter(TypeFilter.ADDRESS)
                .setSessionToken(token)
                .setQuery(queryPlace)
                .build()

        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                for (prediction in response.autocompletePredictions) {
                    //Log.e(TAG, prediction.placeId)
                    response.autocompletePredictions
                    predicationList.add(prediction.getFullText(null).toString())
                    //Log.e(TAG, prediction.getFullText(null).toString())

                }
                predictAdapter.setPredictions(response.autocompletePredictions)

            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    //Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }

        //Log.e("predicationList", predicationList.size.toString())

    }

    override fun onPlaceClicked(place: AutocompletePrediction) {
        //Log.e("which-place", "desc = " + place.getFullText(null).toString())
        predictAdapter.setPredictions(null)
        val placeSelected = place.getFullText(null).toString()
        binding.tvSearch.clearFocus()
        HideSoftkeyboard.hide(requireActivity(), binding.tvSearch)
        binding.tvSearch.removeTextChangedListener(placeTextWatcher)
        binding.tvSearch.setText(placeSelected)
        binding.tvSearch.clearFocus()

        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val addresses: List<Address>? =
                geocoder.getFromLocationName(place.getFullText(null).toString(), 1)
            val countryName: String? = addresses?.get(0)?.countryName
            val locality: String? = addresses?.get(0)?.locality
            val subLocality: String? = addresses?.get(0)?.subLocality
            val postalCode: String? = addresses?.get(0)?.postalCode
            val premises: String? = addresses?.get(0)?.premises
            //val featureName: String? = addresses?.get(0)?.featureName
            //val addressLine: String? = addresses?.get(0)?.getAddressLine(0)
            //val countryCode: String? = addresses?.get(0)?.countryCode
            //val stateName: String? = addresses?.get(0)?.locale

            /*locality?.let { binding.edCity.setText(it) }
            subLocality?.let { binding.tvCounty.setText(it) }
            postalCode?.let { binding.edZipcode.setText(it) }
            countryName?.let { binding.tvCountry.setText(it) }
            binding.edStreetAddress.setText(place.getPrimaryText(null))
            premises?.let { binding.edUnitAtpNo.setText(it) } */


            locality?.let { binding.edCity.setText(it) }
            subLocality?.let {
                binding.tvCounty.setText(it)
                CustomMaterialFields.setColor(binding.layoutCounty, R.color.grey_color_two, requireActivity())
            }
            postalCode?.let { binding.edZipcode.setText(it) }
            countryName?.let {
                binding.tvCountry.setText(it)
                CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_two, requireActivity())
            }
            binding.edStreetAddress.setText(place.getPrimaryText(null))
            premises?.let { binding.edUnitAtpNo.setText(it) }

        } catch (e: IOException) {
            e.printStackTrace()
        }

        val extractState = place.getFullText(null)
        var stateCode = extractState.substring(
            extractState.lastIndexOf(",") - 2,
            extractState.lastIndexOf(",")
        )

        stateCode = stateCode.capitalize()
        //Log.e("stateCode ", " = $stateCode")
        //Log.e("Test State - ", " = " +map.get("LA") +"  "+map.get(stateCode))

        if (map.get(stateCode) != null){
            binding.tvState.setText(map.get(stateCode))
            CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())
        }
        else
            binding.tvState.setText("")

        visibleAllFields()
        clearAllError()
    }

    private fun visibleAllFields() {
        binding.layoutCity.visibility = View.VISIBLE
        binding.layoutCounty.visibility = View.VISIBLE
        binding.layoutCountry.visibility = View.VISIBLE
        binding.layoutZipCode.visibility = View.VISIBLE
        binding.layoutUnitAptNo.visibility = View.VISIBLE
        binding.layoutStreetAddress.visibility = View.VISIBLE
        binding.layoutState.visibility = View.VISIBLE
    }

    private fun clearAllError(){
        CustomMaterialFields.clearError(binding.layoutStreetAddress,requireActivity())
        CustomMaterialFields.clearError(binding.layoutCity,requireActivity())
        CustomMaterialFields.clearError(binding.layoutCounty,requireActivity())
        CustomMaterialFields.clearError(binding.layoutZipCode,requireActivity())
        CustomMaterialFields.clearError(binding.layoutCountry,requireActivity())
        CustomMaterialFields.clearError(binding.layoutState,requireActivity())
    }

    private var map: HashMap<String, String> = HashMap()

    private fun initializeUSAstates() {
        map.put("AL", "Alabama")
        map.put("AK", "Alaska")
        map.put("AZ", "Arizona")
        map.put("AR", "Arkansas")
        map.put("CA", "California")
        map.put("CO", "Colorado")
        map.put("CT", "Connecticut")
        map.put("DE", "Delaware")
        map.put("DC", "District of Columbia")
        map.put("FL", "Florida")
        map.put("GA", "Georgia")
        map.put("HI", "Hawaii")
        map.put("ID", "Idaho")
        map.put("IL", "Illinois")
        map.put("IN", "Indiana")
        map.put("IA", "Iowa")
        map.put("KS", "Kansas")
        map.put("KY", "Kentucky")
        map.put("LA", "Louisiana")
        map.put("ME", "Maine")
        map.put("MD", "Maryland")
        map.put("MA", "Massachusetts")
        map.put("MI", "Michigan")
        map.put("MN", "Minnesota")
        map.put("MS", "Mississippi")
        map.put("MO", "Missouri")
        map.put("MT", "Montana")
        map.put("NE", "Nebraska")
        map.put("NV", "Nevada")
        map.put("NH", "New Hampshire")
        map.put("NJ", "New Jersey")
        map.put("NM", "New Mexico")
        map.put("NY", "New York")
        map.put("NC", "North Carolina")
        map.put("ND", "North Dakota")
        map.put("OH", "Ohio")
        map.put("OK", "Oklahoma")
        map.put("OR", "Oregon")
        map.put("PA", "Pennsylvania")
        map.put("RI", "Rhode Island")
        map.put("SC", "South Carolina")
        map.put("SD", "South Dakota")
        map.put("TN", "Tennessee")
        map.put("TX", "Texas")
        map.put("UT", "Utah")
        map.put("VT", "Vermont")
        map.put("VA", "Virginia")
        map.put("WA", "Washington")
        map.put("WV", "West Virginia")
        map.put("WI", "Wisconsin")
        map.put("WY", "Wyoming")
    }

    private val placeTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.tvSearch.text.toString()
            if (str.length >= 3) {
                if(binding.tvError.isVisible)
                    removeError()
                searchForGooglePlaces(str)
            } else
                if (str.length in 0..2) {
                    binding.recyclerviewPlaces.visibility = View.GONE
                    predictAdapter.setPredictions(null)
                } else if (str.isEmpty()) {
                    binding.recyclerviewPlaces.visibility = View.GONE
                    predictAdapter.setPredictions(null)
                }
        }
    })

    private val countryTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.tvCountry.text.toString()
            if (str.length == 0) {
                CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_three, requireActivity())
            } else {
                CustomMaterialFields.clearError(binding.layoutCountry,requireActivity())
                CustomMaterialFields.setColor(binding.layoutCountry, R.color.grey_color_two, requireActivity())
            }
        }
    })

    private val stateTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.tvState.text.toString()
            if (str.length == 0) {
                CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_three, requireActivity())
            } else {
                CustomMaterialFields.clearError(binding.layoutState,requireActivity())
                CustomMaterialFields.setColor(binding.layoutState, R.color.grey_color_two, requireActivity())            }
        }
    })

    private val bounds = RectangularBounds.newInstance(
        LatLng(24.7433195, -124.7844079),
        LatLng(49.3457868, -66.9513812)
    )

    private fun setError(){
        binding.tvError.visibility = View.VISIBLE
        binding.searchSeparator.layoutParams.height = 1
        binding.searchSeparator.setBackgroundColor(resources.getColor(R.color.colaba_red_color, requireActivity().theme))
    }

    private fun removeError(){
        binding.tvError.visibility = View.GONE
        binding.searchSeparator.layoutParams.height = 1
        binding.searchSeparator.setBackgroundColor(resources.getColor(R.color.grey_color_four, requireActivity().theme))
    }

}