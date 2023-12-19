package com.minbio.erp.utils

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

class DownloadFile(_context: Context, _fileName: String, _fileExt: String) :
    AsyncTask<String, Int, String>() {

    private val fileName = _fileName
    private val fileExt = _fileExt
    private val context = _context
    private var filePath = ""

    init {
        val myDirectory = File(Environment.getExternalStorageDirectory(), "Istarte")
        if (!myDirectory.exists()) {
            myDirectory.mkdirs()
        }
        filePath = "$myDirectory/$fileName.$fileExt"
    }

    override fun doInBackground(vararg params: String?): String? {
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            val url = URL(params[0])
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.responseCode
                    .toString() + " " + connection.responseMessage
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            val fileLength: Int = connection.contentLength

            // download the file
            input = connection.getInputStream()
            output = FileOutputStream(filePath)
            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int = 0
            while (input.read(data).also { count = it } != -1) {
                // allow canceling with back button
                if (isCancelled) {
                    input.close()
                    return null
                }
                total += count.toLong()
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((total * 100 / fileLength).toInt())
                output.write(data, 0, count)
            }


        } catch (e: java.lang.Exception) {
            return e.toString()
        } finally {
            try {
                output?.close()
                input?.close()
            } catch (ignored: IOException) {
            }
            connection?.disconnect()
        }
        return null
    }

    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        AppUtils.dismissDialog()
        val file = File(filePath)
        val uri: Uri
        uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            FileProvider.getUriForFile(
                context,
                context.packageName.toString() + ".provider",
                file
            )
        } else {
            Uri.fromFile(file)
        }

        val intent = Intent(Intent.ACTION_VIEW)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        if (fileExt.equals("xls", true) || fileExt.equals("xlsx", true))
            intent.setDataAndType(uri, "application/vnd.ms-excel")
        else if (fileExt.equals("pdf", true))
            intent.setDataAndType(uri, "application/pdf");

        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "Application not found", Toast.LENGTH_SHORT).show()
        }
    }

}
