package com.eldar.simulator.server.radius.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.net.InetAddress;
import java.net.Socket;

public class ServerUtils {

    private static final Logger logger = LoggerFactory.getLogger(ServerUtils.class);

    public static void showServerInfo(String port, String environment) {
        logger.warn("\u001B[36m-----------------------------------------------------\u001B[0m");
        logger.warn("\u001B[36m                     Server RADIUS                   \u001B[0m");
        logger.warn("\u001B[36m-----------------------------------------------------\u001B[0m");
        logger.info("Deployment Environment: {}", environment);
        logger.info("Listening on Port: {}", port);
        logger.warn("\u001B[36m-----------------------------------------------------\u001B[0m");
    }

    public static void showWaitingTime(String waitTimeout) {
        logger.warn("\u001B[33m-----------------------------------------------------\u001B[0m");
        logger.info("Tiempo de espera para sockets: {} Milisegundos.", waitTimeout);
        logger.info("    ===> Arribo de datos, proceso interno, respuesta");
        logger.warn("\u001B[33m-----------------------------------------------------\u001B[0m");
    }

    public static void showSocketClosed(String ip) {
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
        for (ThreadInfo threadInfo : threadInfos) {
            if (Thread.currentThread().getId() == threadInfo.getThreadId()) {
                String message = "\u001B[31m " +
                        "  Thread Name: " + threadInfo.getThreadName() +
                        " - ID: " + threadInfo.getThreadId();
                logger.info("Socket Closed Correctly for IP {} in thread {} ({}) was closed succeddfully", ip, threadInfo.getThreadName(),threadInfo.getThreadId());
                break;
            }
        }
    }

    public static void showOpenedSocketThreadInfo(Socket clientSocket, int threadCount){
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);

        InetAddress clientAddress = clientSocket.getInetAddress();
        String ipClient=clientAddress.getHostAddress();
        String socketPortClient= String.valueOf(clientSocket.getPort());

        for (ThreadInfo threadInfo : threadInfos) {
            if (Thread.currentThread().getId() == threadInfo.getThreadId()) {
                logger.info("\u001B[36m [O] OPENED <Socket/IP> {}/{} --- <Thread> ( Name: {} - ID: {} - State: {} ) --- <Open Threads> {} \u001b[0m",socketPortClient, ipClient, threadInfo.getThreadName(), threadInfo.getThreadId(), threadInfo.getThreadState(), threadCount);
            }
        }
    }

//    public static void showClosedSocketThreadInfo(InetAddress clientSocket, int threadCount, String request, String response, String estado){
//        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
//        ThreadInfo[] threadInfos = threadMXBean.dumpAllThreads(true, true);
//
//        InetAddress clientAddress = clientSocket.getInetAddress();
//        String ipClient=clientAddress.getHostAddress();
//        String socketPortClient= String.valueOf(clientSocket.getPort());
//
//        for (ThreadInfo threadInfo : threadInfos) {
//            if (Thread.currentThread().getId() == threadInfo.getThreadId()) {
//                switch (estado){
//                    case "NODATA":
//                        logClosedThreadInfoWithoutData(threadInfo, socketPortClient, ipClient, threadCount, request, response);
//                        break;
//                    default:
//                        logClosedThreadInfo(threadInfo, socketPortClient, ipClient, threadCount, request, response);
//                        break;
//                }
//            }
//        }
//    }

    private static void logClosedThreadInfo(ThreadInfo threadInfo, String socketPortClient, String ipClient, int threadCount, String request, String response) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("\u001B[31m [X] CLOSED <Socket/IP> ")
                .append(socketPortClient)
                .append("/")
                .append(ipClient)
                .append(" --- <Thread> ( Name: ")
                .append(threadInfo.getThreadName())
                .append(" - ID: ")
                .append(threadInfo.getThreadId())
                .append(" - State: ")
                .append(threadInfo.getThreadState())
                .append(" ) --- <Open Threads> ")
                .append(threadCount)
                .append(" \u001b[0m")
                .append("\n                                                                                                                                         ")
                .append(" REQUEST: ").append(request)
                .append("\n                                                                                                                                         ")
                .append("RESPONSE: ").append("\u001B[33m"+response+"\u001B[0m");

        logger.info(messageBuilder.toString());
    }
    private static void logClosedThreadInfoWithoutData(ThreadInfo threadInfo, String socketPortClient, String ipClient, int threadCount, String request, String response) {
        StringBuilder messageBuilder = new StringBuilder();
        messageBuilder.append("\u001B[31m [X] CLOSED <Socket/IP> ")
                .append(socketPortClient)
                .append("/")
                .append(ipClient)
                .append(" --- <Thread> ( Name: ")
                .append(threadInfo.getThreadName())
                .append(" - ID: ")
                .append(threadInfo.getThreadId())
                .append(" - State: ")
                .append(threadInfo.getThreadState())
                .append(" ) --- <Open Threads> ")
                .append(threadCount)
                .append(" \u001b[0m")
                .append("\n                                                                                                                                         ")
                .append(" MOTIVO: ").append("Client don't send data - From IP: "+ ipClient+response+"\u001B[0m");

        logger.info(messageBuilder.toString());
    }

    public static void showOpenedSocketThreadInfo(InetAddress clientAddress, int clientPort, int threadCount) {
    }
}