package com.eldar.simulator.server.radius;

import com.eldar.simulator.server.radius.utils.ServerUtils;
import com.eldar.simulator.server.radius.utils.config.PropertiesBeanApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.HashMap;

@SpringBootApplication
public class RadiusApplication {
    private static final Logger logger = LoggerFactory.getLogger(RadiusApplication.class);

    private static PropertiesBeanApp propertiesBeanApp;

    @Autowired
    public RadiusApplication(PropertiesBeanApp propertiesBean) {
        propertiesBeanApp = propertiesBean;
    }

    private HashMap<String, String> userDatabase;

    public RadiusApplication() {
        userDatabase = new HashMap<>();
        userDatabase.put("user1", "pass1");
        userDatabase.put("user2", "pass2");
    }

    public boolean validateUser(String username, String password) {
        String storedPassword = userDatabase.get(username);
        return storedPassword != null && storedPassword.equals(password);
    }

    public static void main(String[] args) {
        SpringApplication.run(RadiusApplication.class, args);

        int port = Integer.parseInt(propertiesBeanApp.getPort());

        ServerUtils.showServerInfo(String.valueOf(port), propertiesBeanApp.getEnvironment());
        ServerUtils.showWaitingTime(propertiesBeanApp.getWaitTimeout());

        // Crear un DatagramSocket para escuchar en el puerto especificado
        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
            logger.info("\u001B[36mIniciado el servidor UDP\u001B[0m");

            // Loop principal que se mantiene escuchando por paquetes
            while (true) {
                byte[] receiveData = new byte[1024];
                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);

                // Esperar a recibir un paquete
                serverSocket.receive(receivePacket);


                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println(receivePacket);
                System.out.println(request);

                // Crear un nuevo hilo para manejar la solicitud recibida
                Thread thread = new Thread(new ClientHandler(receivePacket));
                thread.start();
            }
        } catch (IOException ex) {
            logger.error("\u001B[36mError al iniciar el servidor UDP\u001B[0m", ex);
        }
    }

    // Clase interna para manejar la solicitud de un cliente en un hilo separado
    private static class ClientHandler implements Runnable {
        private final DatagramPacket receivePacket;

        public ClientHandler(DatagramPacket receivePacket) {
            this.receivePacket = receivePacket;
        }

        @Override
        public void run() {
            // Obtener la solicitud del cliente
            String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
            // Crear la respuesta
            String response = "¡Server Response To " + receivePacket.getAddress().getHostAddress();

            // Enviar la respuesta de vuelta al cliente
            try (DatagramSocket serverSocket = new DatagramSocket()) {
                byte[] sendData = response.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
                serverSocket.send(sendPacket); // Enviar respuesta al cliente
            } catch (IOException ex) {
                logger.error("\u001B[36mError al manejar la conexión del cliente\u001B[0m", ex);
            }
        }
    }
}
