package com.dicoding.asclepius.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dicoding.asclepius.R
import com.dicoding.asclepius.data.Result
import com.dicoding.asclepius.data.local.entity.HistoryEntity
import com.dicoding.asclepius.databinding.ActivityMainBinding
import com.dicoding.asclepius.helper.ImageClassifierHelper
import com.dicoding.asclepius.utils.Utils.displayToast
import com.dicoding.asclepius.utils.Utils.formatPercent
import com.dicoding.asclepius.view.adapter.HistoryAdapter
import com.dicoding.asclepius.view.history.HistoryActivity
import com.dicoding.asclepius.view.news.NewsActivity
import com.yalantis.ucrop.UCrop
import org.tensorflow.lite.task.vision.classifier.Classifications
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var imageClassifierHelper: ImageClassifierHelper
    private val viewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(this)
    }

    private lateinit var historyAdapter: HistoryAdapter
    private lateinit var rvHistory: RecyclerView

    private var currentImageUri: Uri? = null

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        this, REQUIRED_PERMISSION
    ) == PackageManager.PERMISSION_GRANTED

    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted: Boolean ->
        if (granted) {
            displayToast(this@MainActivity, "Permission request granted")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarMain)

        if (!allPermissionsGranted()) {
            requestPermission.launch(REQUIRED_PERMISSION)
        }

        binding.galleryButton.setOnClickListener { startGallery() }
        binding.analyzeButton.setOnClickListener { analyzeImage() }

        imageClassifierHelper = ImageClassifierHelper(
            context = this@MainActivity,
            classifierListener = object : ImageClassifierHelper.ClassifierListener {
                override fun onError(error: String) {
                    displayToast(this@MainActivity, error)
                }

                override fun onResults(results: List<Classifications>?, inferenceTime: Long) {
                    runOnUiThread {
                        binding.progressIndicator.visibility = View.GONE
                        try {
                            results?.let {
                                val category = it[0].categories[0].label
                                val confidence = it[0].categories[0].score

                                currentImageUri?.let { uri ->
                                    this@MainActivity.contentResolver.openInputStream(uri)
                                        ?.use { inputStream ->
                                            val bitmap = BitmapFactory.decodeStream(inputStream)
                                            binding.previewImageView.setImageBitmap(bitmap)
                                        }
                                }

                                val historyData = currentImageUri?.let { uri ->
                                    this@MainActivity.contentResolver.openInputStream(uri)
                                        ?.use { inputStream ->
                                            val byteArray = inputStream.readBytes()
                                            HistoryEntity(
                                                category = category,
                                                confidenceScore = confidence,
                                                imageUri = byteArray,
                                                timestamp = System.currentTimeMillis().toString()
                                            )
                                        }
                                }
                                if (historyData != null) {
                                    viewModel.insertHistory(historyData).observe(this@MainActivity) { results ->
                                        if (results != null) {
                                            when (results) {
                                                is Result.Loading -> {
                                                    binding.progressIndicator.visibility = View.VISIBLE
                                                }
                                                is  Result.Success -> {
                                                    binding.progressIndicator.visibility = View.GONE
                                                    displayToast(this@MainActivity, getString(R.string.history_saved))
                                                }
                                                is  Result.Error -> {
                                                    binding.progressIndicator.visibility = View.GONE
                                                    displayToast(this@MainActivity, results.error)
                                                }
                                            }
                                        }
                                    }
                                }

                                val accuracy = formatPercent(confidence)
                                goToResult("$category with accuracy : $accuracy")
                            }
                        } catch (e: Exception) {
                            onError(e.message.toString())
                        }
                    }
                }
            }
        )

        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.actionNews -> {
                val intent = Intent(this, NewsActivity::class.java)
                startActivity(intent)
                true
            }

            R.id.actionHistory -> {
                val intent = Intent(this, HistoryActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startGallery() {
        binding.previewImageView.setImageBitmap(null)
        currentImageUri = null
        if (!allPermissionsGranted()) {
            requestPermission.launch(REQUIRED_PERMISSION)
        }
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private fun analyzeImage() {
        if (currentImageUri != null) {
            binding.progressIndicator.visibility = View.VISIBLE
            imageClassifierHelper.classifyStaticImage(currentImageUri!!)
        } else {
            displayToast(this@MainActivity, getString(R.string.empty_image_warning))
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            UCrop.of(uri, Uri.fromFile(File(cacheDir, "temp_image.jpg"))).withAspectRatio(1f, 1f)
                .withMaxResultSize(512, 512).start(this)
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }


    @Deprecated("This method has been deprecated in favor of using the Activity Result API\n      which brings increased type safety via an {@link ActivityResultContract} and the prebuilt\n      contracts for common intents available in\n      {@link androidx.activity.result.contract.ActivityResultContracts}, provides hooks for\n      testing, and allow receiving results in separate, testable classes independent from your\n      activity. Use\n      {@link #registerForActivityResult(ActivityResultContract, ActivityResultCallback)}\n      with the appropriate {@link ActivityResultContract} and handling the result in the\n      {@link ActivityResultCallback#onActivityResult(Object) callback}.")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == UCrop.REQUEST_CROP) {
            val resultUri: Uri? = UCrop.getOutput(data!!)
            currentImageUri = resultUri
            showImage()
        } else if (resultCode == UCrop.RESULT_ERROR) {
            val cropError: Throwable? = UCrop.getError(data!!)
            Log.e("Crop Error", "onActivityResult: ", cropError)
        }
    }

    private fun goToResult(prediction: String) {
        val intent = Intent(this, ResultActivity::class.java)
        intent.putExtra(ResultActivity.EXTRA_IMAGE_URI, currentImageUri.toString())
        intent.putExtra(ResultActivity.EXTRA_PREDICTION, prediction)
        startActivity(intent)
    }

    private fun setupRecyclerView() {
        rvHistory = binding.rvHistory
        rvHistory.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        historyAdapter = HistoryAdapter(viewModel)
        rvHistory.setHasFixedSize(true)
        rvHistory.adapter = historyAdapter

        viewModel.getHistories().observe(this) { histories ->
            if (histories != null) {
                when (histories) {
                    Result.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }
                    is Result.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        historyAdapter.setHistoryList(histories.data)
                    }
                    is Result.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        displayToast(this, histories.error)
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUIRED_PERMISSION = Manifest.permission.CAMERA
    }
}