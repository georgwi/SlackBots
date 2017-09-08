package us.ihmc.slack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.websocket.CloseReason;
import javax.websocket.DeploymentException;
import javax.websocket.Endpoint;
import javax.websocket.EndpointConfig;
import javax.websocket.MessageHandler;
import javax.websocket.Session;

import org.apache.http.HttpResponse;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONException;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import us.ihmc.commons.PrintTools;
import us.ihmc.slack.datastructures.AuthenticationResponse;

public class SlackClient
{
   private static final String SLACK_API_HTTPS_ROOT = "https://slack.com/api/";
   private static final String SLACK_HTTPS_AUTH_URL = "https://slack.com/api/rtm.start?token=";
   private final String token;

   public SlackClient(String token) throws IOException, JSONException, InterruptedException, DeploymentException
   {
      this.token = token;

      CloseableHttpClient httpClient = HttpClientBuilder.create().build();
      HttpGet request = new HttpGet(SLACK_HTTPS_AUTH_URL + token);
      HttpResponse response = httpClient.execute(request);

      ObjectMapper mapper = new ObjectMapper();
      AuthenticationResponse message = mapper.readValue(response.getEntity().getContent(), AuthenticationResponse.class);

      PrintTools.info("Connected to Slack as " + message.self.name);

//      WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//      SlackEndpoint endpoint = new SlackEndpoint();
//      Session session = container.connectToServer(endpoint, message.getUrl());
//
//      ThreadTools.sleep(100000);
//
//      session.close(new CloseReason(CloseCodes.NORMAL_CLOSURE, "Client done."));
   }

   public static void main(String[] args) throws IOException, JSONException, InterruptedException, DeploymentException
   {
      String token = System.getProperty("token");
      new SlackClient(token);
   }

   private class SlackEndpoint extends Endpoint
   {
      @Override
      public void onOpen(Session session, EndpointConfig config)
      {
         PrintTools.info("Opened.");
         session.addMessageHandler(new SlackMessageHandler());
      }

      @Override
      public void onClose(Session session, CloseReason closeReason)
      {
         PrintTools.info("Closed: " + closeReason.getReasonPhrase());
      }
   }

   private class SlackMessageHandler implements MessageHandler.Whole<String>
   {
      @Override
      public void onMessage(String message)
      {
         JsonParser jsonParser = new JsonParser();
         JsonElement parsedMessage = jsonParser.parse(message);

         Content content = new Content();
         packRecursive(parsedMessage, "", content);

         if ("RightGIF (bot)".equals(content.subtitle))
         {
            HttpPost request = new HttpPost(SLACK_API_HTTPS_ROOT + "chat.postMessage");
            List<BasicNameValuePair> parameters = new ArrayList<>();
            parameters.add(new BasicNameValuePair("token", token));
            parameters.add(new BasicNameValuePair("channel", "D1KKXV1C5"));
            parameters.add(new BasicNameValuePair("text", "You can have this back\n" + content.content));

            CloseableHttpClient httpClient = HttpClientBuilder.create().build();

            try
            {
               request.setEntity(new UrlEncodedFormEntity(parameters, "UTF-8"));
               httpClient.execute(request);
               httpClient.close();
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }
      }

      private void packRecursive(JsonElement element, String key, Content content)
      {
         if (element.isJsonObject())
         {
            element.getAsJsonObject().entrySet().forEach(e -> packRecursive(e.getValue(), e.getKey(), content));
         }
         else if (element.isJsonPrimitive())
         {
            if ("text".equals(key))
            {
               content.text = element.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", "");
            }
            if ("content".equals(key))
            {
               content.content = element.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", "");
            }
            if ("bot_id".equals(key))
            {
               content.bot_id = element.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", "");
            }
            if ("subtitle".equals(key))
            {
               content.subtitle = element.getAsJsonPrimitive().toString().replaceAll("^\"|\"$", "");
            }
         }
      }
   }

   private class Content
   {
      public String text = null;
      public String bot_id = null;
      public String content = null;
      public String subtitle = null;
   }
}
