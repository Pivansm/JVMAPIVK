package apivk;

import com.vk.api.sdk.objects.messages.Message;

public class Unknown extends Command {

    public Unknown(String name) {
        super(name);
    }

    @Override
    public void exec(Message message) {
        System.out.println("Unknown command");
    }
}
