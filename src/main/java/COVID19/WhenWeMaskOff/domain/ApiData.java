package COVID19.WhenWeMaskOff.domain;

public class ApiData {
    private int firstCnt;
    private int secondCnt;
    private int totalFirstCnt;

    public int getFirstCnt() {
        return firstCnt;
    }

    public void setFirstCnt(int firstCnt) {
        this.firstCnt = firstCnt;
    }

    public int getSecondCnt() {
        return secondCnt;
    }

    public void setSecondCnt(int secondCnt) {
        this.secondCnt = secondCnt;
    }

    public int getTotalFirstCnt() {
        return totalFirstCnt;
    }

    public void setTotalFirstCnt(int totalFirstCnt) {
        this.totalFirstCnt = totalFirstCnt;
    }

    public int getTotalSecondCnt() {
        return totalSecondCnt;
    }

    public void setTotalSecondCnt(int totalSecondCnt) {
        this.totalSecondCnt = totalSecondCnt;
    }

    public String getSido() {
        return sido;
    }

    public void setSido(String sido) {
        this.sido = sido;
    }

    private int totalSecondCnt;
    private String sido;

}
