package com.major.qr.dialog

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.DialogFragment
import com.major.qr.R
import com.major.qr.databinding.DialogUploadBinding
import com.major.qr.dialog.UploadDialog
import com.major.qr.services.UpdateService
import com.major.qr.ui.DocumentFragment
import com.major.qr.ui.LoginActivity
import com.major.qr.utils.FileUtils
import com.major.qr.viewmodels.DocumentViewModel
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class UploadDialog : DialogFragment {
    val TAG = UploadDialog::class.java.simpleName
    private val FILE_PICK_REQUEST = 200
    private val REQUEST_CAPTURE_IMAGE = 201
    private val documentViewModel: DocumentViewModel
    var uri: Uri? = null
    var imageFilePath: String? = null
    private lateinit var binding: DialogUploadBinding
    private var documentReference: String? = null

    constructor(documentReference: String?, documentViewModel: DocumentViewModel) {
        this.documentViewModel = documentViewModel
        this.documentReference = documentReference
    }

    constructor(documentViewModel: DocumentViewModel) {
        this.documentViewModel = documentViewModel
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        binding = DialogUploadBinding.inflate(layoutInflater)
        builder.setView(binding.root)

        if (documentReference != null)
            binding.docReference.text = documentReference
        else
            binding.docReference.visibility = View.GONE

        binding.fileSelect.setOnClickListener { view: View? ->
            val supportedMimeTypes = arrayOf("application/pdf", "application/msword", "image/*")
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.type = "*/*"
            intent.putExtra(Intent.EXTRA_MIME_TYPES, supportedMimeTypes)
            try {
                startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_PICK_REQUEST
                )
            } catch (ex: ActivityNotFoundException) {
                // Potentially direct the user to the Market with a Dialog
                Toast.makeText(context, "Please install a File Manager.", Toast.LENGTH_SHORT).show()
            }
        }
        binding.upload.setOnClickListener { view: View? ->
            ActivityCompat.requestPermissions(
                requireActivity(), arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE
                ), 1
            )
            // If you have access to the external storage, do whatever you need
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // If you don't have access, launch a new activity to show the user the system's dialog
                    // to allow access to the external storage
                } else {
                    val intent = Intent()
                    intent.action = Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION
                    val uri = Uri.fromParts("package", requireActivity().packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            }

            val path = binding.fileSelect.text.toString()
            if (path.isNotEmpty() || path != resources.getString(R.string.file_name)) {
                val strings = FileUtils.getPath(requireContext(), uri!!)?.split("/".toRegex())
                    ?.dropLastWhile { it.isEmpty() }?.toTypedArray()
                val stringBuilder = StringBuilder()
                if (strings != null) {
                    for (i in 0 until strings.size - 1) stringBuilder.append("/").append(strings[i])
                }
                val file = File(stringBuilder.toString(), strings?.get(strings.size - 1) ?: "")
                Log.d(TAG, "onCreate: " + file.absolutePath)
                if (documentReference != null) {
                    Log.d(TAG, "documentReference = $documentReference")
                    val retrofit = Retrofit.Builder()
                        .baseUrl(LoginActivity.URL + "/")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
                    val updateService = retrofit.create(UpdateService::class.java)
                    //                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("document",
//                            file.getName(), RequestBody.create(MediaType.parse(
//                                    requireActivity().getContentResolver().getType(Uri.fromFile(file))), file));
                    val filePart = MultipartBody.Part.createFormData(
                        "document",
                        file.name, RequestBody.create(MediaType.parse("image/png"), file)
                    )
                    val body = documentReference?.let {
                        MultipartBody.Builder()
                            .addFormDataPart("documentReference", it).build()
                    }
                    val body1 = RequestBody.create(
                        MediaType.parse("multipart/form-data"),
                        documentReference
                    )
                    val jsonObjectCall =
                        updateService.updateDoc(LoginActivity.ACCESS_TOKEN, filePart, body1)
                    jsonObjectCall.enqueue(object : Callback<String?> {
                        override fun onResponse(call: Call<String?>, response: Response<String?>) {
                            Log.d(TAG, "call = $response")
                        }

                        override fun onFailure(call: Call<String?>, t: Throwable) {
                            Log.e(TAG, "onFailure: $call")
                        }
                    })

//                    documentViewModel.updateDoc(documentReference, file).observe(this, s -> {
//                        Toast.makeText(getContext(), "Successfully updated!", Toast.LENGTH_SHORT).show();
//                        dismiss();
//                    });
                } else documentViewModel.uploadDoc(file).observe(this) { s: String? ->
                    Toast.makeText(context, "Successfully uploaded!", Toast.LENGTH_SHORT).show()
                    val tr = requireActivity().supportFragmentManager.beginTransaction()
                    tr.replace(R.id.fragment, DocumentFragment())
                    tr.commit()
                    dismiss()
                }
            } else Toast.makeText(context, "Select a file first!", Toast.LENGTH_SHORT).show()
        }
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == FILE_PICK_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                // Get the Uri of the selected file
                uri = data!!.data
                assert(uri != null)
                Log.d(TAG, "File Uri: $uri")
                val path: String?
                val projection = arrayOf(MediaStore.Files.FileColumns.DATA)
                val cursor = requireActivity().contentResolver.query(
                    uri!!, projection, null, null, null
                )
                if (cursor == null) {
                    path = uri!!.path
                } else {
                    cursor.moveToFirst()
                    val column_index = cursor.getColumnIndexOrThrow(projection[0])
                    path = cursor.getString(column_index)
                    cursor.close()
                }
                if (path.isNullOrEmpty()) {
                    Log.d("uri.getPath() = ", uri!!.path!!)
                    binding.fileSelect.text = uri!!.path
                } else {
                    Log.d("path = ", path)
                    binding.fileSelect.text = path
                }
            }
        }
        if (requestCode == REQUEST_CAPTURE_IMAGE) {
            Log.d(requestCode.toString() + "", resultCode.toString() + "")
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    context, "Captured! Click on Upload to upload the image.",
                    Toast.LENGTH_SHORT
                ).show()
                binding.fileSelect.text = imageFilePath
                uri = Uri.fromFile(File(imageFilePath))
                Log.d(TAG, "onActivityResult: $uri")
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}