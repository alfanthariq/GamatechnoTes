package com.alfanthariq.skeleton.utils

import android.app.Activity
import android.content.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import id.zelory.compressor.Compressor
import java.io.File
import java.io.IOException
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Base64
import java.io.ByteArrayOutputStream


object FileUtil {

    /**
     * FUNCTION TO DELETE FOLDER OR FILE
     * @param file
     *
     */
    @Throws(Exception::class)
    fun delete(file: File) {
        if (file.isDirectory) {
            file.listFiles().forEach {
                delete(it)
            }
        } else if (file.isFile) {
            file.delete()
        }
    }

    fun getRealPathFromURI(activity: Activity, contentUri: Uri): String {
        try {
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = activity.managedQuery(contentUri, proj, null, null, null)
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(column_index)
        } catch (e: Exception) {
            return contentUri.path
        }
    }

    fun compressFile(context: Context, imageFile: File): File? {
        val folder = File(Environment.getExternalStorageDirectory().toString() + "/esign/Pictures/")
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if (success) {
            var imageCompress: File? = null
            try {
                imageCompress = Compressor(context)
                        .setQuality(20)
                        .setDestinationDirectoryPath(folder.absolutePath)
                        .compressToFile(imageFile)
                galleryAddPic(context, imageCompress)
                return imageCompress
            } catch (e: Exception) {
                e.printStackTrace()
            }
        } else {
            Log.e("FileUtil", "Cannot Create Folder")
        }
        return null
    }

    fun getResizedBitmap(bm: Bitmap, newHeight: Int, newWidth: Int): Bitmap {
        val width = bm.width
        val height = bm.height
        val scaleWidth = newWidth.toFloat() / width
        val scaleHeight = newHeight.toFloat() / height

        // Create a matrix for the manipulation
        val matrix = Matrix()

        // Resize the bit map
        matrix.postScale(scaleWidth, scaleHeight)

        // Recreate the new Bitmap
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false)

    }

    @Throws(IOException::class)
    fun galleryAddPic(context: Context, file: File) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(file)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    fun copyTextToClipboard(context : Context, text : String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager?
        val clip = ClipData.newPlainText("DeviceID", text)
        clipboard!!.primaryClip = clip
    }

    fun image2String(context: Context, img : File) : String {
        try{
            val bitmap = resize(MediaStore.Images.Media.getBitmap(context.contentResolver, Uri.fromFile(img)), 800, 500)
            val outputStream = ByteArrayOutputStream()
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            val imgbyte = outputStream.toByteArray()
            val base64 = Base64.encodeToString(imgbyte, Base64.NO_WRAP)

            return base64
        } catch (e : Exception) {
            e.printStackTrace()
        }

        return ""
    }

    fun dpToPx(dp: Int): Int {
        return (dp * Resources.getSystem().displayMetrics.density).toInt()
    }

    fun resize(image: Bitmap, maxWidth: Int, maxHeight: Int): Bitmap? {
        try {
            var image = image
            if (maxHeight > 0 && maxWidth > 0) {
                val width = image.width
                val height = image.height
                val ratioBitmap = width.toFloat() / height.toFloat()
                val ratioMax = maxWidth.toFloat() / maxHeight.toFloat()

                var finalWidth = maxWidth
                var finalHeight = maxHeight
                if (ratioMax > ratioBitmap) {
                    finalWidth = (maxHeight.toFloat() * ratioBitmap).toInt()
                } else {
                    finalHeight = (maxWidth.toFloat() / ratioBitmap).toInt()
                }
                image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true)
                return image
            } else {
                return image
            }
        } catch (e : Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun setFolderImage(context: Context): Boolean {
        val folder = File(ContextWrapper(context).filesDir.absolutePath + "/images/")
        var success = true
        if (!folder.exists()) {
            success = folder.mkdirs()
        }
        if (success) {
            println("Sukses buat folder images!")
        } else {
            println("Tidak bisa membuat folder !")
        }
        return success
    }

    @Throws(IllegalArgumentException::class)
    fun convert(base64Str: String): Bitmap {
        val decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",") + 1),
                Base64.DEFAULT
        )

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
    }

    fun convert(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }
}