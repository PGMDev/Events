package rip.bolt.ingame.api;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;

import rip.bolt.ingame.config.AppData;

public class AuthorisationHeaderFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {
        if (!String.valueOf(AppData.API.getKey()).equals("null"))
            requestContext.getHeaders().add("Authorization", "Bearer " + AppData.API.getKey());
    }

}
