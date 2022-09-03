package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.rnsoft.colabademo.databinding.ImageViewLayoutBinding
import java.io.File
import javax.inject.Inject

class ImageViewFragment : BaseFragment(), AdapterClickListener {
    private var _binding: ImageViewLayoutBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageFileName:String
    lateinit var imageView: ImageView
    lateinit var titleTextView: TextView


    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ImageViewLayoutBinding.inflate(inflater, container, false)
        val view: View = binding.root

        imageView = view.findViewById(R.id.imagesImageView)
        titleTextView = view.findViewById(R.id.imageTitleTextView)
        imageFileName = arguments?.getString(AppConstant.downloadedFileName).toString()
        titleTextView.text = imageFileName
        val file = File(requireContext().filesDir, imageFileName )

        Glide.with(requireActivity())
            .load(file) // Uri of the picture
            .into(imageView)

        hideFabIcons()

        binding.backButton.setOnClickListener {
            findNavController().popBackStack()
        }
        super.addListeners(binding.root)
        return view
    }

    override fun navigateTo(position: Int) {

    }
    override fun getSingleItemIndex(position: Int) {

    }

    fun hideFabIcons(){
        (activity as DetailActivity).hideFabIcons()
    }


}