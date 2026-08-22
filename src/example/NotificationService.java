package example;

import lib.annotation.Inject;
import lib.annotation.Qualifier;
import lib.annotation.Service;

@Service
public class NotificationService {
    private final MessageSender messageSender;

    @Inject
    public NotificationService(@Qualifier("email") MessageSender messageSender) {
        this.messageSender = messageSender;
    }

    public void send() {
        messageSender.send();
    }
}
