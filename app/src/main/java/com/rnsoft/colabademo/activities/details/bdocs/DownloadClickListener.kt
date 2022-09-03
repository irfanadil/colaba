package com.rnsoft.colabademo

import java.text.FieldPosition

interface DownloadClickListener {

        fun fileClicked(fileName:String , fileId:String,  position: Int)

}