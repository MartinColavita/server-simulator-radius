package com.eldar.simulator.server.radius;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.eldar.simulator.server.radius.utils.ServerUtils;

public class Server {
    private final HashMap<String, String> userDatabase;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Server() {
        userDatabase = new HashMap<>();
        userDatabase.put("user1", "pass1");
        userDatabase.put("user2", "pass2");
    }

    public boolean validateUser(String username, String password) {
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public void startServer(String port) {
        try (DatagramSocket socket = new DatagramSocket(Integer.parseInt(port))) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);
                String request = new String(packet.getData(), 0, packet.getLength());
//                logger.warn(Arrays.toString(packet.getData()));
//                logger.warn(String.valueOf(packet.getLength()));

                String[] parts = request.split(":");
                logger.warn("\u001B[36m-------------------------------------------\u001B[0m");
                logger.warn("\u001B[33mLogin Validation Request : \u001B[36m {}\u001B[0m", parts.length);
//                logger.warn("\u001B[33mLogin Validation Request : \u001B[36m {}\u001B[0m", parts.length);
                if (parts.length == 4) {
                    String username = parts[0];
                    String password = parts[1];
                    String ip = parts[2];
                    String puerto = parts[3];
                    System.out.println(username+":"+password+":"+ip+":"+puerto);

                    boolean isValid = validateUser(username, password);
//                    String response = isValid ? "\u001B[32mAccess-Accept\u001B[0m" : "\u001B[31mAccess-Reject\u001B[0m" ;
                    String response = isValid ? "Access-Accept" : "Access-Reject" ;
                    DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                    logger.warn("\u001B[33m     id_UserName >>> \u001B[36m {}\u001B[0m", username);
                    logger.warn("\u001B[33m id_UserPassword >>> \u001B[36m {}\u001B[0m", password);
                    logger.warn("\u001B[33m id_NasIpAddress >>> \u001B[36m {}\u001B[0m", ip);
                    logger.warn("\u001B[33m      id_NasPort >>> \u001B[36m {}\u001B[0m", puerto);
//                    logger.warn("\u001B[33mRespuesta enviada a {}:{} -  {}", packet.getAddress(), packet.getPort(), response);

                    //coloreo
                    String respuesta = response.equals("Access-Accept") ? "\u001B[32m" + response : "\u001B[31m" + response;
                    logger.info("RADIUS Validation: " + respuesta + "\u001B[0m");



                    //TODO : Podria grabarse en la DB y validar seguridad, tipo. multiples operaciones desde la misma ip , equipo o lo que sea
                } else {
                    logger.warn("Solicitud mal formada.");
                }
            }
        } catch (Exception e) {
            logger.error("Error en el servidor RADIUS: {}", e.getMessage());
        }



    }


}
