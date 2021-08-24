package COVID19.WhenWeMaskOff.scheduler;

import COVID19.WhenWeMaskOff.service.MaskoffService;
import COVID19.WhenWeMaskOff.service.WebhookService;
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

    WebhookService webhookService;
    @Autowired
    public Task(WebhookService webhookService){
        this.webhookService=webhookService;
    }

    @Scheduled(cron="*/60 * * * * *")
    public void alram(){
        webhookService.sendMessage();
    }
}
