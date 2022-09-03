package com.rnsoft.colabademo

import android.content.SharedPreferences
import android.os.Bundle
import com.rnsoft.colabademo.databinding.RequestDocsActivityLayoutBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class RequestDocsActivity : BaseActivity() {
    @Inject
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var binding: RequestDocsActivityLayoutBinding
    var loanApplicationId:Int? = null
    var loanPurpose:String? = null
    var fullName: String?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RequestDocsActivityLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        overridePendingTransition(R.anim.slide_in_right, R.anim.hold)

        val extras = intent.extras
        extras?.let {
            loanApplicationId = it.getInt(AppConstant.loanApplicationId)
            fullName = it.getString(AppConstant.fullName)
        }
    }
}