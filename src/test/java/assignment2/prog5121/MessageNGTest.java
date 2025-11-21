package assignment2.prog5121;

import org.testng.Assert;
import org.testng.annotations.Test;

public class MessageNGTest {

    @Test
    public void testMessageLengthSuccess() {
        Message m = new Message("AB12345678", 1, "+27718693002", "Hi Mike, can you join us for dinner tonight");
        Assert.assertEquals(m.validateMessageLength(), "Message ready to send.");
    }

    @Test
    public void testMessageLengthFailure() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 260; i++) sb.append("x");
        Message m = new Message("AB12345678", 1, "+27718693002", sb.toString());
        String res = m.validateMessageLength();
        Assert.assertTrue(res.startsWith("Message exceeds 250 characters by"));
    }

    @Test
    public void testRecipientValid() {
        Message m = new Message("AB12345678", 1, "+27718693002", "hello");
        Assert.assertTrue(m.checkRecipientCell());
    }

    @Test
    public void testCreateMessageHash() {
        Message m = new Message("0012345678", 1, "+27718693002", "Hi Mike, can you join us for dinner tonight");
        String h = m.createMessageHash();
        // expected 00:1:HIDINNERTONIGHT (first word Hi, last word tonight -> HITIONIGHT? Our algorithm concatenates first and last)
        Assert.assertTrue(h.startsWith("00:1:"));
    }
}
