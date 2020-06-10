package runner;

import com.vk.api.sdk.exceptions.ApiException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Main {

    //https://oauth.vk.com/authorize?client_id=7498451&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends,docs,wall,groups,offline&response_type=code&v=5.107
    //https://oauth.vk.com/access_token?client_id=7498451&client_secret=h3N1Vad7fCrH1Ko6AHFu&redirect_uri=https://oauth.vk.com/blank.html&code=5528b774d047d02214


    /*public static VkMessageGroup vkMessageGroup;
    static {
        try {
            vkMessageGroup = new VkMessageGroup(ACCESS_TOKEN);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }*/

    public static final Logger LOGGER;

    static {
        LOGGER = Logger.getLogger("VK API Groups");
        try {
            File logDir = new File("./logs/");
            if(!logDir.exists())
                logDir.mkdir();
            String logPattern = String.format("%s%clog%s.log", logDir.getAbsolutePath(), File.separatorChar, LocalDate.now());
            FileHandler fileHandler = new FileHandler(logPattern, true);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            LOGGER.addHandler(fileHandler);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) throws ApiException, InterruptedException {

        System.out.println("========Start========");


        try {

            MainLaunch mainLaunch = new MainLaunch();


        } catch (Exception e) {
            e.printStackTrace();
        }

         /*System.out.println("Running server...");
        while (true) {
            Thread.sleep(300);
            try {
                Message message = vkMessageGroup.getMessage();
                if (message != null) {
                    ExecutorService exec = Executors.newCachedThreadPool();
                    exec.execute(new Messenger(message));
                }

            } catch (ClientException | ApiException e) {
                System.out.println("Возникли проблемы");
                final int RECONNECT_TIME = 10000;
                System.out.println("Повторное соединение через " + RECONNECT_TIME / 1000 + " секунд");
                Thread.sleep(RECONNECT_TIME);

            }
        }*/
    }


}
