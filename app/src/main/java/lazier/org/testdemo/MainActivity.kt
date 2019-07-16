package lazier.org.testdemo

import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import lazier.org.piechart.Data

class MainActivity : AppCompatActivity(), View.OnClickListener {

    companion object {
        const val TAG = "tag"
    }

    var boolean  = false
    private val mColors = intArrayOf(-0x330100, -0x9b6a13, -0x1cd9ca, -0x800000, -0x7f8000, -0x7397, -0x7f7f80, -0x194800, -0x830400)
    private val mColors1 = intArrayOf(Color.RED, Color.BLUE, Color.YELLOW, Color.GREEN, Color.CYAN, Color.GRAY)
    private val angle = floatArrayOf(0.37f, 0.1f, 0.13f, 0.2f, 0.15f, 0.05f)
    internal var startX = 0f
    internal var startY = 0f

    var list:MutableList<Data> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_dianji.setOnClickListener(this)
        for (i in 0 until 5) {
            val data = Data()
            data.color = mColors[i]
            data.percentage = angle[i]
            list.add(data)
        }
        pieChart.setData(list)
    }


    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.tv_dianji -> {
                Toast.makeText(this, "你点击了点击按钮", Toast.LENGTH_SHORT).show()
                boolean = !boolean
                pieChart.setIsShowDate(boolean)
            }

        }
    }
}
