package ru.kabylin.andrey.vocabulator.views

import android.content.Context
import android.os.Build
import android.support.annotation.LayoutRes
import android.support.constraint.ConstraintLayout
import android.support.constraint.ConstraintSet
import android.support.design.widget.CoordinatorLayout
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import ru.kabylin.andrey.vocabulator.ext.hideView

fun inflateFullScreenLayout(context: Context, container: ViewGroup, @LayoutRes res: Int): View {
    val view = View.inflate(context, res, null)
    view.hideView()
    view.id = View.generateViewId()

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        view.z = 100.0f  // Поверх остальных элементов

    // Перехватываем все события на touch
    view.setOnTouchListener { _, _ -> true }

    /*
     Сначала добавляем вью в контейнер, и только потом начинаем ее настраивать
     относительно этого контеинера
    */
    container.addView(view)

    when (container) {
        is ConstraintLayout -> {
            view.layoutParams = ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT
            )

            val constraints = ConstraintSet()
            constraints.clone(container)
            constraints.constrainDefaultHeight(view.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
            constraints.constrainDefaultWidth(view.id, ConstraintSet.MATCH_CONSTRAINT_SPREAD)
            constraints.applyTo(container)
        }
        is RelativeLayout -> {
            view.layoutParams = RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT
            )
        }
        is CoordinatorLayout -> {
            view.layoutParams = CoordinatorLayout.LayoutParams(
                CoordinatorLayout.LayoutParams.MATCH_PARENT,
                CoordinatorLayout.LayoutParams.MATCH_PARENT
            )
        }
        else -> throw AssertionError("decoratorContainer should be ConstraintLayout, RelativeLayout or CoordinatorLayout")
    }

    return view
}
