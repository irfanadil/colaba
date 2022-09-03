package com.rnsoft.colabademo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController


class BackToLoginFragment : BaseFragment() {
    private lateinit var root: View
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        root = inflater.inflate(R.layout.back_to_login, container, false)

        val backToButton = root.findViewById<Button>(R.id.backToLoginBtn)
        backToButton.setOnClickListener {
            findNavController().navigate(R.id.login_fragment_id, null)
        }
        super.addListeners(root as ViewGroup)
        return root
    }
}