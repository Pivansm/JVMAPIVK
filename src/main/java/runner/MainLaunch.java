package runner;

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


}
