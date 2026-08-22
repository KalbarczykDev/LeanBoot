package example;

import lib.annotation.Qualifier;
import lib.annotation.Service;

@Service
@Qualifier("email")
public class EmailSender implements MessageSender{
    @Override
    public void send() {
        System.out.println("send");
    }
}
