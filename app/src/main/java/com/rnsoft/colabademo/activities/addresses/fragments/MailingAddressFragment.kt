package com.rnsoft.colabademo


import android.content.SharedPreferences
import android.content.res.ColorStateList
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.*
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.rnsoft.colabademo.activities.model.StatesModel
import com.rnsoft.colabademo.databinding.MailingAddressLayoutBinding
import com.rnsoft.colabademo.utils.CustomMaterialFields
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.coroutineScope
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

@AndroidEntryPoint
class MailingAddressFragment : BaseFragment(), PlacePredictionAdapter.OnPlaceClickListener {

    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private var _binding: MailingAddressLayoutBinding? = null
    private val binding get() = _binding!!
    private var predicationList: ArrayList<String> = ArrayList()
    private lateinit var token: AutocompleteSessionToken
    private lateinit var placesClient: PlacesClient
    private lateinit var predictAdapter: PlacePredictionAdapter
    private var map: HashMap<String, String> = HashMap()
    private var mailingAddressModel : AddressModel? = null
    private var countyFullList: ArrayList<CountiesModel> = arrayListOf()
    private var countryFullList: ArrayList<CountriesModel> = arrayListOf()
    private var stateFullList: ArrayList<StatesModel> = arrayListOf()
    private val viewModel : RealEstateViewModel by activityViewModels()
    var firstName : String? = null
    var lastName : String? = null
    private val primaryInfoViewModel : PrimaryBorrowerViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MailingAddressLayoutBinding.inflate(inflater, container, false)
        val root: View = binding.root
        super.addListeners(binding.root)

        val activity = (activity as? BorrowerAddressActivity)
        activity?.firstName?.let {
            firstName = it
        }
        activity?.lastName?.let {
            lastName = it
        }

        if(firstName !=null && lastName !=null){
            binding.borrowerName.setText(firstName.plus(" ").plus(lastName))
        }

