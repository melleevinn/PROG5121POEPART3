package assignment2.prog5121;

import java.util.Objects;

/**
 * Message model with helper methods required by the rubric.
 */
public final class Message {
    private String messageID;     // unique ID (string)
    private int messageNumber;    // auto-increment counter
    private String recipient;     // +27...
    private String text;          // message text (<= 250)
    private String messageHash;   // generated hash
    private String flag;          // "Sent", "Stored", "Disregarded"

    public Message() {}

    public Message(String messageID, int messageNumber, String recipient, String text) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.text = text;
        this.messageHash = createMessageHash();
    }

    public boolean checkMessageID() {
        if (messageID == null) return false;
        return messageID.length() <= 10;
    }

    /**
     * Recipient must match +27\d{9}
     * @return 
     */
    public boolean checkRecipientCell() {
        if (recipient == null) return false;
        return recipient.matches("^\\+27\\d{9}$");
    }

    /**
     * Create Message Hash:
     * - first two chars of messageID (if available else "00")
     * - colon
     * - messageNumber
     * - colon
     * - first and last word of message text, joined and uppercased (non-letter trimmed)
     * Example: "00:1:HITHANKS"
     * @return 
     */
    public String createMessageHash() {
        String idPart = (messageID != null && messageID.length() >= 2) ? messageID.substring(0, 2) : "00";
        String numPart = String.valueOf(messageNumber);
        String words = "";
        if (text != null && !text.trim().isEmpty()) {
            String[] parts = text.trim().split("\\s+");
            String first = parts[0].replaceAll("[^A-Za-z0-9]", "");
            String last = parts[parts.length - 1].replaceAll("[^A-Za-z0-9]", "");
            words = (first + last).toUpperCase();
        }
        return idPart + ":" + numPart + ":" + words;
    }

    /**
     * Validate message length. Returns success or error message as required.
     * @return 
     */
    public String validateMessageLength() {
        if (text == null) text = "";
        int limit = 250;
        if (text.length() <= limit) return "Message ready to send.";
        int over = text.length() - limit;
        return "Message exceeds 250 characters by " + over + " please reduce size.";
    }

    // Getters and setters
    public String getMessageID() { return messageID; }
    public void setMessageID(String messageID) { this.messageID = messageID; }

    public int getMessageNumber() { return messageNumber; }
    public void setMessageNumber(int messageNumber) {
        this.messageNumber = messageNumber;
    }

    public String getRecipient() { return recipient; }
    public void setRecipient(String recipient) { this.recipient = recipient; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getMessageHash() { return messageHash; }
    public void setMessageHash(String messageHash) { this.messageHash = messageHash; }

    public String getFlag() { return flag; }
    public void setFlag(String flag) { this.flag = flag; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Message)) return false;
        Message message = (Message) o;
        return messageNumber == message.messageNumber &&
                Objects.equals(messageID, message.messageID);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.messageID);
        hash = 97 * hash + this.messageNumber;
        return hash;
    }

    Object getFullMessage() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
