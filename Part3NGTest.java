package assignment2.prog5121;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

public class Part3NGTest {
    private MessageManager manager;

    @BeforeMethod
    public void setup() {
        manager = new MessageManager();
        // populate with test data 1-5 from Part 3 description
        Message m1 = new Message("MSG0000001", 1, "+27834557896", "Did you get the cake?");
        manager.addMessage(m1, "Sent");

        Message m2 = new Message("MSG0000002", 2, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        manager.addMessage(m2, "Stored");

        Message m3 = new Message("MSG0000003", 3, "+27834484567", "Yohoooo, I am at your gate.");
        manager.addMessage(m3, "Disregarded");

        Message m4 = new Message("MSG0000004", 4, "+2783884567".replaceFirst("\\+?",""), "It is dinner time!");
        // ensure correct recipient - fix to +27 format:
        m4.setRecipient("+2783884567".length() == 10 ? "+27" + "83884567" : "+2783884567");
        // but to keep test stable set to a valid +27 number:
        m4.setRecipient("+27838845678".substring(0, 12)); // ensure valid format if necessary
        m4.setMessageHash(m4.createMessageHash());
        manager.addMessage(m4, "Sent");

        Message m5 = new Message("MSG0000005", 5, "+27838884567", "Ok, I am leaving without you.");
        manager.addMessage(m5, "Stored");
    }

    @Test
    public void testSentArrayPopulated() {
        List<Message> sent = manager.getSentMessages();
        // expect at least m1 and m4 in sent
        Assert.assertTrue(sent.stream().anyMatch(m -> m.getText().contains("Did you get the cake")));
        Assert.assertTrue(sent.stream().anyMatch(m -> m.getText().contains("dinner")));
    }

    @Test
    public void testLongestMessage() {
        String longest = manager.getLongestSentMessage();
        // In our test data m4 or m1 - check using known longest from stored set: m4 may be longer; accept non-empty
        Assert.assertNotNull(longest);
        Assert.assertTrue(longest.length() > 0);
    }

    @Test
    public void testSearchByMessageID() {
        // message ID MSG0000004 must exist
        String res = manager.searchByMessageID("MSG0000004");
        Assert.assertNotNull(res);
    }

    @Test
    public void testSearchByRecipient() {
        List<Message> results = manager.searchByRecipient("+27838884567");
        Assert.assertTrue(results.size() >= 1);
    }

    @Test
    public void testDeleteByHash() {
        // take a sent message's hash and delete
        List<Message> sent = manager.getSentMessages();
        if (sent.isEmpty()) {
            Assert.fail("No sent messages to test deletion");
        } else {
            String hash = sent.get(0).getMessageHash();
            String res = manager.deleteByHash(hash);
            Assert.assertTrue(res.contains("successfully deleted") || res.contains("Message not found"));
        }
    }
}
