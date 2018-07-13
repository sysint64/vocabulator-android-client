package ru.kabylin.andrey.vocabulator

import android.content.Context
import android.view.View
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_category_card.view.*
import ru.kabylin.andrey.vocabulator.services.WordsService
import ru.kabylin.andrey.vocabulator.views.RecyclerItemHolder

class CategoryCardHolder(context: Context, view: View) : RecyclerItemHolder<WordsService.Category>(context, view) {
    override fun bind(data: WordsService.Category) {
        with(view) {
            titleTextView.text = data.name

            if (data.image != null) {
                val requestOptions = RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.ic_shape)
                    .error(R.drawable.ic_shape)

                Glide
                    .with(this)
                    .load(data.image)
                    .apply(requestOptions)
                    .into(imageView)
            } else {
                imageView.setImageResource(R.drawable.ic_shape)
            }
        }
    }
}
