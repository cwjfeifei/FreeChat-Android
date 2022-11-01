package com.ti4n.freechat.util

import android.content.Context
import android.media.MediaRecorder
import java.io.File

class RecordVoiceUtil(val file: File) {

    val mediaRecord = MediaRecorder().apply {
        setAudioSource(MediaRecorder.AudioSource.MIC)
        setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
        setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
        setOutputFile(file)
    }

    fun record() {
        if (!file.exists()) file.createNewFile()
        mediaRecord.prepare()
        mediaRecord.start()
    }

    fun cancel() {
        mediaRecord.stop()
        mediaRecord.release()
        if (file.exists()) {
            file.delete()
        }
    }

    fun finish() {
        mediaRecord.stop()
        mediaRecord.release()
    }
}