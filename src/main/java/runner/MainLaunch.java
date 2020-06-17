package runner;

import apivk.ApiPostVK;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import exporttxt.Records;
import exporttxt.ResultSetToTxt;
import exporttxt.TableRecordsAll;
import setting.DBConnector;
import setting.Setting;
import setting.SettingJson;
import sqlitejdbc.SQLiteDAO;

import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;

public class MainLaunch {
    private Setting setting;
    private SettingJson settingJson;
    private Connection connection;
    private DBConnector connector;
    private SQLiteDAO sqLiteDAO;

    public MainLaunch() throws SQLException, ClassNotFoundException {
        settingJson = new SettingJson();
        settingJson.create();
        setting = settingJson.findEntityBy();

        String conn_driver = "org.sqlite.JDBC";
        String conn_url = "jdbc:sqlite:dbsqlrb.sqlite";
        connector = new DBConnector(conn_driver, conn_url, null, null);
        sqLiteDAO = new SQLiteDAO(connector.getConnection());
    }

    public void processingFoundation() {
        try {
            //Строка вставки
            String insertQuery = sqLiteDAO.fieldsToSqlParameter("POSTVK");
            //Заполнение тбл
            //postToFileTxt();
            postToTableSqlite(insertQuery);
            //postToTableReport();
            //getToGroupById();
            //Получить Id группы
            //getToIdGroupReport();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void postToFileTxt() {

        ResultSetToTxt toTxt = new ResultSetToTxt("export.csv");
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        String[] strGroups = setting.getGroup_id().split("[, ]+");
        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));
            int countZ = getPostGrp.getCount();
            System.out.println("Количество постов:" + countZ);
            //String nmGroup = getPostGrp.
            //System.out.println(getPostGrp.toString());
            TableRecordsAll tbl = new TableRecordsAll();
            for(var gr : getPostGrp.getItems()) {
                Records rs = new Records();
                rs.addCell(gr.getFromId());
                rs.addCell(gr.getId());

                System.out.println("Id:" + gr.getId() + ": " +gr.getText());
                var at = gr.getAttachments();
                System.out.println("Ata:" + at.toString());
                String strCaption = "";
                for(int i = 0; i < at.size(); i++) {
                    var ati = at.get(i);
                    System.out.println("" + ati.getLink());
                    var lk = ati.getLink();
                    if(lk != null) {
                        System.out.println(lk.getCaption());
                        strCaption += lk.getCaption() + ",";
                    }
                }
                rs.addCell(strCaption);
                rs.addCell(gr.getText());
                tbl.addRecords(rs);
            }
            //
            toTxt.toFileTxtExport(tbl);
        }
    }

    public void postJsonToFileTxt() {

        ResultSetToTxt toTxt = new ResultSetToTxt("export.csv");
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        String[] strGroups = setting.getGroup_id().split("[, ]+");

        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));


        }
    }

    private void postToTableSqlite(String insertQuery) {

        ApiPostVK apiPostVK = new ApiPostVK(setting);
        String[] strGroups = setting.getGroup_id().split("[, ]+");
        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));

                int countZ = getPostGrp.getCount();
                System.out.println("Количество постов:" + countZ);
                TableRecordsAll tbl = new TableRecordsAll();

                for (var gr : getPostGrp.getItems()) {
                    Records rs = new Records();

                    rs.addCell(gr.getId());
                    rs.addCell(gr.getFromId());

                    System.out.println("Id:" + gr.getId() + " userId:" + gr.getFromId() + " :" + gr.getText());

                    //Данные о клиенте
                    getUserData(apiPostVK, gr);

                    var at = gr.getAttachments();
                    String strCaption = "";
                    if (at != null) {
                        System.out.println("Ata:" + at.toString());

                        for (int i = 0; i < at.size(); i++) {
                            var ati = at.get(i);
                            System.out.println("" + ati.getLink());
                            var lk = ati.getLink();
                            if (lk != null) {
                                System.out.println(lk.getCaption());
                                strCaption += lk.getCaption() + ",";
                            }
                        }
                    }
                    rs.addCell(strCaption);
                    rs.addCell(gr.getText());
                    rs.addCell(null);
                    rs.addCell(null);
                    tbl.addRecords(rs);
                }
                for (int j = 11; j < countZ; j++) {
                    if (j % 10 == 0) {
                        GetResponse getPostGrp2 = apiPostVK.getPostGroupOffs10(Integer.parseInt(group), j);
                        for (WallPostFull gr : getPostGrp2.getItems()) {
                            System.out.println("Id:" + gr.getId() + " userId: " + gr.getFromId() + ": " + gr.getText());
                            Records rs = new Records();

                            rs.addCell(gr.getId());
                            rs.addCell(gr.getFromId());

                            //Данные о клиенте
                            getUserData(apiPostVK, gr);

                            var at = gr.getAttachments();
                            //System.out.println("Ata:" + at.toString());
                            String strCaption = "";
                            if (at != null) {
                                for (int i = 0; i < at.size(); i++) {
                                    var ati = at.get(i);
                                    System.out.println("" + ati.getLink());
                                    var lk = ati.getLink();
                                    if (lk != null) {
                                        System.out.println(lk.getCaption());
                                        strCaption += lk.getCaption() + ",";
                                    }
                                }
                            }
                            rs.addCell(strCaption);
                            rs.addCell(gr.getText());
                            rs.addCell(null);
                            rs.addCell(null);
                            tbl.addRecords(rs);
                        }

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.fillInStackTrace();
                            Thread.currentThread().interrupt();
                        }
                    }
                }

                //
                System.out.println("Запись данных в БД!");
                sqLiteDAO.insertBatch(tbl, insertQuery, 1000);

         }
    }

    public void getUserData(ApiPostVK apiPostVK, WallPostFull gr) {

        if(gr.getFromId() > 0) {
            List<UserXtrCounters> users = apiPostVK.getClient("" + gr.getFromId());
            UserXtrCounters user = users.get(0);
            System.out.println("Фам: " + user.getFirstName() + " Имя: " + user.getLastName() + " Др:" + user.getBdate());

            try {
                Thread.sleep(700);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        else {
            System.out.println("Фам: Admin");
        }
    }

    private void postToTableReport() {

        ApiPostVK apiPostVK = new ApiPostVK(setting);
        String[] strGroups = setting.getGroup_id().split("[, ]+");
        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));
            //GetResponse getPostGrp = apiPostVK.getPostGroupOffs10(Integer.parseInt(group), 0);
            int countZ = getPostGrp.getCount();
            System.out.println("Количество постов:" + countZ);

            System.out.println("Количество постов:" + getPostGrp.toString());

            for (var gr : getPostGrp.getItems()) {
                System.out.println("Id:" + gr.getId() + ": " + gr.getText());
                var at = gr.getAttachments();
                String strCaption = "";
                if (at != null) {
                    System.out.println("Ata:" + at.toString());

                    for (int i = 0; i < at.size(); i++) {
                        var ati = at.get(i);
                        System.out.println("" + ati.getLink());
                        var lk = ati.getLink();
                        if (lk != null) {
                            System.out.println(lk.getCaption());
                            strCaption += lk.getCaption() + ",";
                        }
                    }
                }
                //
            }
            for (int j = 1; j < countZ; j++) {
                //if (j % 10 == 0) {
                    GetResponse getPostGrp2 = apiPostVK.getPostGroupOffs10(Integer.parseInt(group), j);
                    System.out.println("Количество постов:" + getPostGrp2.toString());

                    for (var gr : getPostGrp2.getItems()) {
                        System.out.println("Id:" + gr.getId() + " userId: " + gr.getFromId() + ": " + gr.getText());
                        var at = gr.getAttachments();
                        //System.out.println("Ata:" + at.toString());
                        String strCaption = "";
                        if (at != null) {
                            for (int i = 0; i < at.size(); i++) {
                                var ati = at.get(i);
                                System.out.println("" + ati.getLink());
                                var lk = ati.getLink();
                                if (lk != null) {
                                    System.out.println(lk.getCaption());
                                    strCaption += lk.getCaption() + ",";
                                }

                            }
                        }

                        //Комментарии
                        exportCommentsToRep(apiPostVK, group, gr.getId());

                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.fillInStackTrace();
                            Thread.currentThread().interrupt();
                        }

                    }

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.fillInStackTrace();
                        Thread.currentThread().interrupt();
                    }
                //}
            }

            //
            System.out.println("Запись данных в БД!");

        }
    }

    public void exportCommentsToRep(ApiPostVK apiPostVK, String group, int commentId) {

        var commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);
        for(var cm : commentsAll.getItems()) {
            var comm = cm.getText();

            System.out.println(":" + cm.getFromId() + " text:" +  comm);
            if(cm.getFromId() > 0) {
                List<UserXtrCounters> users = apiPostVK.getClient("" + cm.getFromId());
                //List<UserXtrCounters> users = apiPostVK.getClient("10549922");
                UserXtrCounters user = users.get(0);
                //for(var us : users) {
                System.out.println("Фам: " + user.getFirstName() + " Имя: " + user.getLastName() + " Др:" + user.getBdate());
                //}
            }
            else {
                System.out.println("Фам: Admin");
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

    public void exportCommentsToTbl(ApiPostVK apiPostVK, String group, int commentId) {

        var commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);
        for(var cm : commentsAll.getItems()) {
            var comm = cm.getText();

            System.out.println(":" + cm.getFromId() + " text:" +  comm);

            List<UserXtrCounters> users = apiPostVK.getClient("" +cm.getFromId());
            //List<UserXtrCounters> users = apiPostVK.getClient("10549922");
            UserXtrCounters user = users.get(0);
            //for(var us : users) {
            System.out.println("Фам: " + user.getFirstName() + " Имя: " + user.getLastName() + " Др:" + user.getBdate());
            //}

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
        }

    }

    public void getToGroupById() {
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        List<WallPostFull> getPostGrp = apiPostVK.getPostGroupById("-187639665_2");
        for(var wl : getPostGrp) {
            System.out.println(wl.toString());
        }
    }

    public void getToIdGroupReport() {

        ApiPostVK apiPostVK = new ApiPostVK(setting);
        HashSet<String> hashSet = importFileGroup("bdnmgroup.txt");

        for(String gr : hashSet) {
            apiPostVK.getIDGroup(gr);

        }
    }

    public HashSet<String> importFileGroup(String inFile) {
        HashSet<String> hashSet = new HashSet<>();
        File file = new File(inFile);

        try
        {
            BufferedReader b = new BufferedReader(new FileReader(file));
            String readLine = "";

            while ((readLine = b.readLine()) != null) {
                hashSet.add(readLine);
            }

            b.close();

            return hashSet;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
