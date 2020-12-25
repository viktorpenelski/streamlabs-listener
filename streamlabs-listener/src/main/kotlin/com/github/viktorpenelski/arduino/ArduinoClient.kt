package com.github.viktorpenelski.arduino

import com.fazecast.jSerialComm.SerialPort
import com.fazecast.jSerialComm.SerialPortDataListener
import com.fazecast.jSerialComm.SerialPortEvent
import java.util.*
import kotlin.concurrent.thread

enum class RGBAnimations(val arduinoCode: Int) {
    RAINBOW(48),
    RAINBOW_CYCLE(49),
    THEATER_CHASE(50),
    THEATER_RAINBOW_CHASE(51),
}

internal fun getArduinoComPort() = SerialPort.getCommPorts().firstOrNull {
    it.descriptivePortName.toLowerCase().contains("arduino") ||
            it.descriptivePortName.toLowerCase().contains("com3")
}

fun main() {

    val a = Arduino()
}

class Arduino {
    private val comPort = getArduinoComPort() ?: throw RuntimeException("cannot find COM port with arduino!")

    init {
        comPort.openPort()
        comPort.setComPortTimeouts(SerialPort.TIMEOUT_WRITE_BLOCKING or SerialPort.TIMEOUT_READ_BLOCKING, 0, 0)
        comPort.baudRate = 115200
    }

    fun send(animation: RGBAnimations) {
        println("sending $animation")
        comPort.outputStream.write(animation.arduinoCode)
        comPort.outputStream.flush()
    }

}
