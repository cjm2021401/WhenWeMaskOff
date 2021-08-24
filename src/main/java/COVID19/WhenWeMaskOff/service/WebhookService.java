package COVID19.WhenWeMaskOff.service;

import COVID19.WhenWeMaskOff.domain.ApiData;
import COVID19.WhenWeMaskOff.domain.Member;
import COVID19.WhenWeMaskOff.repository.MemberRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class WebhookService {
    private final MemberRepository memberRepository;
    @Autowired
    public WebhookService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }


    /**
     * call openAPI about COVID-19
     * @return  json data
     */
    public String callAPi()  {
        StringBuilder sb= new StringBuilder();
        try{
            URL url =new URL(ApiUrl());
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            BufferedReader br=new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream(), "UTF-8"));
            String result;
            while((result=br.readLine())!=null){
                sb.append(result+"\n\r");
            }
            httpURLConnection.disconnect();
            return sb.toString();
        }catch (IOException e){
            System.out.println("open api 호출 과정에서 오류가 발생했습니다"+e);
            return "";
        }


    }

    /**
     * get api url 획득
     * @return url
     * @throws IOException url encode 실패
     */
    public String ApiUrl() throws IOException{
        Calendar cal = Calendar.getInstance();
        String year_s = Integer.toString(cal.get(Calendar.YEAR));
        int month = cal.get(Calendar.MONTH) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int hour = cal.get(Calendar.HOUR_OF_DAY);
        if(hour<=9) day=day-1;
        String month_s=Integer.toString(month);
        String day_s=Integer.toString(day);
        if(month<10) month_s="0"+month_s;
        if(day<10) day_s="0"+day_s;
        String date = year_s+"-"+month_s+"-"+day_s+" 00:00:00";
        String urlStr= "https://api.odcloud.kr/api/15077756/v1/vaccine-stat?page=1&perPage=18&serviceKey=rBH85Md4sFnndvteH0uGX1Npjy%2FtBTo1mir%2Ft6yCLlgGglg4JHoHlKU5gMVkusgD3naMV2WkVIu%2Bo%2F7DwG3hHg%3D%3D";
        urlStr+="&"+URLEncoder.encode("cond[baseDate::EQ]","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");
        return urlStr;
    }

    /**
     * get data from json
     * @return ApiData(first, Second Count) + (first , second Total Count) + region
     */
    public ApiData[] getApiData() {
        String result=callAPi();
        ApiData[] dataArray=new ApiData[18];
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject=(JSONObject) jsonParser.parse(result);
            JSONArray apiData = (JSONArray) jsonObject.get("data");
            for(int i=0; i<18; i++){
                JSONObject jobj = (JSONObject) apiData.get(i);
                dataArray[i]=new ApiData();
                dataArray[i].setFirstCnt(Integer.parseInt((jobj.get("firstCnt")).toString()));
                dataArray[i].setSecondCnt(Integer.parseInt((jobj.get("secondCnt")).toString()));
                dataArray[i].setTotalFirstCnt(Integer.parseInt((jobj.get("totalFirstCnt")).toString()));
                dataArray[i].setTotalSecondCnt(Integer.parseInt((jobj.get("totalSecondCnt")).toString()));
                dataArray[i].setSido((jobj.get("sido")).toString());
            }
            return dataArray;
        }catch (ParseException p){
            System.out.println("json으로 변경하는 과정에서 오류가생겼습니다"+p);
            return dataArray;
        }

    }

    /**
     * calculate data to send
     * @return data to send
     */
    public ArrayList<SendDataFormat> calPercent(){
        ApiData[] apiDatas=getApiData();
        ArrayList<SendDataFormat> sendDataFormats =new ArrayList<>();
        for(int i=0; i<apiDatas.length; i++){
            int total=getTotal(apiDatas[i].getSido());
            int percent_1 = (int)(((double)apiDatas[i].getTotalFirstCnt()/(double)total)*100);
            int percent_2 = (int)(((double)apiDatas[i].getTotalSecondCnt()/(double)total)*100);
            int restVaccine =(int)((double)total*0.7)-apiDatas[i].getTotalSecondCnt();
            int restDay = restVaccine/apiDatas[i].getSecondCnt();
            if(percent_1>10||percent_2>69||restDay<100){
                SendDataFormat sendDataFormat =new SendDataFormat(percent_1,percent_2,restDay,restVaccine,apiDatas[i].getSido(),apiDatas[i].getSecondCnt());
                sendDataFormats.add(sendDataFormat);
            }
        }


        return sendDataFormats;
    }

    /**
     * get Population
     * @param sido
     * @return Population
     */
    public int getTotal(String sido){
        if(sido.equals("경기도")) return 13410000;
        else if(sido.equals("서울특별시")) return 9776000;
        else if(sido.equals("부산광역시")) return 3429000;
        else if(sido.equals("대구광역시")) return 2465000;
        else if(sido.equals("광주광역시 ")) return 1500000;
        else if(sido.equals("인천광역시")) return 2923000;
        else if(sido.equals("대전광역시")) return 1531000;
        else if(sido.equals("울산광역시")) return 1166000;
        else if(sido.equals("세종특별자치시")) return 275600;
        else if(sido.equals("강원도")) return 1565000;
        else if(sido.equals("충청북도")) return 1579000;
        else if(sido.equals("충청남도")) return 2060000;
        else if(sido.equals("전라북도")) return 1870000;
        else if(sido.equals("전라남도")) return 1902000;
        else if(sido.equals("경상북도")) return 2700000;
        else if(sido.equals("경상남도")) return 3448000;
        else if(sido.equals("제주특별자치도")) return 695500;
        else if(sido.equals("전국")) return 51710000;
        else return 0;
    }

    public void sendMessage(){
        ArrayList<SendDataFormat> sendDataFormats = calPercent();
        if(sendDataFormats.size()==0) return ;
        for(int i=0; i<sendDataFormats.size(); i++){
            SendDataFormat sendDataFormat=sendDataFormats.get(i);
            RestTemplate restTemplate = new RestTemplate();
            HashMap<String, Object> request = new HashMap<>();
            request.put("username", "MaskOff");
            request.put("text", makeMessage(sendDataFormat));
            HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request);
            List<Member> memberList=memberRepository.findAll();
            String url= "https://hooks.slack.com/services/";
            for(int j=0; j<memberList.size(); j++){
                if((memberList.get(j)).getRegion().equals(sendDataFormat.getRegion())){
                    restTemplate.exchange(url+(memberList.get(j)).getWebhook(), HttpMethod.POST, httpEntity, String.class);
                }
            }
        }
        return ;
    }


    public String makeMessage(SendDataFormat sendDataFormat){
        String result = sendDataFormat.getRegion()+"지역\n"+
                "1차 접종률 :"+Integer.toString(sendDataFormat.getPercent_1())+"%\n"+
                "2차 접종률 :"+Integer.toString(sendDataFormat.getPercent_2())+"%\n"+
                "마스크를 벗기에 필요한 2차 접종자 수 : "+Integer.toString(sendDataFormat.getRestVaccine())+"명\n"+
                "전일 2차 접종자 수 : "+Integer.toString(sendDataFormat.getSecondCnt())+"명\n"+
                "마스크를 벗기 까지 필요한 날(전일 접종자 기반) : "+Integer.toString(sendDataFormat.getRestDay());
        return result;
    }

}

class SendDataFormat{
    private int percent_1;
    private int percent_2;
    private int restDay;
    private int restVaccine;
    private String region;
    private int secondCnt;

    public int getSecondCnt() {
        return secondCnt;
    }

    public void setSecondCnt(int secondCnt) {
        this.secondCnt = secondCnt;
    }

    public SendDataFormat(int percent_1, int percent_2, int restDay, int restVaccine, String region, int secondCnt) {
        this.percent_1 = percent_1;
        this.percent_2 = percent_2;
        this.restDay = restDay;
        this.restVaccine = restVaccine;
        this.region = region;
        this.secondCnt=secondCnt;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public int getPercent_1() {
        return percent_1;
    }

    public void setPercent_1(int percent_1) {
        this.percent_1 = percent_1;
    }

    public int getPercent_2() {
        return percent_2;
    }

    public void setPercent_2(int percent_2) {
        this.percent_2 = percent_2;
    }

    public int getRestDay() {
        return restDay;
    }

    public void setRestDay(int restDay) {
        this.restDay = restDay;
    }

    public int getRestVaccine() {
        return restVaccine;
    }

    public void setRestVaccine(int restVaccine) {
        this.restVaccine = restVaccine;
    }
}