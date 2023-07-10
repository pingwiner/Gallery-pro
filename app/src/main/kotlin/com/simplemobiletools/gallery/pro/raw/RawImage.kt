package com.simplemobiletools.gallery.pro.raw

import android.content.Context
import android.net.Uri
import android.util.Log
import com.homesoft.photo.libraw.LibRaw
import java.io.File
import java.nio.ByteBuffer


class RawImage {
    private var libRaw: LibRaw = LibRaw.newInstance()

    private var _orientation: Int = 0
    val orientation: Int
        get() = _orientation

    private var _data: ByteArray? = null
    val data: ByteArray?
        get() = _data

    val orientationInDegrees: Int
        get() = LibRaw.toDegrees(orientation)

    fun open(context: Context, path: String) {
        val uri = if (path.startsWith("content://")) {
            Uri.parse(path)
        } else {
            Uri.fromFile(File(path))
        }

        try {
            val pfd = context.contentResolver.openFileDescriptor(
                uri, "r", null
            ) ?: return
            libRaw.use {
                val fd = pfd.detachFd()
                val result = it.openFd(fd)
                _orientation = it.orientation
                if (it.thumbnail != null) {
                    _data = ByteArray(it.thumbnail.remaining())
                    it.thumbnail.get(_data!!)
                }
                pfd.close()
                if (result != 0) {
                    Log.e("ERROR", "openFd failed: $path")
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", e.toString())
        }
    }

    fun recycle() {
        _data = null
    }
}
