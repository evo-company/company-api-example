import java.net.*;
import java.io.*;
import java.util.Arrays;


public class EvoClientExample {

    final private String token = "";  // Your authorization token
    final private String host = "my.prom.ua";  // e.g.: my.prom.ua, my.tiu.ru, my.satu.kz, my.deal.by, my.prom.md

    public static void main(String[] args) throws Exception {
        EvoClientExample client = new EvoClientExample();

        System.out.println(client.getOrderList());

        String status = "";  // new order status e.g. "pending"
        Integer[] orderIds = {};  // list of order ids
        System.out.println(client.setOrderStatus(orderIds, status));

        Integer orderId = 0;  // your real order id here
        System.out.println(client.getOrderById(orderId));

    }

    public String getOrderList() throws Exception {
        String url = "/api/v1/orders/list";
        return this.sendGet(url);
    }

    public String getOrderById(Integer id) throws Exception {
        String url = String.format("/api/v1/orders/%s", id);
        return this.sendGet(url);
    }

    public String setOrderStatus(Integer[] orderIds, String status) throws Exception {
        String url = "/api/v1/orders/set_status";

        // Formatting given order ids to string such as "[1, 2, 3]"
        String preparedIds = String.format(
            "[%s]", String.join(",", Arrays.toString(orderIds))
        );

        // Creating string which represents json data
        String outputTemplate = "{\"ids\": %s,\"status\":\"%s\"}";
        String out = String.format(outputTemplate, preparedIds, status);
        return this.sendPost(url, out);
    }

    // Set order status if cancellationReason parameter is specified
    public String setOrderStatus(Integer[] orderIds, String status, String cancellationReason) throws Exception {
        String url = "/api/v1/orders/set_status";

        String preparedIds = String.format(
            "[%s]", String.join(",", Arrays.toString(orderIds))
        );

        String outputTemplate = "{\"ids\": %s,\"status\":\"%s\", \"cancellation_reason\": %s}";
        String out = String.format(outputTemplate, preparedIds, status, cancellationReason);
        return this.sendPost(url, out);
    }

    // Set order status if cancellationReason and cancellationText parameters are specified
    public String setOrderStatus(Integer[] orderIds, String status, String cancellationReason, String cancellationText) throws Exception {
        String url = "/api/v1/orders/set_status";

        String preparedIds = String.format(
            "[%s]", String.join(",", Arrays.toString(orderIds))
        );

        String outputTemplate = "{\"ids\": %s,\"status\":\"%s\", \"cancellation_reason\": %s, \"cancellation_text\": %s}";
        String out = String.format(outputTemplate, preparedIds, status, cancellationReason, cancellationText);
        return this.sendPost(url, out);
    }

    private String sendGet(String path) throws Exception {
        URL url = new URL(String.format("http://%s%s", host, path));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");
        connection.setRequestProperty(
            "Authorization", String.format("Bearer %s", token)
        );

        BufferedReader in = new BufferedReader(
            new InputStreamReader(connection.getInputStream())
        );

        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null)
            response.append(inputLine);
        in.close();

        return response.toString();
    }

    private String sendPost(String path, String data) throws Exception {
        URL url = new URL(String.format("http://%s%s", host, path));
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("POST");
        connection.setDoOutput(true);
        connection.setRequestProperty(
            "Authorization", String.format("Bearer %s", token)
        );
        connection.setRequestProperty(
            "Content-Type", "application/json; charset=UTF-8"
        );

        OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
        writer.write(data);
        writer.close();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        return response.toString();
    }

}
