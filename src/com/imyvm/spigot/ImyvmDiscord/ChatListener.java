package com.imyvm.spigot.ImyvmDiscord;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.*;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ChatListener implements Listener {

    final private ImyvmDiscord plugin;

    public ChatListener(ImyvmDiscord pl) {
        plugin = pl;
        plugin.getServer().getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event){
        FileConfiguration config = plugin.getConfig();
        String prefix = config.getString("Prefix");
        String token = config.getString("Token-ID");
        boolean opOverride = config.getBoolean("OpOverride");
        List<String> boldCommands = config.getStringList("BoldCommands");
        List<String> ignoreCommands = config.getStringList("IgnoreCommands");
        boolean enbaleProxy = config.getBoolean("enbaleProxy");
        String PROXY_ADDRESS = config.getString("PROXY_ADDRESS");
        int PORT = config.getInt("PORT");

        String commands = event.getMessage();
        if (event.isCancelled()){
            return;
        }
        if (event.getPlayer().isOp() && opOverride){
            return;
        }
        if (listContain(ignoreCommands, commands)){
            return;
        }
        if (listContain(boldCommands, commands)){
            commands = "**"+commands+"**";
        }
        String message = prefix+" "+event.getPlayer().getName()+": "+commands;
        CompletableFuture.runAsync(() -> { sendMessage(token, message, PROXY_ADDRESS, PORT, enbaleProxy); });
    }

    private static void sendMessage(String webhook_url, String content, String PROXY_ADDRESS, int PORT, boolean enbaleProxy) {

        RequestConfig defaultRequestConfig = RequestConfig.custom()
                .setSocketTimeout(5000)
                .setConnectTimeout(5000)
                .setConnectionRequestTimeout(5000)
                .build();

        CloseableHttpClient httpClient = HttpClientBuilder.create().build();
        HttpResponse response;
        HttpPost request = new HttpPost(webhook_url);
        request.addHeader("Content-Type", "application/json");
        String jsonMessage = "{\"content\": \"" + content + "\"}";
        try {
            StringEntity params = new StringEntity(jsonMessage, java.nio.charset.Charset.forName("UTF-8"));
            request.setEntity(params);
            if (enbaleProxy){
                RequestConfig requestConfig = RequestConfig.copy(defaultRequestConfig)
                        .setProxy(new HttpHost(PROXY_ADDRESS, PORT))
                        .build();
                request.setConfig(requestConfig);
            }
            response = httpClient.execute(request);
        }catch (IOException ex) {
            ex.printStackTrace();
            return;
        }
        if (!(response.getStatusLine().getStatusCode()==204)){
            System.out.println(response.getStatusLine());
        }
    }

    private boolean listContain(List<String> commandsList, String commands){
        if (commandsList.isEmpty()){
            return false;
        }
        if (!commands.contains(" ")){
            return commandsList.contains(commands);
        }else {
            return commandsList.contains(commands.substring(0, commands.indexOf(" ")));
        }
    }
}
