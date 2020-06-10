package runner;

import apivk.ApiPostVK;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import exporttxt.Records;
import exporttxt.ResultSetToTxt;
import exporttxt.TableRecordsAll;
import setting.Setting;
import setting.SettingJson;

public class MainLaunch {
    private Setting setting;
    private SettingJson settingJson;

    public MainLaunch() {
        settingJson = new SettingJson();
        settingJson.create();
        setting = settingJson.findEntityBy();
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
}
