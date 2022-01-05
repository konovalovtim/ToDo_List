package study.my.todo_list

import android.view.View

interface OnItemClick {

    fun onItemClick(v: View, position: Int)
}