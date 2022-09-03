package com.rnsoft.colabademo

import android.view.ViewGroup
import androidx.core.view.iterator
import androidx.fragment.app.Fragment
import timber.log.Timber

open class BaseFragment:Fragment() {

    protected fun addListeners(rootView: ViewGroup) {
        rootView.setOnClickListener{
            HideSoftkeyboard.hide(requireContext(),rootView)
            removeFocusFromAllFields(rootView)
        }
    }

    protected fun removeFocusFromAllFields(rootView: ViewGroup){
        for (item in rootView) {
            //if(item is ConstraintLayout || item is LinearLayoutCompat || item is LinearLayout || item is RelativeLayout || item is FrameLayout)
            if(item is ViewGroup)
                removeFocusFromAllFields(item)
            item.clearFocus()

            //Timber.e("clearing focus "+item.id)
        }
    }



}