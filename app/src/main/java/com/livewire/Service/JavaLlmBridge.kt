#!/usr/bin/env kotlin

package com.livewire.Service

import android.content.Context
import com.arm.aichat.AiChat
import com.arm.aichat.InferenceEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

class JavaLlmBridge(context: Context) {

    interface TokenCallback {
        fun onToken(token: String)
        fun onComplete()
        fun onError(e: Throwable)
    }

    interface SimpleCallback {
        fun onSuccess()
        fun onError(e: Throwable)
    }

    private val engine: InferenceEngine = AiChat.getInferenceEngine(context)
    private val scope = CoroutineScope(Dispatchers.IO)

    fun loadModel(path: String, callback: SimpleCallback) {
        scope.launch {
            try {
                engine.loadModel(path)
                callback.onSuccess()
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun sendUserPrompt(message: String, predictLength: Int, callback: TokenCallback) {
        scope.launch {
            try {
                engine.sendUserPrompt(message, predictLength).collect { token ->
                    callback.onToken(token)
                }
                callback.onComplete()
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    fun cleanUp() = engine.cleanUp()
    fun destroy() = engine.destroy()
}