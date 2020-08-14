package dev.pgm.events.api;

import dev.pgm.events.config.AppData;
import java.io.IOException;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

public class AuthorisationHeaderFilter implements ClientRequestFilter {

  @Override
  public void filter(ClientRequestContext requestContext) throws IOException {
    if (!String.valueOf(AppData.API.getKey()).equals("null"))
      requestContext.getHeaders().add("Authorization", "Bearer " + AppData.API.getKey());
  }
}
