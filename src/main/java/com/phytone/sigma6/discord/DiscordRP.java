package com.phytone.sigma6.discord;

import com.google.gson.JsonObject;
import com.jagrosh.discordipc.IPCClient;
import com.jagrosh.discordipc.IPCListener;
import com.jagrosh.discordipc.entities.ActivityType;
import com.jagrosh.discordipc.entities.Packet;
import com.jagrosh.discordipc.entities.RichPresence;
import com.jagrosh.discordipc.entities.User;

public class DiscordRP {

    private static IPCClient client;

    public static void init() {
        client = new IPCClient(1575829316615405580L);
        client.setListener(new IPCListener() {

            @Override
            public void onReady(IPCClient client) {
                client.sendRichPresence(
                        new RichPresence.Builder()
                                .setState("Version 26.3")
                                .setDetails("Main Menu")
                                .setActivityType(ActivityType.Playing)
                                .setLargeImage("big")
                                .build()
                );

                System.out.println("DiscordRP Connected! " + client.getCurrentUser().getName());
            }

            // Required by IPCClient Library
            @Override
            public void onPacketSent(IPCClient client, Packet packet) {

            }
            // Required by IPCClient Library
            @Override
            public void onPacketReceived(IPCClient client, Packet packet) {

            }
            // Required by IPCClient Library
            @Override
            public void onActivityJoin(IPCClient client, String secret) {

            }
            // Required by IPCClient Library
            @Override
            public void onActivitySpectate(IPCClient client, String secret) {

            }
            // Required by IPCClient Library
            @Override
            public void onActivityJoinRequest(IPCClient client, String secret, User user) {

            }
            // Required by IPCClient Library
            @Override
            public void onDisconnect(IPCClient client, Throwable t) {
                System.out.println("DiscordRP Disconnected!");
                if (t != null) {
                    t.printStackTrace();
                }
            }
            // Required by IPCClient Library
            @Override
            public void onClose(IPCClient client, JsonObject json) {
            }
        });

        try {
            client.connect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void shutdown() {
        if (client != null) {
            client.close();
        }
        System.out.println("DiscordRP Shutdown!");
    }

    public static void updatePresence(String details) {
        if (client == null) return;

        client.sendRichPresence(
                new RichPresence.Builder()
                        .setDetails(details)
                        .setActivityType(ActivityType.Playing)
                        .setLargeImage("big")
                        .build()
        );
    }
}
