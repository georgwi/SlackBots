package us.ihmc.slack.datastructures;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Channel
{
   public String id;
   public String name;
   public boolean is_channel;
   public List<String> members;
}
