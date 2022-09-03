package com.rnsoft.colabademo

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getDrawable
import timber.log.Timber

/**
 * Created by Anita Kiran on 10/4/2021.
 */
class EmailTemplateSpinnerAdapter(private val mContext: Context, private val viewResourceId: Int,
                                  private val items: ArrayList<Template>) : ArrayAdapter<Template?>(mContext, viewResourceId, items.toList()) {

    private val itemsAll = items.clone() as ArrayList<Template>
    private var suggestions = ArrayList<Template>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var v: View? = convertView
        if (v == null) {
            val vi = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }

      //  v?.setBackground(getDrawable(mContext, R.drawable.all_white))

        val emailTemplte: Template? = items[position]
        if (emailTemplte != null) {
            val docTitle = v?.findViewById(R.id.tv_template_type) as TextView?
            val docDesc = v?.findViewById(R.id.tv_template_desc) as TextView?
            docTitle?.text = emailTemplte.templateName
            docDesc?.text = emailTemplte.docDesc
            if (position == SendDocRequestEmailFragment.selectedItem) {
                v?.setBackgroundColor(ContextCompat.getColor(mContext,R.color.plus_circle_inside_color_with_ten_alpha))
                    docTitle?.setTypeface(null, Typeface.BOLD)



            } else {
                v?.setBackgroundColor(Color.WHITE)
                docTitle?.setTypeface(null, Typeface.NORMAL)
            }


        }
        return v!!
    }

    /*override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        /*var v: View? = convertView
        v = super.getDropDownView(position, convertView, parent)
        Log.e("adapter", ""+SendDocRequestEmailFragment.selectedItem)
        if (position == SendDocRequestEmailFragment.selectedItem) {
            v.setBackgroundColor(Color.BLUE)
        } else {
            v.setBackgroundColor(Color.WHITE)
        }
        return v */

        var v: View? = convertView
        if (v == null) {
            val vi = mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            v = vi.inflate(viewResourceId, null)
        }
        val emailTemplte: Template = items[position]
        if (emailTemplte != null) {

            val docTitle = v?.findViewById(R.id.tv_template_type) as TextView?
            val docDesc = v?.findViewById(R.id.tv_template_desc) as TextView?
            docTitle?.text = emailTemplte.templateName
            docDesc?.text = emailTemplte.docDesc
        }
        return v!!
    } */


    override fun getFilter(): Filter {
        return nameFilter
    }

    private var nameFilter: Filter = object : Filter() {
        override fun convertResultToString(resultValue: Any): String {
            return (resultValue as Template).templateName
        }

        override fun performFiltering(constraint: CharSequence?): FilterResults {
            return if (constraint != null) {
                //Timber.e("perform filtering")
                suggestions.clear()
                for (item in itemsAll) {
                    suggestions.add(item)
                }
                val filterResults = FilterResults()
                filterResults.values = suggestions
                filterResults.count = suggestions.size
                filterResults
            } else {
                FilterResults()
            }
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            val filteredList =  results?.values as ArrayList<Template>?

            if (results != null && results.count > 0) {
                clear()
                for (c: Template in filteredList ?: listOf<Template>()) {
                    add(c)
                }
                notifyDataSetChanged()
            }
        }
    }

}