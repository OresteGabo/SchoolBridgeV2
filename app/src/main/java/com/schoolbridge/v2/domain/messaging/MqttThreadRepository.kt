package com.schoolbridge.v2.domain.messaging

import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken
import org.eclipse.paho.client.mqttv3.MqttAsyncClient
import org.eclipse.paho.client.mqttv3.MqttCallback
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import java.time.LocalDateTime

//class MqttThreadRepository() { // The constructor no longer needs the URL as an argument
//
//    // Dynamically choose the MQTT broker URL
//    private val brokerUrl: String
//        get() {
//            return if (isRunningOnEmulator()) {
//                "ssl://broker.hivemq.com:8883"
//                //"tcp://10.0.2.2:1883" // 10.0.2.2 is the special IP for localhost on an emulator
//            } else {
//                "tcp://172.20.10.3:1883" // Your physical machine's IP address
//            }
//        }
//
//    private fun isRunningOnEmulator(): Boolean {
//        // This is the code you provided, it's a reliable check
//        return (Build.FINGERPRINT.startsWith("generic")
//                || Build.FINGERPRINT.lowercase().contains("emulator")
//                || Build.MODEL.contains("Emulator")
//                || Build.MODEL.contains("Android SDK built for x86")
//                || Build.MANUFACTURER.contains("Genymotion")
//                || Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic")
//                || "google_sdk" == Build.PRODUCT)
//    }
//
//    private val clientId = "SchoolBridge_" + System.currentTimeMillis()
//    private val mqttClient by lazy {
//        Log.d("MQTT", "Initializing MQTT client with broker URL: $brokerUrl")
//        MqttAsyncClient(brokerUrl, clientId, null)
//    }
//
//    private val _threads = MutableStateFlow<List<MessageThread>>(emptyList())
//    val threads: StateFlow<List<MessageThread>> = _threads
//
//    init {
//        Log.d("MQTT", "connectAndSubscribe called in init")
//        connectAndSubscribe()
//    }
//
//    private fun connectAndSubscribe() {
//        Log.d("MQTT", "connectAndSubscribe call reached")
//        // Your connection and callback logic remains the same
//        val options = MqttConnectOptions().apply {
//            isAutomaticReconnect = true
//            isCleanSession = true
//        }
//
//        Log.d("MQTT", "Setting up callback and connection options.")
//
//        mqttClient.setCallback(object : MqttCallback {
//            override fun messageArrived(topic: String?, message: MqttMessage?) {
//                Log.d("MQTT", "Message arrived on topic: $topic")
//                val payload = message?.toString() ?: return
//                Log.d("MQTT", "Message payload: $payload")
//                if (topic != null && topic.startsWith("schoolbridge/threads/")) {
//                    val threadId = topic.removePrefix("schoolbridge/threads/")
//                    //addMessageToThread(threadId, payload)
//                }
//            }
//
//            override fun connectionLost(cause: Throwable?) {
//                Log.e("MQTT", "Connection lost", cause)
//                Log.d("MQTT", "DConnection lost", cause)
//            }
//
//            override fun deliveryComplete(token: IMqttDeliveryToken?) {
//                // not needed here
//            }
//        })
//
//        Log.d("MQTT", "Attempting to connect to broker at: $brokerUrl with client ID: $clientId")
//
//        mqttClient.connect(options).actionCallback = object : org.eclipse.paho.client.mqttv3.IMqttActionListener {
//            override fun onSuccess(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?) {
//                Log.d("MQTT", "onSuccess: Connected to broker")
//                if (mqttClient.isConnected) {
//                    Log.d("MQTT", "Client is connected. Proceeding with subscription.")
//                    try {
//                        mqttClient.subscribe("schoolbridge/threads/#", 1, null, null)
//                        Log.d("MQTT", "Subscribed to topic: schoolbridge/threads/#")
//                    } catch (e: Exception) {
//                        Log.e("MQTT", "Subscription failed due to an exception.", e)
//                    }
//                } else {
//                    Log.d("MQTT", "onSuccess called but client is not connected. Something is wrong.")
//                }
//            }
//
//            override fun onFailure(asyncActionToken: org.eclipse.paho.client.mqttv3.IMqttToken?, exception: Throwable?) {
//                Log.e("MQTT", "onFailure: Connection failed", exception)
//                if (exception != null) {
//                    Log.d("MQTT", "DConnection failed. Exception message: ${exception.message}")
//                    Log.d("MQTT", "DConnection failed. Exception cause: ${exception.cause}")
//                } else {
//                    Log.d("MQTT", "DConnection failed with no specific exception provided.")
//                }
//            }
//        }
//    }
///*
//    // Rest of your class...
//    private fun addMessageToThread(threadId: String, message: String) {
//        val now = LocalDateTime.now()
//        val newMsg = Message(
//            sender = "MQTT",
//            content = message,
//            timestamp = now,
//            attachments = emptyList(),
//            isUnread = false
//        )
//
//        val current = _threads.value.toMutableList()
//        val thread = current.find { it.id == threadId }
//        if (thread == null) {
//            current.add(
//                MessageThread(
//                    id = threadId,
//                    subject = "Thread $threadId",
//                    participants = listOf("MQTT"),
//                    messages = listOf(newMsg),
//                    lastSnippet = message.take(60),
//                    lastDate = now
//                )
//            )
//        } else {
//            val updated = thread.copy(
//                messages = thread.messages + newMsg,
//                lastSnippet = message.take(60),
//                lastDate = now
//            )
//            current[current.indexOf(thread)] = updated
//        }
//
//        CoroutineScope(Dispatchers.Main).launch {
//            _threads.value = current
//        }
//    }
//    */
//}