package org.bibletranslationtools.audio

import org.wycliffeassociates.otter.common.audio.AudioFile
import org.wycliffeassociates.otter.common.audio.wav.*
import java.io.File

fun main(args: Array<String>) {
    if (args.isNotEmpty()) {
        val path = args.first()
        val file = File(path)
        when (file.isDirectory) {
            true -> {
                println("Converting files in directory: ${file.name}")
                val out = File(file, "broadcast_wav_converted").apply { mkdir() }
                convertDirectory(file, out, file)
            }
            false -> {
                val out = File(file.parent, "broadcast_wav_converted").apply { mkdir() }
                convertFile(file, out, file.parentFile)
            }
        }
    } else {
        println("Please provide a file or directory.")
    }
}

fun convertDirectory(dir: File, outDir: File, root: File) {
    val filtered = dir.listFiles().toMutableList().apply { removeAll { it.absolutePath == outDir.absolutePath } }
    for (file in filtered) {
        when (file.isDirectory) {
            true -> {
                val nestedDir = File(outDir, file.name).apply { mkdir() }
                convertDirectory(file, nestedDir, root)
            }

            false -> convertFile(file, outDir, root)
        }
    }
}

fun convertFile(file: File, outDir: File, root: File) {
    if (file.extension != "wav") return

    try {
        val wav = WavFile(file, WavMetadata(listOf(CueChunk())))
        if (wav.wavType == WavType.WAV_WITH_EXTENSION) {
            println("Converting ${file.relativeTo(root).path}")
            val out = File(outDir, file.name)
            val wos = WavOutputStream(WavFile(out, wav.channels, wav.sampleRate, wav.bitsPerSample, wav.metadata))
            val buffer = ByteArray(wav.totalAudioLength)
            val reader = AudioFile(file).reader()
            reader.open()
            reader.getPcmBuffer(buffer)
            wos.use { it.write(buffer) }
        } else {
            println("${file.name} is already a normal wav file, copying to output directory.")
            val out = File(outDir, file.name)
            file.copyTo(out)
        }
    } catch (e: Exception) {
        println("Error in file $file")
        e.printStackTrace()
    }
}