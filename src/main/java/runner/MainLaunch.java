package runner;

import apivk.ApiPostVK;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import exporttxt.Records;
import exporttxt.ResultSetToTxt;
import exporttxt.TableRecordsAll;
import setting.DBConnector;
import setting.Setting;
import setting.SettingJson;
import sqlitejdbc.SQLiteDAO;

import java.sql.Connection;
import java.sql.SQLException;

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
        //Строка вставки
        String insertQuery = sqLiteDAO.fieldsToSqlParameter();
        //Заполнение тбл
        postToTableSqlite(insertQuery);
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

                rs.addCell(gr.getId());
                rs.addCell(gr.getFromId());

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
            System.out.println("Запись данных в БД!");
            sqLiteDAO.insertBatch(tbl, insertQuery, 1000);
        }
    }
}
