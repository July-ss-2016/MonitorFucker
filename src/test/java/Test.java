import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class Test {
    private static OkHttpClient client = new OkHttpClient().newBuilder().readTimeout(100, TimeUnit.MILLISECONDS).connectTimeout(100, TimeUnit.MILLISECONDS).build();

    public static void main(String[] args) {

        Request request = new Request.Builder()
                .url("http://" + "10.55.249.249" + "/doc/page/login.asp")
                .addHeader("user-agent", "Chrome/69.0.3497.100 Safari/537.36")
                .addHeader("Cookie", "language=zh; updateTips=true")
                .build();

        try {
            Response response = client.newCall(request).execute();

            System.out.println(response.body().string());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
