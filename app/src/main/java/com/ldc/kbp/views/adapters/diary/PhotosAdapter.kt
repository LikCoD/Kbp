package com.ldc.kbp.views.adapters.diary

import android.app.Activity
import android.graphics.Bitmap
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.snackbar.Snackbar
import com.ldc.kbp.models.Deprecates
import com.ldc.kbp.R
import com.ldc.kbp.views.adapters.Adapter
import kotlinx.android.synthetic.main.item_photo.view.*
import java.io.File

class PhotosAdapter(
    private val activity: Activity,
    items: List<Pair<Bitmap, File>> = listOf()
) : Adapter<Pair<Bitmap, File>>(activity, items, R.layout.item_photo) {

    override fun onBindViewHolder(view: View, item: Pair<Bitmap, File>?, position: Int) {
        val photoX = Deprecates.getScreenSize(activity).x - 150

        view.item_photo_img.layoutParams =
            ConstraintLayout.LayoutParams(photoX, item!!.first.height * photoX / item.first.width)

        view.item_photo_delete_img.setOnClickListener {
            Snackbar.make(view.item_photo_delete_img, R.string.confirm_delete, Snackbar.LENGTH_LONG)
                .setAction(R.string.confirm) {
                    items = items!!.toMutableList().apply { removeAt(position) }
                    item.second.delete()

                    if (items!!.isEmpty())
                        onAllItemDeleted()

                    notifyItemRemoved(position)

                }.show()

        }

        view.item_photo_img.setImageBitmap(item.first)
    }

    fun addPhoto(photo: Bitmap, file: File) {
        items = items!!.plus(photo to file)

        notifyItemInserted(items!!.lastIndex)
    }

    var onAllItemDeleted: () -> Unit = {  }
}