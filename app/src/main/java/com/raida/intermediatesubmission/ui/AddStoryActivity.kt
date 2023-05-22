package com.raida.intermediatesubmission.ui

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.raida.intermediatesubmission.*
import com.raida.intermediatesubmission.databinding.ActivityAddStoryBinding
import com.raida.intermediatesubmission.helper.*
import com.raida.intermediatesubmission.preference.UserPreference
import com.raida.intermediatesubmission.viewmodel.AddStoryViewModel
import com.raida.intermediatesubmission.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStoryBinding
    private var getFile: File? = null
    private lateinit var addStoryViewModel: AddStoryViewModel
    private lateinit var currentPhotoPath: String


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.permission_text),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = UserPreference.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this@AddStoryActivity,
            ViewModelFactory(pref))[AddStoryViewModel::class.java]

        if (!allPermissionsGranted()) {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        binding.apply {
            btnCamera.setOnClickListener { startTakePhoto() }
            btnGallery.setOnClickListener { startGallery() }
            btnUpload.setOnClickListener { uploadStory() }
        }

        supportActionBar?.title = getString(R.string.add_story_title)

    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, "Choose a Picture")
        launcherIntentGallery.launch(chooser)
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.raida.intermediatesubmission",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }
    }


    private fun uploadStory() {
        if (getFile != null) {
            val file = reduceFileImage(getFile as File)

            val description = binding.descriptionEditText.text.toString()
                .toRequestBody("text/plain".toMediaType())
            val requestImageFile = file.asRequestBody("image/jpeg".toMediaTypeOrNull())
            val imageMultiPart: MultipartBody.Part = MultipartBody.Part.createFormData(
                "photo",
                file.name,
                requestImageFile
            )

            addStoryViewModel.apply {
                getUser().observe(this@AddStoryActivity) { preference ->
                    val token = getString(R.string.bearer) + preference.token
                    addStory(token, imageMultiPart, description)
                }
                status.observe(this@AddStoryActivity) { error ->
                    if (!error) {
                        val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                        Toast.makeText(this@AddStoryActivity,
                            getString(R.string.add_success_text),
                            Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@AddStoryActivity,
                            getString(R.string.add_failure_text),
                            Toast.LENGTH_SHORT).show()
                    }
                }
                isLoading.observe(this@AddStoryActivity) {
                    showLoading(it)
                }
            }

        } else {
            Toast.makeText(this@AddStoryActivity,
                getString(R.string.input_image_text),
                Toast.LENGTH_SHORT).show()
        }
    }

    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg: Uri = result.data?.data as Uri
            val myFile = uriToFile(selectedImg, this@AddStoryActivity)

            getFile = myFile
            binding.previewImage.setImageURI(selectedImg)
        }
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                getFile = file
                binding.previewImage.setImageBitmap(BitmapFactory.decodeFile(file.path))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

}