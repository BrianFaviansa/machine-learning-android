package com.example.textclassification

import android.content.Context
import com.google.mediapipe.tasks.components.containers.Classifications

class TextClassifierHelper(
    val modelName: String = "bert_classifier.tflite",
    val context: Context,
    var classifierListener: ClassifierListener? = null
) {



    interface ClassifierListener {
        fun onError(error: String)
        fun onResults(
            results: List<Classifications>?,
            inferenceTime: Long
        )
    }

    companion object {
        private const val TAG = "TextClassifierHelper"
    }
}