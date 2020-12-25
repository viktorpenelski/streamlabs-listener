package com.github.viktorpenelski.arduino

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.util.*
import kotlin.concurrent.thread

fun main(args: Array<String>) {

    val client = ArduinoClient()
    val comPort = client.getArduinoComPort() ?: throw RuntimeException("cannot find COM port with arduino!")
    comPort.openPort()
    comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING or SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
    comPort.baudRate = 115200

    thread {
        client.attachListener(comPort)
    }

    while (true) {
        // do nothing

        Thread.sleep(3000L)

    }
}


class ArduinoClient {
    private val comPort = getArduinoComPort() ?: throw RuntimeException("cannot find COM port with arduino!")
    private val queueToWrite = ArrayDeque<String>()

    init {
        comPort.openPort()
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING, 0, 0)
        comPort.baudRate = 115200
        attachListener(comPort)
    }



    @Synchronized
    fun enqueue(data: String) {
        val dataToEnqueue = if (data.length >= 48) {
            data.subSequence(0, 48)
        } else {
            data
        }
        queueToWrite.addLast("<$dataToEnqueue>")
    }

    class SerialBuffer(
        private val maxBufferSize: Int
    ) {

        private var buffer: ByteArray = ByteArray(maxBufferSize)
        private var lastIndex = 0

        fun append(toAppend: ByteArray) {
            if (lastIndex >= maxBufferSize) {
                throw RuntimeException("cannot append beyond buffer size")
            }
            for (i in lastIndex until lastIndex + toAppend.size) {
                buffer[i] = toAppend[i - lastIndex]
            }

            lastIndex += toAppend.size
        }

        fun getBuffer(): ByteArray {
            return buffer.copyOfRange(0, lastIndex)
        }

        fun clear() {
            lastIndex = 0
            buffer = ByteArray(maxBufferSize)
        }
    }

    var NEW_LINE_BYTE = '\n'.toByte()
    val buffer = SerialBuffer(50)

    internal fun attachListener(comPort: SerialPort) {
        comPort.addDataListener(object : SerialPortDataListener {
            override fun getListeningEvents(): Int {
                return SerialPort.LISTENING_EVENT_DATA_AVAILABLE
            }

            override fun serialEvent(event: SerialPortEvent) {
                if (event.eventType != SerialPort.LISTENING_EVENT_DATA_AVAILABLE) {
                    return
                }
                val newData = ByteArray(comPort.bytesAvailable())
                val bytesRead = comPort.readBytes(newData, newData.size.toLong())
                println("Read $bytesRead bytes.")
                buffer.append(newData)
                if (newData[newData.lastIndex] == NEW_LINE_BYTE) {
                    println(String(buffer.getBuffer()))
                    buffer.clear()
                    if (queueToWrite.isNotEmpty()) {
                        val data = queueToWrite.poll()
                        // figure out why during debug data is written properly,
                        // but during actual runtime, only a few bytes are sent....
                        val bytesWritten = writeToPort(data, comPort)
                        println("Just wrote $bytesWritten bytes.")
                    }
                }
            }
        })
    }

    private fun writeToPort(data: String, comPort: SerialPort): Int {
        Thread.sleep(100L) // but whyyyyyy
        return comPort.writeBytes(data.toByteArray(), data.length.toLong())

    }

    private fun writeToPortWithOutputStream(data: String, comPort: SerialPort) {
        comPort.outputStream.write(data.toByteArray())
        comPort.outputStream.flush()
    }

    internal fun getArduinoComPort() = SerialPort.getCommPorts().firstOrNull {
        it.descriptivePortName.toLowerCase().contains("arduino") ||
                it.descriptivePortName.toLowerCase().contains("com3")
    }

}