package runner;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.board.responses.GetTopicsResponse;
import com.vk.api.sdk.objects.groups.GroupFull;
import com.vk.api.sdk.objects.messages.Message;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.board.BoardGetTopicsOrder;
import com.vk.api.sdk.queries.board.BoardGetTopicsPreview;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import com.vk.api.sdk.queries.wall.WallGetQuery;
import org.asynchttpclient.Response;


import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    private static final String ACCESS_TOKEN = "10d44a2a4f91889b73c6e5a441577b600fa86e60af01774a166748ab878b83e5bf54e13cd7c218cf57a22e";

    private static final String CLIENT_SECRET = "h3N1Vad7fCrH1Ko6AHFu";
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private static final String CODE = "ab5e7cdfe828b68716";
    //https://oauth.vk.com/authorize?client_id=7498451&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends,docs,wall,groups,offline&response_type=code&v=5.107
    //https://oauth.vk.com/access_token?client_id=7498451&client_secret=h3N1Vad7fCrH1Ko6AHFu&redirect_uri=https://oauth.vk.com/blank.html&code=66f26b1841fff73d8f
    private static int ts;

    /*public static VkMessageGroup vkMessageGroup;
    static {
        try {
            vkMessageGroup = new VkMessageGroup(ACCESS_TOKEN);
        } catch (ApiException | ClientException e) {
            e.printStackTrace();
        }
    }*/

    public static void main(String[] args) throws ApiException, InterruptedException {

        try {

            //ApiMessageVK amvk = new ApiMessageVK();
            //String code = amvk.getCode("offline");
            //System.out.println(code);

            TransportClient transportClient = HttpTransportClient.getInstance();
            VkApiClient vk = new VkApiClient(transportClient);

            /*UserAuthResponse authResponse = vk.oauth()
                    .userAuthorizationCodeFlow(7498451, CLIENT_SECRET, REDIRECT_URI, CODE)
                    .execute();
            */
            //UserActor actor = new UserActor(authResponse.getUserId(), authResponse.getAccessToken());
            UserActor actor = new UserActor(7498451, ACCESS_TOKEN);
            System.out.println(actor.toString());

            //Данные о клиенте
             String getResponse = vk.users().get(actor)
                    .userIds("492829072")
                    .executeAsString();


            System.out.println(getResponse);

            //Группа Сообщество
            //String getGroups = vk.groups().getById(actor)
            List<GroupFull> getGroups = vk.groups().getById(actor)
                    .groupId("151897652")
                    .execute();

            //System.out.println(getGroups);

            UserActor actorGroup = new UserActor(151897652, ACCESS_TOKEN);
            System.out.println(actorGroup.toString());
            //System.out.println(getGroups.getStartDate());

            //Посты в группе
            //for(int i = 0; i < 10; i++) {
            //String getPostGrp
            //WallGetQuery getPostGrp =  vk.wall().get(actorGroup)
            GetResponse getPostGrp =  vk.wall().get(actor)
                        .filter(WallGetFilter.ALL)
                        .ownerId(-151897652)
                        //.count(5)
                        //.offset(0)
                        .execute();

                 System.out.println(getPostGrp.toString());
            //}

            GetTopicsResponse getBoardGrp =  vk.board().getTopics(actor, 151897652)
                    .preview(BoardGetTopicsPreview.FIRST_COMMENT)
                    .order(BoardGetTopicsOrder.BY_CREATED_DATE_DESC)
                    //.count(10)
                    .execute();
                    //.getCount();

            System.out.println(getBoardGrp.toString());


        } catch (ClientException e) {
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
