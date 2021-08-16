package COVID19.WhenWeMaskOff.domain;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Member {
    @Id
    private String id;
    private String webhook;
    private String region;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWebhook() {
        return webhook;
    }

    public void setWebhook(String webhook) {
        this.webhook = webhook;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }
}
