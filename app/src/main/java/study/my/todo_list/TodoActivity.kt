package study.my.todo_list

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.item_tolist.*
import org.jetbrains.anko.alert
import study.my.todo_list.databinding.ActivityMainBinding
import java.text.SimpleDateFormat
import java.util.*

class TodoActivity : AppCompatActivity(), OnItemClick, AdapterView.OnItemSelectedListener {


    private val list = mutableListOf<ToDoListData>()

    private val c = Calendar.getInstance()

    private val month: Int = c.get(Calendar.MONTH)
    private val year: Int = c.get(Calendar.YEAR)
    private val day: Int = c.get(Calendar.DAY_OF_MONTH)

    private var cal = Calendar.getInstance()

    private val listAdapter = ListAdapter(list, this)

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: ToDoListViewModel

//старт активити
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProviders.of(this).get(ToDoListViewModel::class.java)

        rvTodoList.layoutManager = LinearLayoutManager(this)
        rvTodoList.adapter = listAdapter
        binding.viewModel = viewModel

        val spinner: Spinner = findViewById(R.id.priorities_spinner)
        spinner.onItemSelectedListener = this
        ArrayAdapter.createFromResource(
            this,
            R.array.priorities,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter

        }

        viewModel.getPreviousList()

        viewModel.toDoList.observe(this, androidx.lifecycle.Observer {
            if (it == null)
                return@Observer

            list.clear()
            val tempList = mutableListOf<ToDoListData>()
            it.forEach {
                tempList.add(
                    ToDoListData(
                        title = it.title,
                        date = it.date,
                        time = it.time,
                        priority = it.priority,
                        indexDb = it.id,
                        isShow = it.isShow
                    )
                )

            }
//            сортировка приоритета
            list.sortedWith(compareBy<ToDoListData> { it.date }.thenBy { it.priority })
            list.addAll(tempList)
            listAdapter.notifyDataSetChanged()
            viewModel.position = -1

            viewModel.toDoList.value = null
        })

        viewModel.toDoListData.observe(this, {
            if (viewModel.position != -1) {
                list[viewModel.position] = it
                listAdapter.notifyItemChanged(viewModel.position)
            } else {
                list.add(it)
                listAdapter.notifyDataSetChanged()
            }
            viewModel.position = -1
        })

        etdate.setOnClickListener {

            val dpd = DatePickerDialog(this, { view, year, monthOfYear, dayOfMonth ->
//                Отображение выбранной даты в текстовом поле
                etdate.setText("" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year)
                viewModel.month = monthOfYear
                viewModel.year = year
                viewModel.day = dayOfMonth
            }, year, month, day)
            dpd.datePicker.minDate = System.currentTimeMillis() - 1000
            dpd.show()
        }

        etTime.setOnClickListener {
            val cal = Calendar.getInstance()
            val timeSetListener = TimePickerDialog.OnTimeSetListener { timePicker, hour, minute ->
                cal.set(Calendar.HOUR_OF_DAY, hour)
                cal.set(Calendar.MINUTE, minute)
                this.cal.set(Calendar.HOUR_OF_DAY, hour)
                this.cal.set(Calendar.MINUTE, minute)

                viewModel.hour = hour
                viewModel.minute = minute

                etTime.setText(SimpleDateFormat("HH:mm").format(cal.time))
            }

            this.cal = cal
            TimePickerDialog(
                this,
                timeSetListener,
                cal.get(Calendar.HOUR_OF_DAY),
                cal.get(Calendar.MINUTE),
                true
            ).show()
        }
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (parent != null) {
//                viewModel.priority = ObservableField<Priority>(Priority.valueOf(parent.selectedItem.toString()))
            viewModel.priority = ObservableField<String>(parent.selectedItem.toString())
            }

    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return
    }

    override fun onItemClick(v: View, position: Int) {

        alert {
            message = list[position].title
            positiveButton("Edit") {
                viewModel.title.set(list[position].title)
                viewModel.date.set(list[position].date)
                viewModel.time.set(list[position].time)
                viewModel.priority.set(list[position].priority)
                viewModel.position = position
                viewModel.index = list[position].indexDb
                editText.isFocusable = true
            }
            negativeButton("Delete") {
                viewModel.delete(list[position].indexDb)
            }

        }.show()
    }

}