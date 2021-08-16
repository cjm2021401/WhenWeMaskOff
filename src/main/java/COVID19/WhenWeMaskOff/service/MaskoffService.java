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
public class MaskoffService {
    private final MemberRepository memberRepository;
    @Autowired
    public MaskoffService(MemberRepository memberRepository){
        this.memberRepository=memberRepository;
    }

    public String callAPi(String region)  {
        StringBuilder sb= new StringBuilder();
         try{
        URL url =new URL(getNow(region));
        System.out.println(url);
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
    public String getNow(String region) throws IOException{
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
        urlStr+= "&"+URLEncoder.encode("cond[sido::EQ]","UTF-8")+"="+URLEncoder.encode(region,"UTF-8");
        urlStr+="&"+URLEncoder.encode("cond[baseDate::EQ]","UTF-8")+"="+URLEncoder.encode(date,"UTF-8");
        return urlStr;
    }
    public ApiData getApiData(String id) {
        Member member=memberRepository.findById(id).get();
        String result=callAPi(member.getRegion());
        ApiData dataArray=new ApiData();
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject=(JSONObject) jsonParser.parse(result);
            JSONArray apiData = (JSONArray) jsonObject.get("data");
            JSONObject jobj = (JSONObject) apiData.get(0);
            dataArray.setFirstCnt(Integer.parseInt((jobj.get("firstCnt")).toString()));
            dataArray.setSecondCnt(Integer.parseInt((jobj.get("secondCnt")).toString()));
            dataArray.setTotalFirstCnt(Integer.parseInt((jobj.get("totalFirstCnt")).toString()));
            dataArray.setTotalSecondCnt(Integer.parseInt((jobj.get("totalSecondCnt")).toString()));
            dataArray.setSido((jobj.get("sido")).toString());
            return dataArray;
        }catch (ParseException p){
            System.out.println("json으로 변경하는 과정에서 오류가생겼습니다"+p);
            return dataArray;
        }

    }

}
