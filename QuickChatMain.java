package assignment2.prog5121;

import javax.swing.JOptionPane;
import java.util.UUID;

public class QuickChatMain {

    public static void main(String[] args) {

        Login login = new Login();
        MessageManager manager = new MessageManager();

        // ===========================================
        // ACCOUNT REGISTRATION
        // ===========================================
        int haveAccount = JOptionPane.showConfirmDialog(
                null,
                "Do you have an account?",
                "QuickChat",
                JOptionPane.YES_NO_OPTION
        );

        if (haveAccount == JOptionPane.NO_OPTION) {

            String first = JOptionPane.showInputDialog("Enter first name:");
            String last = JOptionPane.showInputDialog("Enter last name:");
            String username = JOptionPane.showInputDialog("Create username (must contain _ and ≤5 chars):");
            String password = JOptionPane.showInputDialog("Create password (8+ chars, uppercase, number, special):");
            String cell = JOptionPane.showInputDialog("Enter phone number (+27xxxxxxxxx):");

            String reg = login.registerUser(username, password, cell, first, last);
            JOptionPane.showMessageDialog(null, reg);

            if (!reg.equals("User successfully registered.")) {
                JOptionPane.showMessageDialog(null, "Registration failed. Exiting.");
                return;
            }
        }

        // ===========================================
        // LOGIN GATE
        // ===========================================
        String u = JOptionPane.showInputDialog("Enter username:");
        String p = JOptionPane.showInputDialog("Enter password:");

        if (!login.returnLoginStatus(u, p).startsWith("Welcome")) {
            JOptionPane.showMessageDialog(null, "Incorrect login credentials.");
            return;
        }

        JOptionPane.showMessageDialog(null, "Welcome to QuickChat.");

        boolean running = true;

        while (running) {

            String menu = JOptionPane.showInputDialog("""
                    === QUICKCHAT MAIN MENU ===
                    1) Send Messages
                    2) Message Management
                    3) Quit

                    Enter option:
                    """);

            if (menu == null) break;

            switch (menu) {

                case "1" -> sendMessages(manager);

                case "2" -> messageManagementMenu(manager);

                case "3" -> running = false;

                default -> JOptionPane.showMessageDialog(null, "Invalid option.");
            }
        }

        JOptionPane.showMessageDialog(null, "Goodbye.");
        manager.saveStoredMessagesToJSON();
    }


    // ============================================================
    //  SEND MESSAGES MENU
    // ============================================================
    private static void sendMessages(MessageManager manager) {

        String cnt = JOptionPane.showInputDialog("How many messages do you want to enter?");
        if (cnt == null) return;

        int number;
        try {
            number = Integer.parseInt(cnt);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Invalid number.");
            return;
        }

        for (int i = 1; i <= number; i++) {

            String messageID = UUID.randomUUID().toString().replace("-", "").substring(0, 10);
            int msgNum = i;

            String recipient = JOptionPane.showInputDialog("Enter recipient number (+27xxxxxxxxx):");
            String text = JOptionPane.showInputDialog("Enter message (max 250 chars):");

            Message m = new Message(messageID, msgNum, recipient, text);

            if (!m.checkRecipientCell()) {
                JOptionPane.showMessageDialog(null, "Invalid cellphone number.");
                continue;
            }

            String len = m.validateMessageLength();
            if (!len.equals("Message ready to send.")) {
                JOptionPane.showMessageDialog(null, len);
                continue;
            }

            String[] actions = {"Send Message", "Store Message", "Disregard Message"};
            int choice = JOptionPane.showOptionDialog(
                    null,
                    "Choose action:",
                    "Message Action",
                    JOptionPane.DEFAULT_OPTION,
                    JOptionPane.PLAIN_MESSAGE,
                    null,
                    actions,
                    actions[0]
            );

            switch (choice) {

                case 0 -> {
                    manager.addMessage(m, "Sent");
                    JOptionPane.showMessageDialog(null,
                            "Message sent.\n\nID: " + m.getMessageID() +
                                    "\nHash: " + m.getMessageHash() +
                                    "\nRecipient: " + m.getRecipient());
                }

                case 1 -> {
                    manager.addMessage(m, "Stored");
                    manager.saveStoredMessagesToJSON();
                    JOptionPane.showMessageDialog(null, "Message stored.");
                }

                default -> {
                    manager.addMessage(m, "Disregarded");
                    JOptionPane.showMessageDialog(null, "Message disregarded.");
                }
            }
        }

        JOptionPane.showMessageDialog(null,
                "Total messages sent: " + manager.returnTotalMessages());
    }


    // ============================================================
    //  MESSAGE MANAGEMENT MENU
    // ============================================================
    private static void messageManagementMenu(MessageManager manager) {

        boolean open = true;

        while (open) {

            String s = JOptionPane.showInputDialog("""
                    === QUICKCHAT — MESSAGE MANAGEMENT ===
                    1) Display sender & recipient of all sent messages
                    2) Display the longest sent message
                    3) Search for message by ID
                    4) Search messages by recipient
                    5) Delete message using message hash
                    6) Display full message report
                    7) Reload messages (get latest data)
                    8) Exit to main menu

                    Enter option:
                    """);

            if (s == null) return;

            switch (s) {

                case "1" -> {
                    StringBuilder sb = new StringBuilder();
                    for (Message m : manager.getSentMessages()) {
                        sb.append("Sender: ").append("You")
                                .append(" | Recipient: ").append(m.getRecipient())
                                .append("\n");
                    }
                    JOptionPane.showMessageDialog(null, sb.length() == 0 ? "No sent messages." : sb.toString());
                 }

                case "2" -> {
                  String longest = manager.getLongestSentMessage();
                  JOptionPane.showMessageDialog(null,
                  longest == null ? "No messages sent yet." : longest);

                }

                case "3" -> {
                    String id = JOptionPane.showInputDialog("Enter message ID to search:");
                    Message found = manager.findMessageByID(id);
                    JOptionPane.showMessageDialog(
                            null,
                            found == null ? "Message not found." : found.getFullMessage()
                    );
                }

                case "4" -> {
                    String rec = JOptionPane.showInputDialog("Enter recipient number:");
                    String list = manager.findMessagesByRecipient(rec);
                    JOptionPane.showMessageDialog(
                            null,
                            list.isEmpty() ? "No messages for that recipient." : list
                    );
                }

                case "5" -> {
                    String hash = JOptionPane.showInputDialog("Enter message hash to delete:");
                    boolean ok = manager.deleteMessageByHash(hash);
                    JOptionPane.showMessageDialog(null, ok ? "Message deleted." : "Hash not found.");
                }

                case "6" -> {
                    String rep = manager.printSentReport();
                    JOptionPane.showMessageDialog(null, rep.isEmpty() ? "No messages yet." : rep);
                }

                case "7" -> {
                    manager.loadStoredMessagesFromJSON();
                    JOptionPane.showMessageDialog(null, "Messages reloaded.");
                }

                case "8" -> open = false;

                default -> JOptionPane.showMessageDialog(null, "Invalid option.");
            }
        }
    }
}
