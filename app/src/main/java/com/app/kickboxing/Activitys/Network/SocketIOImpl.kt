package com.app.kickboxing.Activitys.Network

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.app.kickboxing.Activitys.utils.Prefs
import com.app.kickboxing.BuildConfig
import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONException

import java.net.URI

class SocketIOImpl : SocketIO {

        private var socket: Socket? = null
        private var SOCKET_BASE_URL = "http://192.168.3.223:7004"

    //LIVE URL
     //   private var SOCKET_BASE_URL = "http://1.6.98.141:9152"

    private var isConnecting = false

    @Synchronized
    override fun connect(){
        try {
            socket = IO.socket(
                URI(SOCKET_BASE_URL),
                IO.Options().apply {
                    secure = true
                    reconnection = true
                    timeout = 5000
                    extraHeaders = mapOf("Authorization" to listOf("Bearer " + Prefs.init().accessToken))
                    transports = listOf(
                        Polling.NAME,
                        WebSocket.NAME
                    ).toTypedArray()
                })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        socket?.connect()
        setupSocketListeners()
        socket?.on(io.socket.client.Socket.EVENT_CONNECT) {
            println("Socket connected!")
        }
        }


    @Synchronized
    override fun disconnect() {
        Log.d("SocketObserver_1","Requesting socket disconnection from SocketIO disconnect()")
        socket?.disconnect()
        socket?.close()
        socket = null    }

    @Synchronized
    override fun isConnected(): Boolean {

        return if (socket?.connected() != null) {
            socket?.connected()!!
        } else {
            false
        }
    }

    @Synchronized
    override fun listenOn(key: String, value: Emitter.Listener) {
        socket?.on(key, value)
    }

    @Synchronized
    override fun listenOff(key: String) {
        socket?.off(key)
    }

    @Synchronized
    override fun <T> emitMessage(key: String, value: T, onResult: (key: String, value: T) -> Unit) {
        if (isConnected()) {
            try {
                socket?.emit(key, value)

                println("CallProcess->EmitMessage ${key} ${socket?.connected()}")
            } catch (e: JSONException) {
                println("CallProcess->EmitMessage${e.message}")
                e.printStackTrace()
            }
        }
    }


    private fun setupSocketListeners() {
        listenOn(
            io.socket.client.Socket.EVENT_CONNECT
        ) { args ->
            Handler(Looper.getMainLooper()).post {
                Log.d("EVENApiT CONNECTED", isConnected().toString())

            }
        }
        listenOn(
            io.socket.client.Socket.EVENT_DISCONNECT
        ) { args ->


            Handler(Looper.getMainLooper()).post {
         //       Log.d("EVENT CONNECT DISCONNECT", args[0].toString())
            }
        }


        listenOn(
            io.socket.client.Socket.EVENT_CONNECT_ERROR
        ) {

                args ->
            Log.d("EVENT CONNECT ERROR", args[0].toString())

            Handler(Looper.getMainLooper()).post {

            }
        }


    }


}