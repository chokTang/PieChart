package lazier.org.piechart;

/**
 * Created by :TYK
 * Date: 2019/7/15  13:52
 * Desc: 占比数据
 */
public class Data {
    public int color;
    public float percentage;
    public String des;

    public String getDes() {
        return des;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public float getPercentage() {
        return percentage;
    }

    public void setPercentage(float percentage) {
        this.percentage = percentage;
    }
}
