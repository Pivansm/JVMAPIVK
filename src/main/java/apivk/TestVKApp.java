package apivk;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

public class TestVKApp {
    private static final String LOGIN = ""; // Телефон или E-mail
    private static final String PASSWORD = ""; // Пароль
    private static String USER_ID = null;
    private static HttpClient client;
    private static HttpResponse res;
    private static HttpPost post;
    private static HttpGet get;
    private static JsonParser parser;
    private static String PERMISSIONS_FRIENDS = "friends";
    private static String PERMISSIONS_NOTES = "notes";
    private static String PERMISSIONS_STATUS = "status";
    private static String PERMISSIONS_WALL = "wall";
    private static String PERMISSIONS_GROUPS = "groups";
    private static String PERMISSIONS_MESSAGES = "messages";
    private static String PERMISSIONS_STATS = "stats";
    private static String PERMISSIONS_OFFLINE = "offline";
    private static final String APP_ID = ""; // ID вашего приложения
    private static final String PERMISSIONS = "messages,status,wall,offline"; // http://vk.com/dev/permissions
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html"; // Заглушка для Standalone-приложений
    private static final String DISPLAY = "mobile"; // page|popup|mobile
    private static final String API_VERSION = "5.5"; // Последняя на данный момент
    private static final String RESPONSE_TYPE = "token"; // Есть ещё code, но это для сайтов
    //    private static String link = "http://oauth.vk.com/authorize?"
//                              + "client_id=" + APP_ID
//                              + "&scope=" + PERMISSIONS
//                              + "&redirect_uri=" + REDIRECT_URI
//                              + "&display=" + DISPLAY
//                              + "&v=" + API_VERSION
//                              + "response_type=" + RESPONSE_TYPE;
    private static String ACCESS_TOKEN = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            client = getHttpClient();
            parser = new JsonParser();
            // Запостим текст себе на стену
            get = new HttpGet("https://api.vk.com/method/wall.post?message=This+is+the+test+message+from+my+simple+app.&access_token=" + getAccessToken());
            client.execute(get);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    /**
     * Возвращает access_token
     */
    private static String getAccessToken() {
        try
        {
            if (client == null) {
                client = getHttpClient();
            }
            get = new HttpGet("https://oauth.vk.com/token?grant_type=password&client_id=3140623&client_secret=VeWdmVclDCtn6ihuP1nt&username=" + LOGIN + "&password=" + URLEncoder.encode(PASSWORD) + "&scope=" + getPermissions() + "&test_redirect_uri=" + "0" + "&v=" + "5.23");

            client.execute(get);
            res = client.execute(get);
            get.releaseConnection();
            JsonObject result = parser.parse(new InputStreamReader(res.getEntity().getContent())).getAsJsonObject();
            if (result.toString().contains("error")) {
                System.err.println("------VK Off-------");
                if (result.toString().contains("need_captcha")) {
                    System.err.println("Вылезла капча");
                    System.err.println(result.get("captcha_sid").getAsString());
                    System.err.println(result.get("captcha_img").getAsString());
                }
                if (result.toString().contains("invalid_client")) {
                    System.err.println("Ошибка авторизации");
                    System.err.println(result.get("error_description").getAsString());
                }
                if (result.toString().contains("need_validation")) {
                    System.err.println("Требуется ввести недостающие цифры номер телефона");
                    System.err.println("redirect_uri: " + result.get("redirect_uri").getAsString());
                    if (result.get("redirect_uri").getAsString() != null) {
                        LoginSecurityCheck(result.get("redirect_uri").getAsString());
                    }
                }
                System.err.println("contains: " + result.get("error").toString());
                System.err.println("-------------------");
                return null;
            }

            ACCESS_TOKEN = result.get("access_token").getAsString();
            USER_ID = result.get("user_id").getAsString();
        } catch (Exception ex) {
            System.err.println("VKProtocol (getOfficialAccessToken): " + ex.getMessage());
            ex.printStackTrace();
        }
        return ACCESS_TOKEN;
    }
    public static void LoginSecurityCheck(String url){
        try
        {
            if (LOGIN.substring(0, 2).contains("+7"))
            {
                get = new HttpGet(url);
                HttpResponse res1 = client.execute(get);
                get.releaseConnection();
                String html = EntityUtils.toString(res1.getEntity());
                post = new HttpPost("https://m.vk.com" + html.split(" action=\"")[1].split("\">")[0]);
                List formdata = new ArrayList();
                formdata.add(new BasicNameValuePair("code", LOGIN.substring(2, 10)));

                UrlEncodedFormEntity form = new UrlEncodedFormEntity(formdata, "UTF-8");
                post.setEntity(form);
                res1 = client.execute(post);
                post.releaseConnection();
            } else {
                System.err.println("В качестве логина введена почта.");
            }
        } catch (Exception ex) {
            System.err.println("VKProtocol (LoginSecurityCheck): " + ex.getMessage());
            ex.printStackTrace();
        }
    }


    public static String getPermissions(){
        String s = "";
        s = s + PERMISSIONS_FRIENDS + ",";
        s = s + PERMISSIONS_NOTES + ",";
        s = s + PERMISSIONS_STATUS + ",";
        s = s + PERMISSIONS_WALL + ",";
        s = s + PERMISSIONS_GROUPS + ",";
        s = s + PERMISSIONS_MESSAGES + ",";
        s = s + PERMISSIONS_STATS + ",";
        s = s + PERMISSIONS_OFFLINE;
        System.out.println("Request authorization: " + s);
        return s;
    }

    /**
     * Возвращает сконфигурированный HttpClient
     */
    private static HttpClient getHttpClient() {
        HttpClient client = null;
        try {
            HttpClient httpclient = new DefaultHttpClient();
            ClientConnectionManager baseCcm = httpclient.getConnectionManager();
            SchemeRegistry sr = baseCcm.getSchemeRegistry();
            ClientConnectionManager safeCcm = new ThreadSafeClientConnManager(sr);
            /**
             * Принимаем все сертификаты
             */
            /*X509TrustManager dontCareTrustManager = new X509TrustManager() {
                private final X509Certificate[] empty = new X509Certificate[0];

                @Override
                public X509Certificate[] getAcceptedIssuers() { return empty; }

                @Override
                public void checkServerTrusted(X509Certificate[] ar, String st) throws CertificateException {}

                @Override
                public void checkClientTrusted(X509Certificate[] ar, String st) throws CertificateException {}
            };*/
            SSLContext ctx = SSLContext.getInstance("TLS");
            //ctx.init(null, new TrustManager[] { dontCareTrustManager }, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            sr.register(new Scheme("https", 443, ssf));
            httpclient = new DefaultHttpClient(safeCcm, httpclient.getParams());
            httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY, CookiePolicy.BROWSER_COMPATIBILITY);
            httpclient.getParams().setParameter(ClientPNames.HANDLE_REDIRECTS, false);
            httpclient.getParams().setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:26.0) Gecko/20100101 Firefox/26.0");
            client = httpclient;
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        return client;
    }
    /**
     * Возвращает сконфигурированный HttpContext
     */
    private static HttpContext getHttpContext() {
        HttpContext localContext = new BasicHttpContext();
        CookieStore cookieStore = new BasicCookieStore();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
        return localContext;
    }
}
