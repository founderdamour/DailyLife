package cn.zrc.dailylife.entity;

/**
 * Created by yangzhizhong
 */

public class PlanInfo {

    private String title;

    private String needDay;

    private String day;

    public PlanInfo(String title, String needDay, String day) {
        this.title = title;
        this.needDay = needDay;
        this.day = day;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNeedDay() {
        return needDay;
    }

    public void setNeedDay(String needDay) {
        this.needDay = needDay;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }
}
