package apivk;

import com.google.gson.*;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiCaptchaException;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.ServiceClientCredentialsFlowResponse;
import com.vk.api.sdk.objects.board.responses.GetTopicsResponse;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.users.UserXtrCounters;
import com.vk.api.sdk.objects.utils.DomainResolved;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.responses.GetCommentsResponse;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.board.BoardGetTopicsOrder;
import com.vk.api.sdk.queries.board.BoardGetTopicsPreview;
import com.vk.api.sdk.queries.users.UserField;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import netscape.javascript.JSObject;
import setting.Setting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ApiPostVK {
    private VkApiClient vk;
    private UserActor actor;
    private UserActor actorGroup;
    private UserActor actorUser;
    private ServiceActor actorSrv;
    private Setting setting;

    public ApiPostVK(Setting inSetting) {
        TransportClient transportClient = HttpTransportClient.getInstance();
        vk = new VkApiClient(transportClient);
        this.setting = inSetting;
        actor = new UserActor(setting.getClient_id(), setting.getAccess_token());
        actorGroup = new UserActor(-151897652, setting.getAccess_token());
        actorUser = new UserActor(setting.getClient_id(), setting.getSecure_key());
        actorSrv = new ServiceActor(setting.getClient_id(), setting.getSecure_key());
    }

    public VkApiClient getVk() {
        return vk;
    }

    public List<UserXtrCounters> getClient(String userId) {
        //Данные о клиенте
        try {

            List<UserXtrCounters> getResponse = vk.users().get(actorGroup)
                    .userIds(userId)
                    .execute();

            return getResponse;
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void getClientId(String userId) {


        try {

            ServiceClientCredentialsFlowResponse authResponse = vk.oauth()
                    .serviceClientCredentialsFlow(setting.getClient_id(), "h3N1Vad7fCrH1Ko6AHFu")
                    .execute();
            System.out.println(authResponse.getAccessToken());
            actorSrv = new ServiceActor(setting.getClient_id(),"h3N1Vad7fCrH1Ko6AHFu", authResponse.getAccessToken());
            var getResponse = vk.users().get(actorSrv)
                    .userIds(userId)
                    //.fields(UserField.BDATE, UserField.CITY, UserField.)
                    .execute();
            System.out.println("" + getResponse.toString());

        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }

    }

    public void getClientSearch() {
        try {

            var getResponse = vk.users().search(actor)
                    .groupId(151897652)
                    .count(10)
                    //.fields(UserField.BDATE, UserField.CITY, UserField.)
                    .execute();
            for(var it : getResponse.getItems()) {
                System.out.println("" + it.toString());
            }

        } catch (ClientException e) {
            e.printStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }


    public void getGroups() {
        try {

            List<GroupFull> getGroups = vk.groups().getById(actor)
                    .groupId("151897652")
                    .execute();

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

    public GetResponse getPostGroup(int nmGroup) {
        String captchaSid = null;
        String captchaImg = null;
        try {
            //Посты в группе
            GetResponse getPostGrp = vk.wall().get(actor)
                    .filter(WallGetFilter.ALL)
                    .ownerId(-1 * nmGroup)
                    .count(10)
                    .execute();

            return getPostGrp;

        } catch (ApiCaptchaException e) {
            captchaSid = e.getSid();
            e.fillInStackTrace();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }


        return null;
    }

    public GetResponse getPostGroupOffs10(int nmGroup, int offset) {

        try {

            GetResponse getPostGrp = vk.wall().get(actor)
                    .filter(WallGetFilter.ALL)
                    .ownerId(-1*nmGroup)
                    .count(10)
                    .offset(offset)
                    .execute();

            return getPostGrp;

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<WallPostFull> getPostGroupById(String nmGroup) {

        try {

            List<WallPostFull> getPostGrp =  vk.wall().getById(actor, nmGroup)
                    .execute();

            return getPostGrp;

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

        return null;
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

    public void getIDGroup(String nmGroup) {

        DomainResolved getIdGrp = null;
        try {
            getIdGrp = vk.utils().resolveScreenName(actor, nmGroup)
                    .execute();
            System.out.println(getIdGrp.toString());
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }

    }

    public GetCommentsResponse getGroupComments(int nmGroup, int postId) {

        try {
            GetCommentsResponse commentsResponse = vk.wall().getComments(actor, postId)
                    .ownerId(-1 * nmGroup)
                    .execute();
            System.out.println(commentsResponse.toString());
            return commentsResponse;

        } catch (ApiException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public VkUserDul getClientPost(String clientId) throws IOException, ClientException, ApiException {

        Gson gson = new Gson();
        String url = "https://api.vk.com/method/users.get?user_ids=" + clientId + "&fields=bdate&access_token=" + setting.getAccess_token() + "&v=5.110";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // из документации: параметры могут передаваться как методом GET, так и POST. Если вы будете передавать большие данные (больше 2 килобайт), следует использовать POST.
        connection.setRequestMethod("GET");
        // посылаем запрос и сохраняем ответ
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer userdul = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            userdul.append(inputLine);
        }
        in.close();
        // выведет json-ответ запроса
        //System.out.println(userdul.toString());

        JsonObject jsonObject = new JsonParser()
                .parse(userdul.toString())
                .getAsJsonObject();

        JsonElement ps  = jsonObject.get("response");
        //    System.out.println(ps.toString());

        VkUserDul userFioDr = gson.fromJson(ps.toString().replace("[", "").replace("]", ""), VkUserDul.class);
        return userFioDr;

    }
}
