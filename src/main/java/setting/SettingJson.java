package setting;

import com.google.gson.Gson;

import java.io.*;

public class SettingJson extends AbstractJson<Setting>{
    Gson gson;

    public Setting findEntityBy() {
        Setting setting;
        gson = new Gson();
        File file = new File("ThisSetting.json");
        try(BufferedReader reader = new BufferedReader(new FileReader(file)))
        {
            String settingJson = reader.readLine();
            setting = gson.fromJson(settingJson, Setting.class);
            return setting;
        }
        catch (IOException e) {
            e.fillInStackTrace();
            return null;
        }

    }

    public boolean create() {
        Setting setting = new Setting();
        gson = new Gson();
        File file = new File("ThisSetting.json");
        if(!file.exists()) {
            setting.setClient_id(7498451);
            setting.setGroup_id("151897652");
            setting.setAccess_token("624e44cb4413ef63fabc370bf8551de62615a112861007c383b81269bef10226bf01b9afbd5574651af2e");

            String json = gson.toJson(setting);
            try(FileWriter writer = new FileWriter(file, true))
            {
                writer.write(json);
                return true;
            }
            catch (IOException e) {
                e.fillInStackTrace();
                return false;
            }
        }
        return false;
    }
}
