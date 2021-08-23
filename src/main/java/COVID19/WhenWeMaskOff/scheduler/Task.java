package COVID19.WhenWeMaskOff.scheduler;

import COVID19.WhenWeMaskOff.service.MaskoffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class Task {

    MaskoffService maskoffService;
    @Autowired
    public Task(MaskoffService maskoffService){
        this.maskoffService=maskoffService;
    }

    @Scheduled(cron="*/60 * * * * *")
    public void alram(){
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> request = new HashMap<>();
        request.put("username", "MaskOff");
        request.put("text", "custom-slack-msg");
        HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(request);
        String url= "https://hooks.slack.com/services/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";
        restTemplate.exchange(url, HttpMethod.POST, httpEntity, String.class);
    }
}
