# PieChart
## 饼状图  先看效果
![image](https://github.com/chokTang/PieChart/blob/master/app/src/main/res/raw/git.gif)

### 用法
    >   allprojects {
 		    repositories {
 			    ...
 			    maven { url 'https://jitpack.io' }
 		    }
 	    }

 	>   dependencies {
        	        implementation 'com.github.chokTang:PieChart:v1.0.0'
        	}

    >>       <lazier.org.piechart.PieChartView
                        android:id="@+id/pieChart"
                        android:layout_width="wrap_content"
                        android:layout_height="wrap_content"
                        app:circleWidth="5dp"
                        app:height="300dp"
                        app:width="300dp"
                        app:mStartAngle="0"
                        app:circleBackgroundColor="@color/colorAccent"
                        app:isShowData="false"
                        app:textSize="12sp"
                        />

        >>         设置数据源Data实体的集合 自己设置
                        for (i in 0 until 5) {
                            val data = Data()
                            data.color = mColors[i]
                            data.percentage = angle[i]
                            list.add(data)
                        }
                        pieChart.setData(list)
                         //控制是否显示数据只是
                         pieChart.setIsShowDate(boolean)
   ####  属性
   >>   height 饼状图的高度
        width 饼状图的宽度
        mStartAngle 饼状图的起始角度
        circleBackgroundColor 饼状图的背景颜色
        isShowData 饼状图是否显示 数据指引
        textSize 数据指引字体大小
