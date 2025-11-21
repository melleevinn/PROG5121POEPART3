package assignment2.prog5121;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.json.simple.parser.ParseException;

/**
 * MessageManager handles arrays, storage, JSON, searches, deletion and report printing.
 */
public final class MessageManager {

    private final List<Message> sentMessages = new ArrayList<>();
    private final List<Message> disregardedMessages = new ArrayList<>();
    private final List<Message> storedMessages = new ArrayList<>();
    private final List<String> messageHashes = new ArrayList<>();
    private final List<String> messageIDs = new ArrayList<>();

    // resource file for stored messages (relative to project)
    private final String storedJsonPath = "src/main/resources/stored_messages.json";

    public MessageManager() {
        // load stored messages if present
        loadStoredMessagesFromJSON();
    }

    /**
     * Add a message and allocate to the right list according to flag.
     * @param m
     * @param flag
     */
    public void addMessage(Message m, String flag) {
        m.setFlag(flag);
        m.setMessageHash(m.createMessageHash());
        messageHashes.add(m.getMessageHash());
        messageIDs.add(m.getMessageID());

        switch (flag) {
            case "Sent" -> sentMessages.add(m);
            case "Stored" -> storedMessages.add(m);
            case "Disregarded" -> disregardedMessages.add(m);
        }
    }

    public List<Message> getSentMessages() { return sentMessages; }
    public List<Message> getStoredMessages() { return storedMessages; }
    public List<Message> getDisregardedMessages() { return disregardedMessages; }
    public List<String> getMessageHashes() { return messageHashes; }
    public List<String> getMessageIDs() { return messageIDs; }

    /**
     * Return total number of messages sent (Sent list size)
     * @return 
     */
    public int returnTotalMessages() {
        return sentMessages.size();
    }

    /**
     * Return longest sent message text (null safe)
     * @return 
     */
    public String getLongestSentMessage() {
        String longest = "";
        for (Message m : sentMessages) {
            if (m.getText() != null && m.getText().length() > longest.length()) {
                longest = m.getText();
            }
        }
        return longest;
    }

    /**
     * Search by Message ID, return recipient + message or null.
     * @param id
     * @return 
     */
    public String searchByMessageID(String id) {
        for (Message m : sentMessages) {
            if (m.getMessageID().equals(id)) {
                return m.getRecipient() + " - " + m.getText();
            }
        }
        // also search stored/disregarded for completeness
        for (Message m : storedMessages) if (m.getMessageID().equals(id)) return m.getRecipient() + " - " + m.getText();
        for (Message m : disregardedMessages) if (m.getMessageID().equals(id)) return m.getRecipient() + " - " + m.getText();
        return null;
    }

    /**
     * Search for all messages to a recipient across sent & stored.
     * @param recipient
     * @return 
     */
    public List<Message> searchByRecipient(String recipient) {
        List<Message> results = new ArrayList<>();
        for (Message m : sentMessages) if (m.getRecipient().equals(recipient)) results.add(m);
        for (Message m : storedMessages) if (m.getRecipient().equals(recipient)) results.add(m);
        return results;
    }

    /**
     * Delete a message using the message hash from any array; returns message confirming deletion.
     * @param hash
     * @return 
     */
    public String deleteByHash(String hash) {
        if (hash == null) return "No hash provided.";
        // sent
        Message removed = removeFromListByHash(sentMessages, hash);
        if (removed != null) return "Message \"" + removed.getText() + "\" successfully deleted.";
        removed = removeFromListByHash(storedMessages, hash);
        if (removed != null) return "Message \"" + removed.getText() + "\" successfully deleted.";
        removed = removeFromListByHash(disregardedMessages, hash);
        if (removed != null) return "Message \"" + removed.getText() + "\" successfully deleted.";
        return "Message not found.";
    }

    private Message removeFromListByHash(List<Message> list, String hash) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMessageHash().equals(hash)) {
                return list.remove(i);
            }
        }
        return null;
    }

    /**
     * Print a report string of sent messages (ID, Hash, Recipient, Message)
     * @return 
     */
    public String printSentReport() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append("MessageID: ").append(m.getMessageID()).append("\n")
              .append("MessageHash: ").append(m.getMessageHash()).append("\n")
              .append("Recipient: ").append(m.getRecipient()).append("\n")
              .append("Message: ").append(m.getText()).append("\n\n");
        }
        return sb.toString();
    }

    /**
     * Save stored messages (storedMessages list) to JSON (overwrite).
     * Attribution: JSON helper code created with assistance from ChatGPT (OpenAI, 2025).
     */
    @SuppressWarnings("unchecked")
    public void saveStoredMessagesToJSON() {
        try {
            JSONArray arr = new JSONArray();
            for (Message m : storedMessages) {
                JSONObject obj = new JSONObject();
                obj.put("messageID", m.getMessageID());
                obj.put("messageNumber", m.getMessageNumber());
                obj.put("recipient", m.getRecipient());
                obj.put("text", m.getText());
                obj.put("messageHash", m.getMessageHash());
                obj.put("flag", m.getFlag());
                arr.add(obj);
            }
            File file = new File(storedJsonPath);
            file.getParentFile().mkdirs();
            try (FileWriter fw = new FileWriter(file)) {
                fw.write(arr.toJSONString());
            }
        } catch (IOException e) {
            // Log or ignore for simplicity in POE
            System.err.println("Error saving stored messages: " + e.getMessage());
        }
    }

    /**
     * Load stored messages back into storedMessages list (if JSON exists).
     */
    public void loadStoredMessagesFromJSON() {
        try {
            File f = new File(storedJsonPath);
            if (!f.exists()) return;
            JSONParser parser = new JSONParser();
            try (FileReader fr = new FileReader(f)) {
                Object obj = parser.parse(fr);
                if (obj instanceof JSONArray arr) {
                    for (Object o : arr) {
                        if (!(o instanceof JSONObject)) continue;
                        JSONObject jo = (JSONObject) o;
                        String id = (String) jo.get("messageID");
                        long num = (long) jo.getOrDefault("messageNumber", 0L);
                        String recipient = (String) jo.get("recipient");
                        String text = (String) jo.get("text");
                        Message m = new Message(id, (int) num, recipient, text);
                        m.setMessageHash((String) jo.get("messageHash"));
                        m.setFlag((String) jo.get("flag"));
                        storedMessages.add(m);
                        messageIDs.add(m.getMessageID());
                        messageHashes.add(m.getMessageHash());
                    }
                }
            }
        } catch (IOException | ParseException e) {
            System.err.println("Error loading stored messages: " + e.getMessage());
        }
    }

    Message findMessageByID(String id) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    String findMessagesByRecipient(String rec) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    boolean deleteMessageByHash(String hash) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