        //activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        binding.btnSaveMailingAddress.setOnClickListener {
            checkValidations()
        }

        setUpUI()
        getDropDownData()
        setUpCompleteViewForPlaces()
        initializeUSAstates()
        setAddressData()

        binding.backButton.setOnClickListener {
            val message = "Are you sure you want to delete Richard's Mailing Address?"
            AddressNotSavingDialogFragment.newInstance(message).show(
                childFragmentManager,
                AddressNotSavingDialogFragment::class.java.canonicalName
            )
            //findNavController().popBackStack()
        }

        binding.btnDeleteMailingAddress.setOnClickListener {
            var message = "Are you sure you want to delete Mailing Address?"
            if(firstName != null) {
                message = "Are you sure you want to delete ".plus(firstName).plus("'s Mailing Address?")
            }
            DeleteMailingAddressDailog.newInstance(message).show(childFragmentManager, DeleteMailingAddressDailog::class.java.canonicalName)
        }

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.mailingAddressParentLayout.setOnClickListener {
            HideSoftkeyboard.hide(requireActivity(),binding.mailingAddressParentLayout)
            super.removeFocusFromAllFields(binding.mailingAddressParentLayout)
        }

        return root
    }

    private fun setAddressData(){
        try {
            if (arguments != null) {
                mailingAddressModel = arguments?.getParcelable(AppConstant.mailing_address)!!
                mailingAddressModel?.let {
                    it.street?.let {
                        binding.topSearchAutoTextView.setText(it)
                        binding.streetAddressEditText.setText(it)
                        //setColor(binding.layoutSearchField)
                        //setColor(binding.streetAddressLayout)
                    }

                    it.city?.let { binding.cityEditText.setText(it) }
                    it.countryName?.let {
                        binding.countryCompleteTextView.setText(it)
                        //setColor(binding.countryCompleteLayout)
                    }
                    it.zipCode?.let {
                        binding.zipcodeEditText.setText(it)
                    }
                    it.stateName?.let {
                        binding.stateCompleteTextView.setText(it)
                        //setColor(binding.stateCompleteTextInputLayout)
                    }
                    it.countyName?.let {
                        binding.countyEditText.setText(it)
                        //  setColor(binding.countyLayout)
                    }
                    it.unit?.let {
                        binding.unitAptInputEditText.setText(it)
                    }
                    visibleAllFields()
                }
             }
            } catch (e: Exception){}
        }

    private fun getDropDownData(){
        lifecycleScope.launchWhenStarted {
            var dataCounter : Int = 0
            sharedPreferences.getString(AppConstant.token, "")?.let { authToken ->
                binding.loaderMailingAddress.visibility = View.VISIBLE
                coroutineScope {

                    viewModel.getStates(authToken)
                    // get countries
                    viewModel.getCountries(authToken)
                    // get county
                    viewModel.getCounty(authToken)
                }
            }
            lifecycleScope.launchWhenStarted {
                viewModel.states.observe(viewLifecycleOwner, { states ->
                    if (states != null && states.size > 0) {
                        val itemList: ArrayList<String> = arrayListOf()
                        for (item in states) {
                            itemList.add(item.name)
                            stateFullList.add(item)
                        }
                        val stateAdapter = ArrayAdapter(requireContext(),android.R.layout.simple_list_item_1, itemList)
                        binding.stateCompleteTextView.setAdapter(stateAdapter)

                       /* binding.stateCompleteTextView.setOnFocusChangeListener { _, _ ->
                            //binding.stateCompleteTextView.showDropDown()
                            HideSoftkeyboard.hide(requireActivity(),
                                binding.stateCompleteTextInputLayout
                            )
                        }
                        binding.stateCompleteTextView.setOnClickListener {
                            binding.stateCompleteTextView.showDropDown()
                            HideSoftkeyboard.hide(
                                requireActivity(),
                                binding.stateCompleteTextInputLayout
                            )
                        } */

                        binding.stateCompleteTextView.onItemClickListener =
                            object : AdapterView.OnItemClickListener {
                                override fun onItemClick(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    binding.stateCompleteTextInputLayout.defaultHintTextColor =
                                        ColorStateList.valueOf(ContextCompat.getColor(
                                                requireContext(),
                                                R.color.grey_color_two
                                            )
                                        )
                                    HideSoftkeyboard.hide(
                                        requireActivity(),
                                        binding.stateCompleteTextInputLayout
                                    )
                                }
                            }
                    }
                })

                viewModel.countries.observe(viewLifecycleOwner, { countries ->
                    if (countries != null && countries.size > 0) {
                        val itemList: ArrayList<String> = arrayListOf()
                        for (item in countries) {
                            itemList.add(item.name)
                            countryFullList.add(item)
                        }
                        val countryAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            itemList
                        )
                        binding.countryCompleteTextView.setAdapter(countryAdapter)

                        /*binding.countryCompleteTextView.setOnFocusChangeListener { _, _ ->
                            binding.countryCompleteTextView.showDropDown()
                            HideSoftkeyboard.hide(requireActivity(), binding.countryCompleteLayout)
                        }
                        binding.countryCompleteTextView.setOnClickListener {
                            binding.countryCompleteTextView.showDropDown()
                            HideSoftkeyboard.hide(requireActivity(), binding.countryCompleteLayout)
                        } */

                        binding.countryCompleteTextView.onItemClickListener =
                            object : AdapterView.OnItemClickListener {
                                override fun onItemClick(
                                    p0: AdapterView<*>?,
                                    p1: View?,
                                    position: Int,
                                    id: Long
                                ) {
                                    binding.countryCompleteLayout.defaultHintTextColor =
                                        ColorStateList.valueOf(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.grey_color_two
                                            )
                                        )
                                    HideSoftkeyboard.hide(
                                        requireActivity(),
                                        binding.countryCompleteLayout
                                    )
                                }
                            }
                    }
                })

                viewModel.counties.observe(viewLifecycleOwner, { counties ->
                    if (counties != null && counties.size > 0) {
                        val itemList: ArrayList<String> = arrayListOf()
                        for (item in counties) {
                            itemList.add(item.name)
                            countyFullList.add(item)
                        }
                        val countyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_list_item_1, itemList)
                        binding.countyEditText.setAdapter(countyAdapter)

                        binding.countyEditText.onItemClickListener =
                            object : AdapterView.OnItemClickListener {
                                override fun onItemClick(p0: AdapterView<*>?, p1: View?, position: Int, id: Long) {
                                    binding.countyLayout.defaultHintTextColor =
                                        ColorStateList.valueOf(
                                            ContextCompat.getColor(
                                                requireContext(),
                                                R.color.grey_color_two
                                            )
                                        )
                                    HideSoftkeyboard.hide(requireActivity(), binding.countyLayout)
                                }
                            }
                    }

                })

                binding.loaderMailingAddress.visibility = View.GONE
                setAddressData()
            }
        }
    }

    private fun setUpUI() {

        binding.topSearchAutoTextView.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                binding.topSearchTextInputLine.layoutParams.height = 3
                binding.topSearchTextInputLine.setBackgroundColor(
                    resources.getColor(
                        R.color.colaba_apptheme_blue,
                        requireActivity().theme
                    )
                )
                binding.topSearchAutoTextView.addTextChangedListener(placeTextWatcher)
            } else {
                binding.topSearchAutoTextView.removeTextChangedListener(placeTextWatcher)
                binding.topSearchTextInputLine.layoutParams.height = 1
                binding.topSearchTextInputLine.setBackgroundColor(
                    resources.getColor(
                        R.color.grey_color_four,
                        requireActivity().theme
                    )
                )

                val search: String = binding.topSearchAutoTextView.text.toString()
                if (search.length == 0) {
                    setError()
                    CustomMaterialFields.setColor(binding.layoutSearchField, R.color.grey_color_three, requireActivity())
                } else {
                    removeError()
                    CustomMaterialFields.setColor(binding.layoutSearchField, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.stateCompleteTextView.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                //binding.stateCompleteTextView.showDropDown()
                binding.stateCompleteTextView.addTextChangedListener(stateTextWatcher)
                CustomMaterialFields.setColor(binding.stateCompleteTextInputLayout, R.color.grey_color_two, requireActivity())

            } else {
                binding.stateCompleteTextView.removeTextChangedListener(stateTextWatcher)
                val state: String = binding.stateCompleteTextView.text.toString()
                if (state.length == 0) {
                    CustomMaterialFields.setError(binding.stateCompleteTextInputLayout, getString(R.string.error_field_required),requireActivity())
                    CustomMaterialFields.setColor(binding.stateCompleteTextInputLayout, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.stateCompleteTextInputLayout, requireActivity())
                    CustomMaterialFields.setColor(binding.stateCompleteTextInputLayout, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.countryCompleteTextView.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus) {
                //binding.countryCompleteTextView.showDropDown()
                binding.countryCompleteTextView.addTextChangedListener(countryTextWatcher)
                CustomMaterialFields.setColor(binding.countryCompleteLayout, R.color.grey_color_two, requireActivity())
            } else {
                binding.countryCompleteTextView.removeTextChangedListener(countryTextWatcher)
                val country: String = binding.countryCompleteTextView.text.toString()
                if (country.length == 0) {
                    CustomMaterialFields.setError(binding.countryCompleteLayout, getString(R.string.error_field_required), requireActivity())
                    CustomMaterialFields.setColor(binding.countryCompleteLayout, R.color.grey_color_three, requireActivity())
                } else {
                    CustomMaterialFields.clearError(binding.countryCompleteLayout, requireActivity())
                    CustomMaterialFields.setColor(binding.countryCompleteLayout, R.color.grey_color_two, requireActivity())
                }
            }
        }

        binding.cityEditText.setOnFocusChangeListener{ _, hasFocus: Boolean ->
            //binding.tvState.showDropDown()
            //HideSoftkeyboard.hide(requireActivity(), binding.layoutCounty)

            if(!hasFocus){
                if (binding.cityEditText.text.toString().length == 0) {
                    CustomMaterialFields.setColor(binding.countyLayout, R.color.grey_color_three, requireActivity())
                    CustomMaterialFields.setError(binding.countyLayout,getString(R.string.error_field_required),requireActivity())
                } else {
                    CustomMaterialFields.setColor(binding.countyLayout, R.color.grey_color_two, requireActivity())
                    CustomMaterialFields.clearError(binding.countyLayout, requireActivity())
                }
            }
        }

        binding.cityEditText.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.cityEditText, binding.cityLayout, requireContext(),getString(R.string.error_field_required)))

        binding.streetAddressEditText.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.streetAddressEditText, binding.streetAddressLayout,requireContext(), getString(R.string.error_field_required)))

        binding.unitAptInputEditText.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.unitAptInputEditText, binding.unitAptInputLayout, requireContext()))

        binding.zipcodeEditText.setOnFocusChangeListener(CustomFocusListenerForEditText(binding.zipcodeEditText, binding.zipcodeLayout, requireContext(),getString(R.string.error_field_required)))

        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.unitAptInputEditText, binding.unitAptInputLayout)

        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.streetAddressEditText, binding.streetAddressLayout)

        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.cityEditText, binding.cityLayout)

        CustomMaterialFields.onTextChangedLableColor(requireActivity(), binding.zipcodeEditText, binding.zipcodeLayout)

    }

    private fun setDropDownData(){
        val countryAdapter =
            ArrayAdapter(requireContext(), R.layout.autocomplete_text_view, AppSetting.countries)
        binding.countryCompleteTextView.setAdapter(countryAdapter)
        binding.countryCompleteTextView.addTextChangedListener(countryTextWatcher)


//        binding.countryCompleteTextView.setOnFocusChangeListener { _, _ ->
//            binding.countryCompleteTextView.showDropDown()
//        }

        binding.countryCompleteTextView.setOnClickListener {
            binding.countryCompleteTextView.showDropDown()
        }


        val stateAdapter =
            ArrayAdapter(requireContext(), R.layout.autocomplete_text_view, AppSetting.states)
        binding.stateCompleteTextView.setAdapter(stateAdapter)
        binding.stateCompleteTextView.addTextChangedListener(stateTextWatcher)

        binding.stateCompleteTextView.setOnClickListener {
            binding.stateCompleteTextView.showDropDown()

        }
    }

    private fun setUpCompleteViewForPlaces() {

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        predictAdapter = PlacePredictionAdapter(this@MailingAddressFragment)
        binding.fakePlaceSearchRecyclerView.apply {
            this.layoutManager = linearLayoutManager
            this.setHasFixedSize(true)
            predictAdapter = PlacePredictionAdapter(this@MailingAddressFragment)
            this.adapter = predictAdapter
        }

        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        token = AutocompleteSessionToken.newInstance()

        Places.initialize(requireContext(), "AIzaSyBzPEiQOTReBzy6W1UcIyHApPu7_5Die6w")
        // Create a new Places client instance.
        placesClient = Places.createClient(requireContext())


        //autoCompleteAdapter = ArrayAdapter(requireContext(), R.layout.autocomplete_text_view,  predicationList)
        //binding.topSearchAutoTextView.freezesText = false
        //binding.topSearchAutoTextView.threshold = 4
        //binding.topSearchAutoTextView.setAdapter(autoCompleteAdapter)
        binding.topSearchAutoTextView.dropDownHeight = 0

        /*binding.topSearchAutoTextView.setOnFocusChangeListener { p0: View?, hasFocus: Boolean ->
            if (hasFocus)
                binding.topSearchAutoTextView.addTextChangedListener(placeTextWatcher)
            else
                binding.topSearchAutoTextView.removeTextChangedListener(placeTextWatcher)
        } */


        binding.topSearchAutoTextView.setOnClickListener {
            //binding.topSearchAutoTextView.addTextChangedListener(placeTextWatcher)
        }

    }

    private val placeTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.topSearchAutoTextView.text.toString()
            if (str.length >= 3) {
                removeError()
                searchForGooglePlaces(str)
            } else
                if (str.length in 0..2) {
                    binding.fakePlaceSearchRecyclerView.visibility = View.GONE
                    predictAdapter.setPredictions(null)
                } else if (str.isEmpty()) {
                    binding.fakePlaceSearchRecyclerView.visibility = View.GONE
                    predictAdapter.setPredictions(null)
                    //binding.topSearchAutoTextView.clearFocus()
                    //hideKeyBoard()
                    //binding.topSearchAutoTextView.removeTextChangedListener(this)
                    //binding.topSearchAutoTextView.clearFocus()
                }
        }
    })

    private val countryTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.countryCompleteTextView.text.toString()
            if (str.length == 0) {
                CustomMaterialFields.setColor(
                    binding.countryCompleteLayout,
                    R.color.grey_color_three,
                    requireActivity()
                )
            } else {
                CustomMaterialFields.clearError(binding.countryCompleteLayout, requireActivity())
                CustomMaterialFields.setColor(
                    binding.countryCompleteLayout,
                    R.color.grey_color_two,
                    requireActivity()
                )
            }
        }
    })

    private val stateTextWatcher = (object : TextWatcher {
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun afterTextChanged(s: Editable) {
            val str: String = binding.stateCompleteTextView.text.toString()
            if (str.length == 0) {
                CustomMaterialFields.setColor(
                    binding.stateCompleteTextInputLayout,
                    R.color.grey_color_three,
                    requireActivity()
                )
            } else {
                CustomMaterialFields.clearError(
                    binding.stateCompleteTextInputLayout,
                    requireActivity()
                )
                CustomMaterialFields.setColor(
                    binding.stateCompleteTextInputLayout,
                    R.color.grey_color_two,
                    requireActivity()
                )
            }
        }
    })

    private fun searchForGooglePlaces(queryPlace: String) {

        val TAG = "OTHER_WAY-"

        binding.fakePlaceSearchRecyclerView.visibility = View.VISIBLE

        // Use the builder to create a FindAutocompletePredictionsRequest.
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
                   // Log.e(TAG, prediction.getFullText(null).toString())


                }
                predictAdapter.setPredictions(response.autocompletePredictions)

            }.addOnFailureListener { exception: Exception? ->
                if (exception is ApiException) {
                    //Log.e(TAG, "Place not found: " + exception.statusCode)
                }
            }

        //Log.e("predicationList", predicationList.size.toString())


        //var al2: ArrayList<String> = ArrayList<String>(predicationList.subList(1, 4))

        //if(predicationList.size>20)
        //predicationList = ArrayList(predicationList.subList(0,predicationList.size/2)


        // predictAdapterModified.notifyDataSetChanged()


        //predicationList.add("irfan")

        //this.autoCompleteAdapter.notifyDataSetChanged()
        //binding.topSearchAutoTextView.postDelayed({
        //(binding.topSearchAutoTextView as AutoCompleteTextView).showDropDown()

        //}, 200)
    }

    override fun onPlaceClicked(place: AutocompletePrediction) {
        predictAdapter.setPredictions(null)
        binding.fakePlaceSearchRecyclerView.visibility = View.GONE
        val placeSelected = place.getFullText(null).toString()
        binding.topSearchAutoTextView.clearFocus()
        hideKeyBoard()
        binding.topSearchAutoTextView.removeTextChangedListener(placeTextWatcher)
        binding.topSearchAutoTextView.setText(placeSelected)
        binding.topSearchAutoTextView.clearFocus()

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


            locality?.let { binding.cityEditText.setText(it) }
            //stateName?.let {  binding.stateCompleteTextView.setText(it) }
            subLocality?.let { binding.countyEditText.setText(it) }
            postalCode?.let { binding.zipcodeEditText.setText(it) }
            countryName?.let { binding.countryCompleteTextView.setText(it) }
            //addressLine?.let {  binding.streetAddressEditText.setText(it) }

            binding.streetAddressEditText.setText(place.getPrimaryText(null))

            premises?.let { binding.unitAptInputEditText.setText(it) }
            //unitAptInputEditText.setText(place.getSecondaryText(null))
            //featureName?.let {  binding.cityEditText.setText(it) }
            //countryCode?.let {  binding.cityEditText.setText(it) }
            //cityName?.let {  binding.cityEditText.setText(it) }
            //Log.e("Bingo ", " = " + subLocality + " " + locality + "  " + postalCode)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val extractState = place.getFullText(null)
        var stateCode =
            extractState.substring(extractState.lastIndexOf(",") - 2, extractState.lastIndexOf(","))
        stateCode = stateCode.capitalize()

        if (map.get(stateCode) != null)
            binding.stateCompleteTextView.setText(map.get(stateCode))
        else
            binding.stateCompleteTextView.setText("")

        visibleAllFields()
    }

    private fun checkValidations(){

        val searchBar: String = binding.topSearchAutoTextView.text.toString()
        val country: String = binding.countryCompleteTextView.text.toString()
        val state: String = binding.stateCompleteTextView.text.toString()
        val street = binding.streetAddressEditText.text.toString()
        val city = binding.cityEditText.text.toString()
        val county = binding.countyEditText.text.toString()
        val zipCode = binding.zipcodeEditText.text.toString()

        if (searchBar.isEmpty() || searchBar.length == 0) {
            setError()
        }
        if(binding.streetAddressLayout.visibility == View.VISIBLE){
            if(street.isEmpty() || street.length == 0) {
                CustomMaterialFields.setError(binding.streetAddressLayout,getString(R.string.error_field_required),requireActivity())
            }
            if(city.isEmpty() || city.length == 0) {
                CustomMaterialFields.setError(binding.cityLayout,getString(R.string.error_field_required),requireActivity())
            }

            if(zipCode.isEmpty() || zipCode.length == 0) {
                CustomMaterialFields.setError(binding.zipcodeLayout,getString(R.string.error_field_required),requireActivity())
            }
            if(country.isEmpty() || country.length == 0) {
                CustomMaterialFields.setError(binding.countryCompleteLayout,getString(R.string.error_field_required),requireActivity())
            }
            if(state.isEmpty() || state.length == 0) {
                CustomMaterialFields.setError(binding.stateCompleteTextInputLayout,getString(R.string.error_field_required),requireActivity())
            }
            // clear error
            if(street.isNotEmpty() || street.length > 0) {
                CustomMaterialFields.clearError(binding.streetAddressLayout,requireActivity())
            }
            if(city.isNotEmpty() || city.length > 0) {
                CustomMaterialFields.clearError(binding.cityLayout,requireActivity())
            }

            if(zipCode.isNotEmpty() || zipCode.length > 0) {
                CustomMaterialFields.clearError(binding.zipcodeLayout,requireActivity())
            }
            if(country.isNotEmpty() || country.length > 0) {
                CustomMaterialFields.clearError(binding.countryCompleteLayout,requireActivity())
            }
            if(state.isNotEmpty() || state.length > 0) {
                CustomMaterialFields.clearError(binding.stateCompleteTextInputLayout,requireActivity())
            }

        }
        if(searchBar.length > 0 && street.length > 0 && city.length > 0 && state.length > 0 && country.length > 0 && zipCode.length > 0){
            val unit = if(binding.unitAptInputEditText.text.toString().length > 0) binding.unitAptInputEditText.text.toString() else null

            val countyName : String = binding.countryCompleteTextView.getText().toString().trim()
            val matchedCounty =  countyFullList.filter { p -> p.name.equals(countyName,true)}
            val countyId = if(matchedCounty.size > 0)
                matchedCounty.get(0).id else null

            val countryName : String = binding.countryCompleteTextView.getText().toString().trim()
           val matchedCountry =  countryFullList.filter { p -> p.name.equals(countryName,true)}
            val countryId = if(matchedCountry.size > 0) matchedCountry.get(0).id else null

            val stateName : String = binding.stateCompleteTextView.getText().toString().trim()
            val matchedState =  stateFullList.filter { p -> p.name.equals(stateName,true)}
            val stateId = if(matchedState.size > 0) matchedState.get(0).id else null

              mailingAddressModel = AddressModel(
                street = street,
                unit = unit,
                city = city,
                stateName = state,
                countryName = country,
                countyName = county,
                countyId = countyId,
                stateId = stateId,
                countryId = countryId,
                zipCode = zipCode)

               primaryInfoViewModel.saveMailingAddress(mailingAddressModel!!)
                //findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.mailing_address, mailingAddressModel)
               findNavController().popBackStack()
        }
    }

    private fun setError() {
        binding.tvError.visibility = View.VISIBLE
        binding.topSearchTextInputLine.layoutParams.height = 3
        binding.topSearchTextInputLine.setBackgroundColor(
            resources.getColor(
                R.color.colaba_red_color,
                requireActivity().theme
            )
        )
    }

    private fun removeError() {
        binding.tvError.visibility = View.GONE
        binding.topSearchTextInputLine.layoutParams.height = 1
        binding.topSearchTextInputLine.setBackgroundColor(
            resources.getColor(
                R.color.grey_color_four,
                requireActivity().theme
            )
        )
    }

    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this)
    }

    override fun onStop() {
        super.onStop()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onNotSavingAddressEvent(event: NotSavingAddressEvent) {
        if (event.boolean) {
            findNavController().popBackStack()
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMailingAddressDelete(event: MailingAddressDeleteEVent){
        if(event.boolean){
            //Log.e("MailingAddressEvent","Received")
            findNavController().previousBackStackEntry?.savedStateHandle?.set(AppConstant.delete_mailing_address,true)
            findNavController().popBackStack()
        }
    }

    private fun hideKeyBoard() {
        val inputMethodManager =
            ContextCompat.getSystemService(requireContext(), InputMethodManager::class.java)!!
        inputMethodManager.hideSoftInputFromWindow(binding.fakePlaceSearchRecyclerView.windowToken, 0)
    }

    private val bounds = RectangularBounds.newInstance(
        LatLng(24.7433195, -124.7844079),
        LatLng(49.3457868, -66.9513812)
    )

    private val bias: LocationBias = RectangularBounds.newInstance(
        LatLng(37.7576948, -122.4727051), // SW lat, lng
        LatLng(37.808300, -122.391338) // NE lat, lng
    )

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

    private fun visibleAllFields() {
        binding.cityLayout.visibility = View.VISIBLE
        binding.countyLayout.visibility = View.VISIBLE
        binding.countryCompleteLayout.visibility = View.VISIBLE
        binding.zipcodeLayout.visibility = View.VISIBLE
        binding.unitAptInputLayout.visibility = View.VISIBLE
        binding.streetAddressLayout.visibility = View.VISIBLE
        binding.stateCompleteTextInputLayout.visibility = View.VISIBLE
        binding.btnDeleteMailingAddress.visibility= View.VISIBLE

    }

}
