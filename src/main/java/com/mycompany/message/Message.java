/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.message;

/**
 *
 * @author Student
 */
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Message {

    private String messageID;
    private String recipient;
    private String message;
    private String messageHash;
    private String status;

    private static int totalMessagesSent = 0;
    private static int totalMessagesProcessed = 0;

    private static final SecureRandom random = new SecureRandom();
    private static final Set<String> usedIDs = new HashSet<>();

    public Message(String recipient, String message) {
        this.messageID = generateMessageID();
        this.recipient = recipient;
        this.message = message;
        totalMessagesProcessed++;
        this.messageHash = createMessageHash();
        this.status = "Pending";
    }

    private String generateMessageID() {
        String id;

        do {
            long number = 1000000000L
                    + random.nextLong(9000000000L);
            id = String.valueOf(number);
        } while (!usedIDs.add(id));

        return id;
    }

    public boolean checkMessageID() {
        return messageID != null
                && messageID.matches("\\d{10}");
    }

    public boolean checkRecipientCell() {
        return recipient != null
                && recipient.matches("\\+27\\d{9}");
    }

    public String createMessageHash() {
        String trimmed = message.trim();

        String[] words = trimmed.isEmpty()
                ? new String[0]
                : trimmed.split("\\s+");

        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 0
                ? words[words.length - 1] : "";

        firstWord = firstWord.replaceAll(
                "^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");

        lastWord = lastWord.replaceAll(
                "^[^\\p{L}\\p{N}]+|[^\\p{L}\\p{N}]+$", "");

        return (messageID.substring(0, 2)
                + ":" + totalMessagesProcessed
                + ":" + firstWord + lastWord).toUpperCase();
    }

    public String SentMessage() {
        return status;
    }

    public String printMessages() {
        return "Message ID: " + messageID
                + "\nMessage Hash: " + messageHash
                + "\nRecipient: " + recipient
                + "\nMessage: " + message;
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    public void send() {
        if (!status.equals("Sent")) {
            status = "Sent";
            totalMessagesSent++;
        }
    }

    public void disregard() {
        status = "Disregarded";
    }

    public void store() {
        status = "Stored";
    }

    public String getMessageID() {
        return messageID;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessage() {
        return message;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getStatus() {
        return status;
    }

    public static int getTotalMessagesProcessed() {
        return totalMessagesProcessed;
    }
}





 class QuickChat {

    private static final Scanner input = new Scanner(System.in);
    private static final List<Message> messages = new ArrayList<>();
    private static final Path JSON_FILE = Paths.get("messages.json");

    public static void main(String[] args) {

        System.out.println("================================");
        System.out.println("       QUICKCHAT LOGIN");
        System.out.println("================================");

        String correctUsername = "admin";
        String correctPassword = "1234";

        boolean loggedIn = false;

        for (int attempts = 1; attempts <= 3; attempts++) {

            System.out.print("Enter username: ");
            String username = input.nextLine();

            System.out.print("Enter password: ");
            String password = input.nextLine();

            if (username.equals(correctUsername)
                    && password.equals(correctPassword)) {

                loggedIn = true;
                System.out.println("Login successful!");
                break;

            } else {
                System.out.println("Incorrect login details. Try again.");
            }
        }

        if (!loggedIn) {
            System.out.println("Login failed. Application closing.");
            return;
        }

        System.out.println("\nWelcome to QuickChat.");

        int messageLimit = readMessageLimit();
        int messagesProcessed = 0;
        boolean running = true;

        while (running) {

            System.out.println("\n========== MAIN MENU ==========");
            System.out.println("1. Send Messages");
            System.out.println("2. Show Recently Sent Messages");
            System.out.println("3. Quit");
            System.out.print("Choose an option: ");

            String choice = input.nextLine().trim();

            switch (choice) {

                case "1":

                    if (messagesProcessed >= messageLimit) {
                        System.out.println(
                                "You have reached your message limit.");
                        break;
                    }

                    System.out.println("\nSend Message");

                    System.out.print("Enter recipient number (+27...): ");
                    String recipient = input.nextLine().trim();

                    if (!recipient.matches("\\+27\\d{9}")) {
                        System.out.println(
                                "Invalid recipient number. "
                                + "Use +27 followed by 9 digits.");
                        break;
                    }

                    System.out.print("Enter your message: ");
                    String text = input.nextLine();

                    if (text.trim().isEmpty()) {
                        System.out.println("Message cannot be empty.");
                        break;
                    }

                    if (text.length() > 250) {
                        System.out.println(
                                "Message exceeds 250 characters.");
                        break;
                    }

                    Message newMessage = new Message(recipient, text);

                    System.out.println("\nChoose what to do:");
                    System.out.println("1. Send Message");
                    System.out.println("2. Disregard Message");
                    System.out.println("3. Store Message to send later");
                    System.out.print("Enter your choice: ");

                    String action = input.nextLine().trim();

                    switch (action) {

                        case "1":
                            newMessage.send();
                            messages.add(newMessage);
                            messagesProcessed++;

                            System.out.println("Message successfully sent");
                            System.out.println();
                            System.out.println(newMessage.printMessages());
                            break;

                        case "2":
                            newMessage.disregard();
                            messagesProcessed++;

                            System.out.println(
                                    "Press 0 to delete the message.");

                            String deleteChoice = input.nextLine().trim();

                            if (deleteChoice.equals("0")) {
                                System.out.println("Message deleted.");
                            } else {
                                messages.add(newMessage);
                                System.out.println("Message disregarded.");
                            }
                            break;

                        case "3":
                            newMessage.store();
                            messages.add(newMessage);
                            messagesProcessed++;

                            storeMessage(newMessage);

                            System.out.println("Message successfully stored");
                            System.out.println();
                            System.out.println(newMessage.printMessages());
                            break;

                        default:
                            System.out.println(
                                    "Invalid choice. Message not processed.");
                    }

                    break;

                case "2":
                    System.out.println("Coming Soon.");
                    break;

                case "3":
                    running = false;
                    System.out.println(
                            "Thank you for using QuickChat.");
                    break;

                default:
                    System.out.println(
                            "Invalid option. Please choose 1, 2 or 3.");
            }
        }

        System.out.println("\n========== SUMMARY ==========");
        System.out.println("Total messages sent: "
                + Message.returnTotalMessages());

        System.out.println("Messages processed this session: "
                + messagesProcessed);

        input.close();
    }

    private static int readMessageLimit() {

        while (true) {

            System.out.print(
                    "How many messages would you like to enter? ");

            String value = input.nextLine().trim();

            try {
                int limit = Integer.parseInt(value);

                if (limit >= 0) {
                    return limit;
                }

                System.out.println(
                        "Enter zero or a positive number.");

            } catch (NumberFormatException e) {
                System.out.println(
                        "Enter a valid whole number.");
            }
        }
    }

    public static void storeMessage(Message message) {

        try {
            String jsonObject = "{"
                    + "\"messageID\":\""
                    + escapeJson(message.getMessageID()) + "\","
                    + "\"messageHash\":\""
                    + escapeJson(message.getMessageHash()) + "\","
                    + "\"recipient\":\""
                    + escapeJson(message.getRecipient()) + "\","
                    + "\"message\":\""
                    + escapeJson(message.getMessage()) + "\","
                    + "\"status\":\""
                    + escapeJson(message.getStatus()) + "\""
                    + "}";

            String existingJson = "[]";

            if (Files.exists(JSON_FILE)) {
                existingJson = Files.readString(
                        JSON_FILE, StandardCharsets.UTF_8).trim();
            }

            if (existingJson.isEmpty()) {
                existingJson = "[]";
            }

            if (!existingJson.startsWith("[")
                    || !existingJson.endsWith("]")) {

                System.out.println(
                        "Could not store message: "
                        + "messages.json is not a valid JSON array.");
                return;
            }

            String contents = existingJson.substring(
                    1, existingJson.length() - 1).trim();

            String updatedJson;

            if (contents.isEmpty()) {
                updatedJson = "[\n" + jsonObject + "\n]";
            } else {
                updatedJson = "[\n"
                        + contents + ",\n"
                        + jsonObject + "\n]";
            }

            Files.writeString(
                    JSON_FILE,
                    updatedJson,
                    StandardCharsets.UTF_8);

        } catch (IOException e) {
            System.out.println(
                    "Error storing message: " + e.getMessage());
        }
    }

    private static String escapeJson(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}


    
    

