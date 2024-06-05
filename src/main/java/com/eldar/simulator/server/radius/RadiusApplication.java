//package com.eldar.simulator.server.radius;
//
//import com.eldar.simulator.server.radius.utils.ServerUtils;
//import com.eldar.simulator.server.radius.utils.config.PropertiesBeanApp;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.Socket;
//import java.net.SocketTimeoutException;
//import java.util.HashMap;
//import java.util.concurrent.atomic.AtomicInteger;
//
//@SpringBootApplication
//public class RadiusApplication {
//    private static final Logger logger = LoggerFactory.getLogger(RadiusApplication.class);
//
//    private static PropertiesBeanApp propertiesBeanApp;
//    //    private static int threadCount = 0; // Contador de hilos
//    private static final AtomicInteger threadCount = new AtomicInteger(0); // Contador de hilos
//
//    @Autowired
//    public RadiusApplication(PropertiesBeanApp propertiesBean) {
//        propertiesBeanApp = propertiesBean;
//    }
//
//    private HashMap<String, String> userDatabase;
//
//    public RadiusApplication() {
//        userDatabase = new HashMap<>();
//        userDatabase.put("user1", "pass1");
//        userDatabase.put("user2", "pass2");
//    }
//
//    public boolean validateUser(String username, String password) {
//        String storedPassword = userDatabase.get(username);
//        return storedPassword != null && storedPassword.equals(password);
//    }
//
//    public static void main(String[] args) {
//        SpringApplication.run(RadiusApplication.class, args);
//
//        int port = Integer.parseInt(propertiesBeanApp.getPort());
//
//        ServerUtils.showServerInfo(String.valueOf(port), propertiesBeanApp.getEnvironment());
//        ServerUtils.showWaitingTime(propertiesBeanApp.getWaitTimeout());
//
//        try (DatagramSocket serverSocket = new DatagramSocket(port)) {
//            logger.info("\u001B[36mIniciado el servidor UDP\u001B[0m");
//
//            // Loop principal que se mantiene escuchando por paquetes
//            while (true) {
//                byte[] receiveData = new byte[1024];
//                DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
//                serverSocket.receive(receivePacket);
//                String request = new String(receivePacket.getData(), 0, receivePacket.getLength());
//                Thread thread = new Thread(new ClientHandler(receivePacket));
//                threadCount.incrementAndGet();
//                thread.start();
//            }
//        } catch (IOException ex) {
//            logger.error("\u001B[36mError al iniciar el servidor UDP\u001B[0m", ex);
//        }
//    }
//
//    // Clase interna para manejar la solicitud de un cliente en un hilo separado
//    private record ClientHandler(DatagramPacket receivePacket) implements Runnable {
//        static String status = "";
//
//        @Override
//        public void run() {
//            try {
//
//                ServerUtils.showOpenedSocketThreadInfo(receivePacket, threadCount);
//                String requestIP = receivePacket.getAddress().getHostAddress();
//                String requestData = new String(receivePacket.getData(), 0, receivePacket.getLength());
//
//                // Crear la respuesta
//                String response = "¡Server Response To " + requestIP;
//
//                System.out.println("Request: " + requestData);
//                System.out.println("Response = " + response);
//
//                // Enviar la respuesta de vuelta al cliente
//                try (DatagramSocket serverSocket = new DatagramSocket()) {
//                    byte[] sendData = response.getBytes();
//                    DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort());
//                    serverSocket.send(sendPacket); // Enviar respuesta al cliente
//
//
//                } catch (SocketTimeoutException e) {
//                    status = "NODATA";
//                } catch (IOException e) {
//                    logger.error("\u001B[36mError al manejar la conexión del cliente\u001B[0m", e);
//                }
//            } finally {
//                threadCount.decrementAndGet();
////                ServerUtils.showClosedSocketThreadInfo(clientSocket,threadCount,request, String.valueOf(responseMessage),status);
//            }
//        }
//    }
//}


package com.eldar.simulator.server.radius;

import com.eldar.simulator.server.radius.utils.ServerUtils;
import com.eldar.simulator.server.radius.utils.config.PropertiesBeanApp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RadiusApplication {

    private static PropertiesBeanApp propertiesBeanApp;
    @Autowired
    public RadiusApplication(PropertiesBeanApp propertiesBean) {
        propertiesBeanApp = propertiesBean;
    }

    public static void main(String[] args) {
        SpringApplication.run(RadiusApplication.class, args);

        String port = propertiesBeanApp.getPort();
        String environment = propertiesBeanApp.getEnvironment();
        ServerUtils.showServerInfo(port, environment);

        new Thread(() -> {
            Server server = new Server();
            server.startServer(port);
        }).start();
    }
}