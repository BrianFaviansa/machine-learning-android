package com.dicoding.asclepius.view

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUri = intent.getParcelableExtra<Uri>(EXTRA_IMAGE_URI)
        val prediction = intent.getStringExtra(EXTRA_PREDICTION)

        binding.resultImage.setImageURI(imageUri)
        binding.resultText.text = prediction
    }

    companion object {
        const val EXTRA_IMAGE_URI = "extra_image"
        const val EXTRA_PREDICTION = "extra_prediction"
    }
}