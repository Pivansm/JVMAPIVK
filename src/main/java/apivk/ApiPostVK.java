package apivk;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.board.responses.GetTopicsResponse;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.board.BoardGetTopicsOrder;
import com.vk.api.sdk.queries.board.BoardGetTopicsPreview;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import setting.Setting;

import java.util.List;

public class ApiPostVK {
    private VkApiClient vk;
    private UserActor actor;
    private UserActor actorGroup;
    private Setting setting;

    public ApiPostVK(Setting inSetting) {
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        this.setting = inSetting;
        actor = new UserActor(setting.getClient_id(), setting.getAccess_token());
        actorGroup = new UserActor(-151897652, setting.getAccess_token());
    }

    public VkApiClient getVk() {
        return vk;
    }

    public String getClient() {
        //Данные о клиенте
        try {
            String getResponse = vk.users().get(actor)
                    .userIds("492829072")
                    .executeAsString();
            System.out.println(getResponse);

            return getResponse;
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void getGroups() {
        try {
            //Группа Сообщество
            //String getGroups = vk.groups().getById(actor)
            List<GroupFull> getGroups = vk.groups().getById(actor)
                    .groupId("151897652")
                    .execute();

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

    public void getPostGroup(int nmGroup) {
        try {
            //Посты в группе

            GetResponse getPostGrp = vk.wall().get(actor)
                    .filter(WallGetFilter.ALL)
                    .ownerId(-1*nmGroup)
                    //.count(5)
                    //.offset(0)
                    .execute();

            System.out.println(getPostGrp.toString());

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    public void getBoardGroup() {
        try {
            GetTopicsResponse getBoardGrp = vk.board().getTopics(actor, 151897652)
                    .preview(BoardGetTopicsPreview.FIRST_COMMENT)
                    .order(BoardGetTopicsOrder.BY_CREATED_DATE_DESC)
                    //.count(10)
                    .execute();
            //.getCount();

            System.out.println(getBoardGrp.toString());

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }
}
