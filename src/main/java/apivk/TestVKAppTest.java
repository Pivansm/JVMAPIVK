package apivk;

import java.io.InputStream;
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

public class TestVKAppTest {
    private static final String LOGIN = ""; // Телефон или E-mail
    private static final String PASSWORD = ""; // Пароль

    private static final String APP_ID = ""; // ID вашего приложения
    private static final String PERMISSIONS = "messages,status,wall,offline"; // http://vk.com/dev/permissions
    private static final String REDIRECT_URI = "https://oauth.vk.com/blank.html"; // Заглушка для Standalone-приложений
    private static final String DISPLAY = "mobile"; // page|popup|mobile
    private static final String API_VERSION = "5.5"; // Последняя на данный момент
    private static final String RESPONSE_TYPE = "token"; // Есть ещё code, но это для сайтов

    private static String link = "http://oauth.vk.com/authorize?"
            + "client_id=" + APP_ID
            + "&scope=" + PERMISSIONS
            + "&redirect_uri=" + REDIRECT_URI
            + "&display=" + DISPLAY
            + "&v=" + API_VERSION
            + "&response_type=" + RESPONSE_TYPE;

    private static String ACCESS_TOKEN = null;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            HttpClient client = getHttpClient();
            String access_token = getAccessToken();
            // Запостим текст себе на стену
            HttpGet get = new HttpGet("https://api.vk.com/method/wall.post?message=This+is+the+test+message+from+my+simple+app.&access_token=" + access_token);
            client.execute(get);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Возвращает access_token
     */
    private static String getAccessToken() {
        if(ACCESS_TOKEN == null) {
            try {
                HttpClient client = getHttpClient();
                HttpContext context = getHttpContext();

                // Шаг 1
                HttpGet get = new HttpGet(link);
                HttpResponse response = client.execute(get, context);
                InputStream is = response.getEntity().getContent();
                StringBuffer content = new StringBuffer();
                int i;
                while((i = is.read()) != -1) content.append((char) i);
                String ip_h = content.toString().split("name=\"ip_h\" value=\"")[1].split("\"")[0];
                String to = content.toString().split("name=\"to\" value=\"")[1].split("\"")[0];
                link = "http://login.vk.com/?act=login&soft=1&utf8=1";
                content = new StringBuffer();
                get.releaseConnection();

                //Шаг 2
                HttpPost post = new HttpPost(link);
                List<NameValuePair> formdata = new ArrayList<NameValuePair>();
                formdata.add(new BasicNameValuePair("_origin", "http://oauth.vk.com"));
                formdata.add(new BasicNameValuePair("ip_h", ip_h));
                formdata.add(new BasicNameValuePair("to", to));
                formdata.add(new BasicNameValuePair("email", LOGIN));
                formdata.add(new BasicNameValuePair("pass", PASSWORD));
                UrlEncodedFormEntity form = new UrlEncodedFormEntity(formdata);
                post.setEntity(form);
                response = client.execute(post);
                link = response.getFirstHeader("Location").getValue();
                post.releaseConnection();
                content = new StringBuffer();

                // Шаг 3
                get = new HttpGet(link);
                response = client.execute(get, context);
                is = response.getEntity().getContent();
                while((i = is.read()) != -1) content.append((char) i);
                if (response.getFirstHeader("Location") != null) {
                    link = response.getFirstHeader("Location").getValue().replace("code", "token"); // Глюк Вконтакте
                } else {
                    link = content.toString().split("<form method=\"post\" action=\"")[1].split("\"")[0].replace("code", "token"); // Глюк Вконтакте
                }
                content = new StringBuffer();
                get.releaseConnection();

                // Шаг 4
                get = new HttpGet(link);
                response = client.execute(get);
                get.releaseConnection();
                ACCESS_TOKEN = response.getFirstHeader("Location").getValue().split("access_token=")[1].split("&")[0];
            } catch(Exception ex) {
                ex.printStackTrace();
            }
        }
        return ACCESS_TOKEN;
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
           /* X509TrustManager dontCareTrustManager = new X509TrustManager() {
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
