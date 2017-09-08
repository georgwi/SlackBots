package us.ihmc.slack.datastructures;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Profile
{
   public String avatar_hash;
   public String first_name;
   public String last_name;
   public String real_name;
   public String email;
}
