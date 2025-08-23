package com.schoolbridge.v2.ui.message

import android.os.Build
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.schoolbridge.v2.domain.messaging.Message
import com.schoolbridge.v2.domain.messaging.MessageThread
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

class MessageThreadViewModel : ViewModel() {
    private val brokerUrl: String ="ssl://broker.hivemq.com:8883"
    private val clientId = "SchoolBridge_" + System.currentTimeMillis()
    private val mqttClient by lazy {
        Log.d("MessageThreadVM", "Initializing MQTT client with broker URL: $brokerUrl")
        MqttAsyncClient(brokerUrl, clientId, null)
    }

    private val _messageThreads = MutableStateFlow<List<MessageThread>>(emptyList())
    val messageThreads: StateFlow<List<MessageThread>> = _messageThreads

    init {
        Log.d("MessageThreadVM", "ViewModel initialized")
        connectAndSubscribe()
    }

    private fun connectAndSubscribe() {
        Log.d("MessageThreadVM", "connectAndSubscribe called")
        val options = MqttConnectOptions().apply {
            isAutomaticReconnect = true
            isCleanSession = true
        }

        mqttClient.setCallback(object : MqttCallback {
            override fun messageArrived(topic: String?, message: MqttMessage?) {
                Log.d("MessageThreadVM", "Message arrived on topic: $topic")
                val payload = message?.toString() ?: return
                Log.d("MessageThreadVM", "Message payload: $payload")
                if (topic != null) {
                    val newMessage = Message(
                        sender = "unknown",
                        content = payload,
                        timestamp = System.currentTimeMillis().toString(),
                        isUnread = true
                    )
                    updateMessageThreads(topic, newMessage)
                }
            }

            override fun connectionLost(cause: Throwable?) {
                Log.e("MessageThreadVM", "Connection lost", cause)
            }

            override fun deliveryComplete(token: IMqttDeliveryToken?) {
                // Not needed for subscribers
            }
        })

        Log.d("MessageThreadVM", "Attempting to connect to broker at: $brokerUrl with client ID: $clientId")
        mqttClient.connect(options, null, object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken?) {
                Log.d("MessageThreadVM", "Connected to broker")
                if (mqttClient.isConnected) {
                    try {
                        mqttClient.subscribe("schoolbridge/#", 1, null, object : IMqttActionListener {
                            override fun onSuccess(asyncActionToken: IMqttToken?) {
                                Log.d("MessageThreadVM", "Subscribed to topic: schoolbridge/#")
                            }

                            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                                Log.e("MessageThreadVM", "Subscription failed", exception)
                            }
                        })
                    } catch (e: Exception) {
                        Log.e("MessageThreadVM", "Subscription failed due to an exception.", e)
                    }
                } else {
                    Log.d("MessageThreadVM", "onSuccess called but client is not connected.")
                }
            }

            override fun onFailure(asyncActionToken: IMqttToken?, exception: Throwable?) {
                Log.e("MessageThreadVM", "Connection failed", exception)
                if (exception != null) {
                    Log.d("MessageThreadVM", "Connection failed. Exception message: ${exception.message}")
                    Log.d("MessageThreadVM", "Connection failed. Exception cause: ${exception.cause}")
                } else {
                    Log.d("MessageThreadVM", "Connection failed with no specific exception provided.")
                }
            }
        })
    }

    private fun updateMessageThreads(topic: String, newMessage: Message) {
        viewModelScope.launch(Dispatchers.Main) {
            try {
                Log.d("MessageThreadVM", "Updating message threads for topic: $topic")
                val currentThreads = _messageThreads.value.toMutableList()
                val existingThread = currentThreads.find { it.topic == topic }
                if (existingThread != null) {
                    Log.d("MessageThreadVM", "Adding message to existing thread: $topic")
                    existingThread.messages.add(newMessage)
                } else {
                    Log.d("MessageThreadVM", "Creating new thread for topic: $topic")
                    currentThreads.add(MessageThread(topic, mutableListOf(newMessage)))
                }
                _messageThreads.value = currentThreads
                Log.d("MessageThreadVM", "Message threads updated. Total threads: ${currentThreads.size}")
            } catch (e: Exception) {
                Log.d("MessageThreadVM", "Error updating message threads: ${e.message}")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        try {
            mqttClient.disconnect()
            mqttClient.close()
            Log.d("MessageThreadVM", "MQTT client disconnected and closed")
        } catch (e: Exception) {
            Log.d("MessageThreadVM", "Error disconnecting MQTT client: ${e.message}")
        }
    }
    fun sendMessage(topic: String, content: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                if (mqttClient.isConnected) {
                    val message = MqttMessage(content.toByteArray())
                    mqttClient.publish(topic, message)
                    Log.d("MessageThreadVM", "Message published to $topic")
                } else {
                    Log.e("MessageThreadVM", "Cannot send message: MQTT client not connected")
                }
            } catch (e: Exception) {
                Log.e("MessageThreadVM", "Error publishing message: ${e.message}")
            }
        }
    }

}
