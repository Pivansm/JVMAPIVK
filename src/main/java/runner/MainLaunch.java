package runner;

import apivk.ApiPostVK;
import apivk.UserFioDr;
import apivk.VkUserDul;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import exporttxt.Records;
import exporttxt.ResultSetToTxt;
import exporttxt.TableRecordsAll;
import setting.DBConnector;
import setting.Setting;
import setting.SettingJson;
import sqlitejdbc.SQLiteDAO;
import sqlitejdbc.SetQueryFields;

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
            SetQueryFields insertQuery = sqLiteDAO.fieldsToSqlParameter("POSTVK");
            SetQueryFields insertComm = sqLiteDAO.fieldsToSqlParameter("COMMENTVK");
            //Заполнение тбл
            //postToFileTxt();
            postToTableSqlite(insertQuery, insertComm);
            //postToTableReport();
            //getToGroupById();
            //Получить Id группы
            //getToIdGroupReport();

            //Клиент
            //userFindId("492829072");

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

    private void postToTableSqlite(SetQueryFields insertQuery, SetQueryFields insertComm) {

        ApiPostVK apiPostVK = new ApiPostVK(setting);
        String[] strGroups = setting.getGroup_id().split("[, ]+");
        for(String group : strGroups) {
            System.out.println("Сообщество: " + group);
            GetResponse getPostGrp = apiPostVK.getPostGroup(Integer.parseInt(group));

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
                int countZ = getPostGrp.getCount();
                System.out.println("Количество постов:" + countZ);
                TableRecordsAll tbl = new TableRecordsAll();

                for (var gr : getPostGrp.getItems()) {
                    Records rs = new Records();

                    rs.addCell(gr.getId());
                    rs.addCell(group);

                    System.out.println("Id:" + gr.getId() + " userId:" + gr.getFromId() + " :" + gr.getText());

                    //Данные о клиенте
                    VkUserDul userFioDr = getUserDul(apiPostVK, "" + gr.getFromId());
                    System.out.println("ФИО: " + userFioDr.getFullName() + " Др:" + userFioDr.getBdate());
                    //Каптион
                    String strCaption = getCaption(gr.getAttachments());
                    //Комментарии
                    exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm);

                    rs.addCell(strCaption);
                    rs.addCell(gr.getText());
                    rs.addCell(gr.getFromId());
                    rs.addCell(userFioDr.getFullName());
                    rs.addCell(userFioDr.getBdate());
                    tbl.addRecords(rs);
                }
                for (int j = 11; j < countZ; j++) {
                    if (j % 10 == 0) {
                        GetResponse getPostGrp2 = apiPostVK.getPostGroupOffs10(Integer.parseInt(group), j);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ex) {
                            ex.fillInStackTrace();
                            Thread.currentThread().interrupt();
                        }
                        if(getPostGrp2 != null) {
                            for (WallPostFull gr : getPostGrp2.getItems()) {
                                System.out.println("Id:" + gr.getId() + " userId: " + gr.getFromId() + ": " + gr.getText());
                                Records rs = new Records();

                                rs.addCell(gr.getId());
                                rs.addCell(group);

                                //Данные о клиенте
                                VkUserDul userFioDr = getUserDul(apiPostVK, "" + gr.getFromId());
                                System.out.println("ФИО: " + userFioDr.getFullName() + " Др:" + userFioDr.getBdate());
                                //Каптион
                                String strCaption = getCaption(gr.getAttachments());
                                //Комментарии
                                exportCommentsToRep(apiPostVK, group, gr.getId(), insertComm);

                                rs.addCell(strCaption);
                                rs.addCell(gr.getText());
                                rs.addCell(gr.getFromId());
                                rs.addCell(userFioDr.getFullName());
                                rs.addCell(userFioDr.getBdate());
                                tbl.addRecords(rs);
                            }

                        }

                    }
                }

                //
                System.out.println("Запись данных в БД!");
                sqLiteDAO.insertBatch(tbl, insertQuery, 1000);

         }
    }

    public void userFindId(String userId) throws IOException, ClientException, ApiException {
        ApiPostVK apiPostVK = new ApiPostVK(setting);
        List<UserXtrCounters> users = apiPostVK.getClient("" + userId);
        UserXtrCounters user = users.get(0);
        System.out.println(user.toString());
        //apiPostVK.getClientId(userId);
        //apiPostVK.getClientSearch();
        apiPostVK.getClientPost(userId);
    }

    public UserFioDr getUserData(ApiPostVK apiPostVK, WallPostFull gr) {

        UserFioDr userFioDr = new UserFioDr();

        if(gr.getFromId() > 0) {
            List<UserXtrCounters> users = apiPostVK.getClient("" + gr.getFromId());
            UserXtrCounters user = users.get(0);
            System.out.println(user.toString());
            System.out.println("Фам: " + user.getFirstName() + " Имя: " + user.getLastName() + " Др:" + user.getBdate());
            userFioDr.setFirstName(user.getFirstName());
            userFioDr.setLastName(user.getLastName());
            userFioDr.setBirthUser(user.getBdate());

            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                ex.fillInStackTrace();
                Thread.currentThread().interrupt();
            }
        }
        else {
            userFioDr.setFirstName("Модератор");
        }

        return userFioDr;
    }

    public VkUserDul getUserDul(ApiPostVK apiPostVK, String clientId) {

        VkUserDul userFioDr = new VkUserDul();
        try {
            if (clientId.indexOf('-') == -1) {
                userFioDr = apiPostVK.getClientPost(clientId);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    ex.fillInStackTrace();
                    Thread.currentThread().interrupt();
                }
            } else {
                userFioDr.setFirst_name("Модератор");
            }

            return userFioDr;

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getCaption(List<WallpostAttachment> at) {

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

        return strCaption;
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
                        //exportCommentsToRep(apiPostVK, group, gr.getId());

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

    public void exportCommentsToRep(ApiPostVK apiPostVK, String group, int commentId, SetQueryFields insertQuery) {

        GetCommentsResponse commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);

        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            ex.fillInStackTrace();
            Thread.currentThread().interrupt();
        }
        if(commentsAll != null) {
            TableRecordsAll tbl = new TableRecordsAll();
            for (var cm : commentsAll.getItems()) {
                Records rs = new Records();

                rs.addCell(cm.getId());
                rs.addCell(group);

                var comm = cm.getText();
                System.out.println(":" + cm.getFromId() + " text:" + comm);

                rs.addCell(comm);
                rs.addCell(cm.getFromId());

                if (cm.getFromId() > 0) {
                    //Данные о клиенте
                    VkUserDul userFioDr = getUserDul(apiPostVK, "" + cm.getFromId());
                    System.out.println("Фам: " + userFioDr.getFullName() + " Имя: " + " Др:" + userFioDr.getBdate());

                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        ex.fillInStackTrace();
                        Thread.currentThread().interrupt();
                    }

                    rs.addCell(userFioDr.getFullName());
                    rs.addCell(userFioDr.getBdate());

                } else {
                    System.out.println("Фам: Admin");
                    rs.addCell("Модератор");
                    rs.addCell(null);
                }
                 tbl.addRecords(rs);
            }

            sqLiteDAO.insertBatch(tbl, insertQuery, 1000);
        }

    }

    public void exportCommentsToTbl(ApiPostVK apiPostVK, String group, int commentId) {

        var commentsAll = apiPostVK.getGroupComments(Integer.parseInt(group), commentId);
        for(var cm : commentsAll.getItems()) {
            var comm = cm.getText();

            System.out.println(":" + cm.getFromId() + " text:" +  comm);
            if(cm.getFromId() > 0) {
                List<UserXtrCounters> users = apiPostVK.getClient("" + cm.getFromId());
                UserXtrCounters user = users.get(0);
                System.out.println("Фам: " + user.getFirstName() + " Имя: " + user.getLastName() + " Др:" + user.getBdate());
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
