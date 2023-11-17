package com.major.qr.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LifecycleOwner
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.major.qr.R
import com.major.qr.adapters.DocDisplayAdapter
import com.major.qr.databinding.DialogDocumentDetailsBinding
import com.major.qr.models.Doc
import com.major.qr.viewmodels.DocumentViewModel

class DocumentDetailDialog(
    var viewModel: DocumentViewModel,
    var doc: Doc,
    var adapter: DocDisplayAdapter,
    var position: Int
) : DialogFragment() {
    var binding: DialogDocumentDetailsBinding? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        binding = DialogDocumentDetailsBinding.inflate(layoutInflater)
        builder.setView(binding!!.root)
        binding!!.docType.text = doc.documentType
        binding!!.docId.text = doc.documentId
        binding!!.docRef.text = doc.documentReference
        if (doc.documentType == "pdf") binding!!.imageView.setImageDrawable(
            AppCompatResources.getDrawable(requireContext(), R.drawable.pdf_placeholder)
        ) else Glide.with(requireContext())
            .load(doc.docLink)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(R.drawable.placeholder_doc)
            .into(binding!!.imageView)
        binding!!.updateButton.setOnClickListener { view: View? ->
            val uploadDialog = UploadDialog(doc.documentReference, viewModel)
            uploadDialog.show(requireActivity().supportFragmentManager, "Your Dialog")
        }
        binding!!.downloadButton.setOnClickListener { view: View? ->
            viewModel.getDocLink(
                doc.documentReference!!
            )
                .observe((requireContext() as LifecycleOwner)) { s: String? ->
//                    Intent i = new Intent(context, WebViewActivity.class);
//                    i.putExtra("URL", URL);
//                    context.startActivity(i);
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(doc.docLink))
                    requireContext().startActivity(browserIntent)
                }
        }
        binding!!.deleteButton.setOnClickListener { view: View? ->
            val dialogClickListener =
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    when (which) {
                        DialogInterface.BUTTON_POSITIVE -> viewModel.deleteDoc(
                            doc.documentId!!, doc.documentReference!!
                        )
                            .observe((requireContext() as LifecycleOwner)) { s: Any? ->
                                Toast.makeText(
                                    requireContext(), "Deleted successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                adapter.list.removeAt(position)
                                adapter.notifyItemRemoved(position)
                                dismiss()
                            }

                        DialogInterface.BUTTON_NEGATIVE -> {}
                    }
                }
            AlertDialog.Builder(requireContext()).setMessage("Are you sure?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show()
        }
        val dialog: Dialog = builder.create()
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return dialog
    }
}