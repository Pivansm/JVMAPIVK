package runner;

import com.vk.api.sdk.exceptions.ApiException;
import setting.DBConnector;
import sqlitejdbc.SQLiteDAO;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
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

    private static SQLiteDAO sqLiteDAO;
    private static DBConnector connectorSqlite;
    private static final String CONN_IRL = "jdbc:sqlite:dbsqlrb.sqlite";
    private static final String CONN_DRIVER = "org.sqlite.JDBC";


    public static void main(String[] args) throws ApiException, InterruptedException {

        System.out.println("========Start========");
        File currSqlite = new File("dbsqlrb.sqlite");
        if(!currSqlite.exists()) {
            System.out.println("Нет БД dbsqlrb.sqlite");
            try
            {
                connectorSqlite = new DBConnector(CONN_DRIVER, CONN_IRL, null, null);
                sqLiteDAO = new SQLiteDAO(connectorSqlite.getConnection());
                sqLiteDAO.createTable();
                sqLiteDAO.closeConnection();

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            System.out.println("БД dbsqlrb.sqlite OK!");
        }


        try {

            MainLaunch mainLaunch = new MainLaunch();
            //mainLaunch.postToFileTxt();
            mainLaunch.processingFoundation();


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
