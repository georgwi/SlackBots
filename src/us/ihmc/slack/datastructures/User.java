package us.ihmc.slack.datastructures;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class User
{
   public String id;
   public String name;
   public boolean deleted;
   public String real_name;
   public Profile profile;
   public boolean is_admin;
   public boolean is_owner;
   public boolean is_primary_owner;
   public boolean is_restricted;
   public boolean is_ultra_restricted;
   public boolean is_bot;
}
