package us.ihmc.slack.datastructures;

import java.net.URI;
import java.util.List;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AuthenticationResponse
{
   public List<User> users;
   public List<Channel> channels;
   public List<Group> groups;
   public URI url;
   public boolean ok;
   public User self;
}
