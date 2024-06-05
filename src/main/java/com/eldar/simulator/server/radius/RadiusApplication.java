package com.eldar.simulator.server.radius;

import com.eldar.simulator.server.radius.utils.ServerUtils;
import com.eldar.simulator.server.radius.utils.config.PropertiesBeanApp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;

@SpringBootApplication
public class RadiusApplication {
    private static final Logger logger = LoggerFactory.getLogger(RadiusApplication.class);

    private static PropertiesBeanApp propertiesBeanApp;
    private static int threadCount = 0; // Contador de hilos

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

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("\u001B[36mIniciado el servidor TCP\u001B[0m");
            //START: Loop que se mantiene escuchando
            while (true) {
                Socket clientSocket = serverSocket.accept();
                Thread thread = new Thread(new ClientHandler(clientSocket));
                thread.start();
                synchronized (RadiusApplication.class) {
                    threadCount++;
                }
            }
            //END: Loop que se mantiene escuchando
        } catch (IOException ex) {
            logger.error("\u001B[36mError al iniciar el servidor TCP\u001B[0m",ex);
        }
    }

    private static class ClientHandler implements Runnable {
        private final Socket clientSocket;

        public ClientHandler(Socket socket) {
            this.clientSocket = socket;
        }

        @Override
        public void run() {

            StringBuilder responseMessage = new StringBuilder();
            String estado="";
            String request="";

            InetAddress clientAddress = clientSocket.getInetAddress();

            ServerUtils.showOpenedSocketThreadInfo(clientSocket,threadCount);

            try (BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                 PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)) {

                clientSocket.setSoTimeout(Integer.parseInt(propertiesBeanApp.getWaitTimeout()));

                request = in.readLine(); // Leer el mensaje del cliente
                if (request == null) {
                    return;
                }

                // Start: Simulo Tiempo de Espera. Para Responder
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // End: Simulo Tiempo de Espera

                responseMessage.append("¡Server Response To ").append(clientAddress.getHostAddress());
                out.println(responseMessage);
            } catch (SocketTimeoutException e) {
                estado="NODATA";
            } catch (IOException e) {
                logger.error("Error al manejar la conexión del cliente", e);
            } finally {
                try {
                    clientSocket.close();
                } catch (IOException ex) {
                    logger.error("Error al cerrar el socket", ex);
                }
                synchronized (RadiusApplication.class) {
                    threadCount--;
                    ServerUtils.showClosedSocketThreadInfo(clientSocket,threadCount,request, String.valueOf(responseMessage),estado);
                }
            }
        }
    }
}
