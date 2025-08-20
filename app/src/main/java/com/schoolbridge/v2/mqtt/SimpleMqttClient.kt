package com.schoolbridge.v2.mqtt

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

class SimpleMqttClient(
    private val brokerUrl: String = "ssl://broker.hivemq.com:8883", // works better than 1883
    private val clientId: String = MqttClient.generateClientId(),
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private var client: MqttClient? = null

    fun connect(
        onConnected: (() -> Unit)? = null,
        onMessage: ((topic: String, message: String) -> Unit)? = null
    ) {
        scope.launch {
            try {
                Log.d("MQTT", "Connecting to $brokerUrl ...")

                client = MqttClient(brokerUrl, clientId, null)
                val options = MqttConnectOptions().apply {
                    isCleanSession = true
                }

                client?.setCallback(object : MqttCallback {
                    override fun connectionLost(cause: Throwable?) {
                        Log.e("MQTT", "Connection lost: ${cause?.message}")
                    }

                    override fun messageArrived(topic: String?, message: MqttMessage?) {
                        Log.d("MQTT", "Message arrived [$topic]: ${message.toString()}")
                        onMessage?.invoke(topic ?: "", message.toString())
                    }

                    override fun deliveryComplete(token: IMqttDeliveryToken?) {
                        Log.d("MQTT", "Delivery complete")
                    }
                })

                client?.connect(options)
                if (client?.isConnected == true) {
                    Log.d("MQTT", "✅ Connected to MQTT broker")
                    onConnected?.invoke()
                } else {
                    Log.e("MQTT", "❌ Failed to connect")
                }
            } catch (e: Exception) {
                Log.e("MQTT", "Error: ${e.message}", e)
            }
        }
    }

    fun subscribe(topic: String) {
        scope.launch {
            try {
                client?.subscribe(topic, 1)
                Log.d("MQTT", "Subscribed to $topic")
            } catch (e: Exception) {
                Log.e("MQTT", "Subscribe error: ${e.message}", e)
            }
        }
    }

    fun publish(topic: String, msg: String) {
        scope.launch {
            try {
                val message = MqttMessage(msg.toByteArray())
                client?.publish(topic, message)
                Log.d("MQTT", "Published '$msg' to $topic")
            } catch (e: Exception) {
                Log.e("MQTT", "Publish error: ${e.message}", e)
            }
        }
    }

    fun disconnect() {
        scope.launch {
            try {
                client?.disconnect()
                Log.d("MQTT", "Disconnected")
            } catch (e: Exception) {
                Log.e("MQTT", "Disconnect error: ${e.message}", e)
            }
        }
    }
}
