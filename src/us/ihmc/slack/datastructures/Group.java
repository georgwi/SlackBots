package us.ihmc.slack.datastructures;

import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Group
{
   public String id;
   public String name;
   public boolean is_group;
   public List<String> members;
}
