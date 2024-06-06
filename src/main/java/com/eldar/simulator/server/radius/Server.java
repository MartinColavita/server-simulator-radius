package com.eldar.simulator.server.radius;

import java.net.*;
import java.util.Arrays;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Server {
    private final HashMap<String, String> userDatabase;
    private static final Logger logger = LoggerFactory.getLogger(Server.class);


    public Server() {
        userDatabase = new HashMap<>();
        userDatabase.put("HEXA-6X", "01 00 00 3b 7f e6 38 91 f2 f9 24 24 2e 8a 1c 7b 35 29 11 30 01 09 46 42 47 52 46 36 58 02 18 a2 c8 87 b7 3d 5a 61 07 e6 38 f8 5b 0b 80 3c ee 04 06 c0 a8 01 01 05 06 00 00 15 86");
        userDatabase.put("HEXA-7X", "01 00 00 3b 7f e6 38 91 f2 f9 24 24 2e 8a 1c 7b 35 29 11 30 01 09 46 42 47 52 46 37 58 02 18 37 35 95 d0 b7 d9 8d 19 38 08 ae 30 ef 17 8d 36 04 06 c0 a8 01 01 05 06 00 00 15 86");
        userDatabase.put("HEXA-8X", "01 00 00 3b 7f e6 38 91 f2 f9 24 24 2e 8a 1c 7b 35 29 11 30 01 09 46 42 47 52 46 38 58 02 18 00 d0 54 ce 1b fe 6c 09 33 be 41 b6 6a 96 9d 25 04 06 c0 a8 01 01 05 06 00 00 15 86");
    }



    public void startServer(String port) {
        try (DatagramSocket socket = new DatagramSocket(Integer.parseInt(port))) {
            byte[] buffer = new byte[1024];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            while (true) {
                socket.receive(packet);

                // Se recibe el mensaje y se convierte a hexadecimal
                byte[] data = Arrays.copyOf(packet.getData(), packet.getLength());
                String request = bytesToHex(data);
                logger.info("REQUEST recivido by convertido : " + request);

                // Se Valida el mensaje hexadecimal completo con userDatabase
                boolean isValid = validateMessage(request);
                String response = isValid ? "Access-Accept" : "Access-Reject" ;

                // crea un paquete de respuesta y lo envía
                DatagramPacket responsePacket = new DatagramPacket(response.getBytes(), response.getBytes().length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);

                logger.warn("\u001B[33m     Message >>> \u001B[36m {}\u001B[0m", request);

                //coloreo
                String respuesta = response.equals("Access-Accept") ? "\u001B[32m" + response : "\u001B[31m" + response;
                logger.info("RADIUS Validation: " + respuesta + "\u001B[0m");
            }
        } catch (Exception e) {
            logger.error("Error en el servidor RADIUS: {}", e.getMessage());
        }

    }


    /** Método para validar un mensaje hexa con userDatabase */
    public boolean validateMessage(String message) {
        // Normaliza el mensaje (elimina espacios y convierte a mayúsculas)
        message = message.replaceAll("\\s", "").toUpperCase();

        // Busca el mensaje en la base de datos
        for (String storedMessage : userDatabase.values()) {
            // Normaliza el mensaje almacenado
            storedMessage = storedMessage.replaceAll("\\s", "").toUpperCase();

            if (message.equals(storedMessage)) {
                return true;
            }
        }
        return false;
    }


    /** Método para validar un usuario y contraseña*/
        public boolean validateUser(String username, String password) {
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equalsIgnoreCase(password);
    }


    /** Método para convertir una cadena hexadecimal a string */
    private String hexToString(String hex) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < hex.length(); i += 2) {
            String hexChar = hex.substring(i, i + 2);
            int charCode = Integer.parseInt(hexChar, 16);
            result.append((char) charCode);
        }
        return result.toString();
    }


    /** Método para convertir un array de bytes a una cadena hexadecimal */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X ", b));
        }
        return sb.toString();
    }




}
