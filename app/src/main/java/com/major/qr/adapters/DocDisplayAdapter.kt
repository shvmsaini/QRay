package com.major.qr.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.major.qr.R
import com.major.qr.databinding.FragmentDocumentBinding
import com.major.qr.dialog.DocumentDetailDialog
import com.major.qr.models.Doc
import com.major.qr.viewmodels.DocumentViewModel

class DocDisplayAdapter(
    var context: Context, var list: ArrayList<Doc>, var documentViewModel: DocumentViewModel,
    var binding: FragmentDocumentBinding
) : RecyclerView.Adapter<DocDisplayAdapter.ItemViewHolder>() {
    val TAG = DocDisplayAdapter::class.java.simpleName
    @JvmField
    var selectedDocs: HashSet<String?>

    init {
        selectedDocs = HashSet()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_doc, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val doc = list[position]
        if (doc.documentType == "pdf") holder.imageView.setImageDrawable(
            AppCompatResources.getDrawable(context, R.drawable.pdf_placeholder)
        ) else documentViewModel.getDocLink(doc.documentReference!!).observe(
            (context as LifecycleOwner)
        ) { link: String ->
            doc.docLink = link.substring(1, link.length - 1)
            Glide.with(context)
                .load(doc.docLink)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.placeholder_doc)
                .into(holder.imageView)
        }
        holder.itemView.setOnLongClickListener { view: View? ->
            if (selectedDocs.contains(list[position].documentId)) {
                unSelect(holder, position)
                return@setOnLongClickListener false
            }
            select(holder, position)
            true
        }
        holder.itemView.setOnClickListener { view: View? ->
            if (selectedDocs.size == 0) {
                val dialog = DocumentDetailDialog(
                    documentViewModel, doc,
                    this, position
                )
                dialog.show((context as FragmentActivity).supportFragmentManager, "Doc Dialog")
                return@setOnClickListener
            }
            if (selectedDocs.contains(list[position].documentId)) unSelect(
                holder,
                position
            ) else select(holder, position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun select(holder: ItemViewHolder, position: Int) {
//        holder.itemView.setBackgroundColor(ContextCompat.getColor(context, R.color.teal_200));
        holder.itemView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.teal_200)
        )
        selectedDocs.add(list[position].documentId)
        binding.clearAll.visibility = View.VISIBLE
        Handler(Looper.getMainLooper()).postDelayed(
            { holder.itemView.setBackgroundResource(R.drawable.attendance_background) },
            400
        )
    }

    fun unSelect(holder: ItemViewHolder, position: Int) {
        holder.itemView.backgroundTintList = ColorStateList.valueOf(
            ContextCompat.getColor(context, R.color.primary_blue)
        )
        selectedDocs.remove(list[position].documentId)
        if (selectedDocs.size == 0) binding.clearAll.visibility = View.GONE
        Handler(Looper.getMainLooper()).postDelayed(
            { holder.itemView.setBackgroundResource(R.drawable.document_background) },
            400
        )
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView

        init {
            imageView = itemView.findViewById(R.id.imageView)
        }
    }
}