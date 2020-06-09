package apivk;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;


public class ApiMessageVK {
    private static final String ACCESS_TOKEN = "30e244f15729306884a97a85b9070d21a1c321fd736cf8669cd9e4b909ae03608e48e84e41c03f08f9cdf";
    private static final String CLIENT_ID = "7498451";
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html";
    private static final String API_VERSION = "5.107"; // Последняя на данный момент
    private static final String RESPONSE_TYPE = "code"; // Есть ещё code, но это для сайтов
    private static final String DISPLAY = "page"; // page|popup|mobile

    private static String link = "http://oauth.vk.com/authorize?"
            + "client_id=" + CLIENT_ID
            + "&scope=" + "offline"
            + "&redirect_uri=" + REDIRECT_URI
            + "&display=" + DISPLAY
            + "&v=" + API_VERSION
            + "&response_type=" + RESPONSE_TYPE;

    public ApiMessageVK() {

    }

    public void getMessage(String acc_toke) throws IOException {
        // формируют url запроса
        String url = "https://api.vk.com/method/messages.get?count=20&access_token=" + acc_toke;
        //String url = "https://oauth.vk.com/authorize?client_id=7498451&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=friends&response_type=code&v=5.107";

        URL obj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

        // из документации: параметры могут передаваться как методом GET, так и POST. Если вы будете передавать большие данные (больше 2 килобайт), следует использовать POST.
        connection.setRequestMethod("GET");
        // посылаем запрос и сохраняем ответ
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        // выведет json-ответ запроса
        System.out.println(response.toString());
    }

    public String getCode(String scope) {

        try {
            HttpClient httpclient = new DefaultHttpClient();
            //CloseableHttpClient httpclient = HttpClients.createDefault();
            // Делаем первый запрос
            //https://oauth.vk.com/authorize?client_id=7498451&display=page&redirect_uri=https://oauth.vk.com/blank.html&scope=offline&response_type=code&v=5.107
            HttpPost post = new HttpPost("https://oauth.vk.com/authorize?" +
                    "client_id=" + CLIENT_ID +
                    "&display=popup" +
                    "&redirect_uri=" + REDIRECT_URI +
                    "&scope=" + scope +
                    "&response_type=code" +
                    "&v=5.107");

            HttpResponse response2;
            //CloseableHttpResponse response2 = httpclient.execute(post);
            response2 = httpclient.execute(post);

            //post.abort();
            HttpEntity entity2 = response2.getEntity();
            System.out.println(EntityUtils.toString(entity2));

            //BufferedReader br = new BufferedReader(new InputStreamReader(response2.getEntity().getContent()));
            //String output;
            //while((output = br.readLine()) != null) {
            //    System.out.println(output);
            //}
            //
            InputStream is = response2.getEntity().getContent();
            StringBuffer content = new StringBuffer();
            int i;
            while((i = is.read()) != -1) content.append((char) i);
            String ip_h = content.toString().split("name=\"ip_h\" value=\"")[1].split("\"")[0];
            System.out.println(ip_h);
            String to_h = content.toString().split("name=\"to\" value=\"")[1].split("\"")[0];
            System.out.println(to_h);
            link = "http://login.vk.com/?act=login&soft=1&utf8=1";
            //content = new StringBuffer();
            //get.releaseConnection();

             //HttpResponse response;
            //response = httpclient.execute(post);
            post.abort();
            //Получаем редирект
            Header[]headers = response2.getAllHeaders();
            for (Header header : headers) {
                System.out.println("Key : " + header.getName()
                        + " ,Value : " + header.getValue());
            }
            String server = response2.getFirstHeader("Server").getValue();
            System.out.println(":" + server);
            //response2.



            String HeaderLocation = response2.getFirstHeader("Location").getValue();
            //URI RedirectUri = new URI(HeaderLocation);
            //Для запроса авторизации необходимо два параметра полученных в первом запросе
            //ip_h и to_h
            //String ip_h = RedirectUri.getQuery().split("&")[2].split("=")[1];
            //String to_h =RedirectUri.getQuery().split("&")[4].split("=")[1];

            //System.out.println(":" + ip_h + " :" + to_h);

            //String HeaderLocation = response.getFirstHeader("location").getValue();
            //URI RedirectUri = new URI(HeaderLocation);
            //Для запроса авторизации необходимо два параметра полученных в первом запросе
            //ip_h и to_h
            //String code_h = RedirectUri.getQuery().split("#")[0].split("=")[1];
            String code_h ="A";
            return code_h;

        } catch (Exception e) {
            e.printStackTrace();

        }
        //catch (URISyntaxException e) {
        //    e.printStackTrace();
        //}
        return null;
        // Просто спарсим его сплитами
        //access_token = HeaderLocation.split("#")[1].split("&")[0].split("=")[1];
    }

    private String getOAuthUrl() {
        //return "https://oauth.vk.com/authorize?client_id=" + clientId + "&display=page&redirect_uri=" + getRedirectUri() + "&scope=groups&response_type=code";
        return "https://oauth.vk.com/authorize?client_id=";
    }


    public void getCodeWebCln() {
        /*WebView wv = (WebView) this.findViewById(R.id.webView1);
        WebSettings webSettings = wv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wv.setHorizontalScrollBarEnabled(false);
        webSettings.setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
        wv.setWebViewClient(new VkWebViewClient());
        wv.loadUrl("https://oauth.vk.com/authorize?client_id=111111&scope=friends,notify,photos,photos,audio,video,docs,notes,pages,groups,offline&redirect_uri=https://oauth.vk.com/blank.html&display=mobile&v=5.5&response_type=token&revoke=1");

         */
    }
}
