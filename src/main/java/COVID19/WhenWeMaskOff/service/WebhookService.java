package COVID19.WhenWeMaskOff.service;

import COVID19.WhenWeMaskOff.domain.ApiData;
import COVID19.WhenWeMaskOff.domain.Member;
import COVID19.WhenWeMaskOff.repository.MemberRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

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

    
}
