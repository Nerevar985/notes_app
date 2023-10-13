package com.kanush_productions.shoppinglist.utils

import android.view.MotionEvent
import android.view.View
import com.kanush_productions.shoppinglist.fragments.NoteFragment

class MyTouchListener : View.OnTouchListener {
    var xDelta = 0.0f
    var yDelta = 0.0f

    override fun onTouch(v: View, event: MotionEvent?): Boolean {
        val clickDragTolerance = 10
        when(event?.action){
            MotionEvent.ACTION_DOWN -> {
                xDelta = v.x - event.rawX
                yDelta = v.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                v.x = xDelta + event.rawX
                v.y = yDelta + event.rawY

                v.x = v.x.coerceIn(0f, (v.parent as View).width - v.width.toFloat())
                v.y = v.y.coerceIn(0f, (v.parent as View).height - v.height.toFloat())

                v.animate().x(v.x).y(v.y).setDuration(0).start()
            }
        }
        return true
    }
}