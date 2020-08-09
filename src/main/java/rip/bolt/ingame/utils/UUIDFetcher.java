package rip.bolt.ingame.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.util.UUIDTypeAdapter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

public class UUIDFetcher {
    private static ExecutorService pool = Executors.newCachedThreadPool();
    private static final String UUID_URL = "https://api.mojang.com/users/profiles/minecraft/%s?at=%d";
    private static Map<String, UUID> uuidCache = new HashMap<String, UUID>();
    private static Gson gson = new GsonBuilder().registerTypeAdapter(UUID.class, new UUIDTypeAdapter()).create();

    private UUID id;

    public static void getUUID(String name, Consumer<UUID> action) {
        pool.execute(() -> action.accept(getUUID(name)));
    }

    public static UUID getUUID(String name) {
        name = name.toLowerCase();
        if (uuidCache.containsKey(name)) {
            return uuidCache.get(name);
        }
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(String.format(UUID_URL, name, System.currentTimeMillis()/1000)).openConnection();
            connection.setReadTimeout(5000);
            UUIDFetcher data = gson.fromJson(new BufferedReader(new InputStreamReader(connection.getInputStream())), UUIDFetcher.class);

            if(data != null) {
                uuidCache.put(name, data.id);
            }
            return data.id;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
