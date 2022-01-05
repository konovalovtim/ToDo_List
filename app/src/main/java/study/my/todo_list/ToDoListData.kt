package study.my.todo_list



class ToDoListData(
    val title: String= "",
    val date: String = "",
    val time: String = "",
    val priority: String = "",
    var indexDb: Long = 0,
    val isShow: Int = 0,
)