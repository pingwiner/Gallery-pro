package com.simplemobiletools.gallery.pro.raw

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.homesoft.photo.libraw.LibRaw
import java.io.File


class RawImage {
    private var _orientation: Int = 0
    val orientation: Int
        get() = _orientation

    private var _data: ByteArray? = null
    val data: ByteArray?
        get() = _data

    val orientationInDegrees: Int
        get() = LibRaw.toDegrees(orientation)

    fun open(context: Context, path: String, getLargestSize: Boolean) {
        val uri = if (path.startsWith("content://")) {
            Uri.parse(path)
        } else {
            Uri.fromFile(File(path))
        }

        try {
            val pfd = context.contentResolver.openFileDescriptor(
                uri, "r", null
            ) ?: return
            val libRaw: LibRaw = LibRaw.newInstance()
            libRaw.use {
                val fd = pfd.detachFd()
                val result = it.openFd(fd, getLargestSize)
                _orientation = it.orientation ?: 0
                if (it.thumbnailSize > 0) {
                    _data = ByteArray(it.thumbnailSize)
                    it.getThumbnail(_data)
                }
                if (result != 0) {
                    Log.e("ERROR", "openFd failed: $path")
                }
            }
            pfd.close()
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    fun recycle() {
        _data = null
        System.gc()
    }

}
