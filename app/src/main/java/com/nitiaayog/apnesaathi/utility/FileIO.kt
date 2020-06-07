package com.nitiaayog.apnesaathi.utility

import android.os.Environment
import java.io.File

object FileIO {

    private val TAG = FileIO::class.java.simpleName

    const val TEMP = "temp"

    private const val AppFolder = "Mvvm-Example/Media/"
    private const val SentFolder = AppFolder.plus("Sent/")

    private val ParentDirectory = Environment.getExternalStorageDirectory().path

    val SentFolderAbsolutePath = ParentDirectory.plus(File.separator).plus(SentFolder)
    val AppFolderAbsolutePath = ParentDirectory.plus(File.separator).plus(AppFolder)

    fun getTempFilename(): String {
        val file = File(ParentDirectory, "temp")
        if (!file.exists()) file.mkdirs()
        return file.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"

    }

    fun getSentFolder(): File {
        val file = File(ParentDirectory, SentFolder)
        if (!file.exists()) file.mkdirs()
        return file/*file.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"*/
    }

    fun getAppFolder(): File {
        val file = File(ParentDirectory, AppFolder)
        if (!file.exists()) file.mkdirs()
        return file/*file.absolutePath + File.separator + System.currentTimeMillis() + ".jpg"*/
    }

    fun renameFile(fromName: String, toName: String) {
        val fromFile = File(ParentDirectory.plus(File.separator).plus(SentFolder), fromName)
        val toFile = File(ParentDirectory.plus(File.separator).plus(SentFolder), toName)
        fromFile.renameTo(toFile)
    }

    @Throws(Exception::class)
    fun deleteFile(path: File): Boolean {
        if (path.exists()) {
            val files = path.listFiles() ?: return true
            for (file in files) {
                if (file.isDirectory) deleteFile(file) else file.delete()
            }
        }
        return path.delete()
    }

    @Throws(Exception::class)
    fun deleteChatImageFile(imageName: String): Boolean {
        val file = File(AppFolderAbsolutePath, imageName)
        return if (file.exists()) file.delete() else true
    }

    /*private fun moveFile(inputPath: String, inputFile: String, outputPath: String) {

        val `in`: InputStream?
        val out: OutputStream?
        try {

            //create output directory if it doesn't exist
            val dir = File(outputPath)
            if (!dir.exists()) {
                dir.mkdirs()
            }

            `in` = FileInputStream(inputPath + inputFile)
            out = FileOutputStream(outputPath + inputFile)

            val buffer = ByteArray(1024)
            var read: Int

            while ((read = `in`.read(buffer)) != -1) {
                out.write(buffer, 0, read)
            }
            `in`.close()

            // write the output file
            out.flush()
            out.close()

            File(inputPath + inputFile).delete()
        } catch (fnfe: FileNotFoundException) {
            Logs.printMessages(TAG, fnfe.message)
        } catch (e: Exception) {
            Logs.printMessages(TAG, e.message)
        }
    }*/
}