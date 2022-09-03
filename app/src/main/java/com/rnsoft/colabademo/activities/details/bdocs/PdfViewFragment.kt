package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.pdfview.PDFView
import com.rnsoft.colabademo.databinding.PdfViewLayoutBinding
import java.io.File
import javax.inject.Inject

class PdfViewFragment : BaseFragment() {

    private var _binding: PdfViewLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var pdfFileName:String
    private lateinit var pdfView: PDFView
    private lateinit var titleTextView: TextView

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = PdfViewLayoutBinding.inflate(inflater, container, false)
        val view: View = binding.root

        pdfView = view.findViewById(R.id.dmitry_pdf)
        titleTextView = view.findViewById(R.id.pdfTitleTextView)


        pdfFileName = arguments?.getString(AppConstant.downloadedFileName).toString()
        val file = File(requireContext().filesDir, pdfFileName )
        pdfView.fromFile(file).show()

        titleTextView.text = pdfFileName

        binding.backButton.setOnClickListener {
           requireActivity().onBackPressed()
        }

        hideFabIcons()

        super.addListeners(binding.root)

        return view
    }


    private fun hideFabIcons(){
        (activity as DetailActivity).hideFabIcons()
    }
}