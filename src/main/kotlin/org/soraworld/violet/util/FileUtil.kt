package org.soraworld.violet.util

import java.io.*

object FileUtil {

    private const val EOF = -1
    private const val DEFAULT_BUFFER_SIZE = 1024 * 4

    @Throws(IOException::class)
    fun copyInputStreamToFile(source: InputStream?, destination: File?) {
        if (source == null || destination == null) return
        try {
            val output = openOutputStream(destination, false)
            try {
                copy(source, output)
                // don't swallow close Exception if copy completes normally
                output.close()
            } finally {
                closeQuietly(output)
            }
        } finally {
            closeQuietly(source)
        }
    }

    @Throws(IOException::class)
    private fun openOutputStream(file: File, append: Boolean): FileOutputStream {
        if (file.exists()) {
            if (file.isDirectory) {
                throw IOException("File '$file' exists but is a directory")
            }
            if (!file.canWrite()) {
                throw IOException("File '$file' cannot be written to")
            }
        } else {
            val parent = file.parentFile
            if (parent != null) {
                if (!parent.mkdirs() && !parent.isDirectory) {
                    throw IOException("Directory '$parent' could not be created")
                }
            }
        }
        return FileOutputStream(file, append)
    }

    @Throws(IOException::class)
    private fun copy(input: InputStream, output: OutputStream): Int {
        val count = copyLarge(input, output)
        return if (count > Integer.MAX_VALUE) {
            -1
        } else count.toInt()
    }

    @Throws(IOException::class)
    private fun copyLarge(input: InputStream, output: OutputStream, buffer: ByteArray = ByteArray(DEFAULT_BUFFER_SIZE)): Long {
        var count: Long = 0
        var n = input.read(buffer)
        while (EOF != n) {
            output.write(buffer, 0, n)
            count += n.toLong()
            n = input.read(buffer)
        }
        return count
    }

    private fun closeQuietly(output: OutputStream) {
        closeQuietly(output as Closeable)
    }

    private fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ignored: IOException) {
        }
    }

}
