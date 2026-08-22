package example;

import lib.annotation.Primary;
import lib.annotation.Qualifier;
import lib.annotation.Service;

@Service
@Primary
@Qualifier("console")
public class ConsoleMessageSender implements MessageSender {
    @Override
    public void send() {
        System.out.println("ConsoleMessageSender is sending...");
    }
}
