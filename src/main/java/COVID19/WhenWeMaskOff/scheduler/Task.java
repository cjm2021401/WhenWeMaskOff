package COVID19.WhenWeMaskOff.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Task {

    @Scheduled(cron="*/5 * * * * *")
    public void alram(){
        System.out.println("알람");
    }
}
