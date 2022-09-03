package com.rnsoft.colabademo

import android.util.Log
import com.rnsoft.colabademo.ServerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.File
import java.io.FileOutputStream

class FilesDataSource(private val parentFolder: File, private val serverApi: ServerApi,

                      private val token:String, private val id:String, private val requestId:String,
                      private val docId:String,
                      private val fileId:String , private val fileName:String
) {

    suspend fun downloadZip( processCallback: (Long, Long) -> Unit): File {
       // val response = serverApi.downloadFile(id).awaitResponse()// returns the response, but it's content will be later

        val newToken = "Bearer $token"
        val response = serverApi.downloadFile( id = id, requestId = requestId, docId = docId, fileId = fileId)


        val body = response.body()
        if (response.isSuccessful && body != null) {
            val file = File(parentFolder, "$id")
            body.byteStream().use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    val data = ByteArray(8192)
                    var read: Int
                    var progress = 0L
                    val fileSize = body.contentLength()
                    while (inputStream.read(data).also { read = it } != -1) {
                        outputStream.write(data, 0, read)
                        progress += read
                        publishProgress(processCallback, progress, fileSize)
                    }
                    publishProgress(processCallback, fileSize, fileSize)
                }
            }
            return file
        } else {
            throw HttpException(response)
        }
    }

    private suspend fun publishProgress(
        callback: (Long, Long) -> Unit,
        progress: Long, //bytes
        fileSize: Long  //bytes
    ) {
        withContext(Dispatchers.Main) { // invoke callback in UI thtread
            Log.e("Progresss---", "$progress  $fileSize")
            callback(progress, fileSize)
        }
    }
}