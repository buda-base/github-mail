package bdrc.utils.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.sargue.mailgun.Configuration;
import net.sargue.mailgun.Mail;
import net.sargue.mailgun.MailBuilder;

public class GithubMessage {

    private static Properties props = null;
    private String repo;
    private Mail mail;
    public static String TAB = "\t";
    String authorEmail = "";

    static {
        try {
            FileInputStream stream = new FileInputStream(System.getProperty("githubmail.configpath") + "mail.properties");
            props = new Properties();
            props.load(stream);
            stream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public GithubMessage(String json) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(json);
        String msg = parsePayload(node);
        String[] recip = props.getProperty("recipients").split(",");
        Configuration configuration = new Configuration().domain(props.getProperty("mail.domain")).apiKey(props.getProperty("mail.key"));
        MailBuilder build = Mail.using(configuration);
        for (String s : recip) {
            build = build.to(s);
        }
        String subject = node.findValue("head_commit").findValue("message").asText().split(System.lineSeparator())[0];
        build.from(node.findValue("head_commit").findValue("author").findValue("name").asText(), node.findValue("head_commit").findValue("author").findValue("email").asText())
                .replyTo(node.findValue("head_commit").findValue("author").findValue("email").asText()).subject("[" + repo + "] " + node.findValue("head_commit").findValue("id").asText().substring(0, 7) + ": " + subject).text(msg);
        this.mail = build.build();

    }

    public void send() {
        this.mail.send();
    }

    private String parsePayload(JsonNode node) {
        authorEmail = node.findValue("head_commit").findValue("author").findValue("email").asText();
        String res = "";
        repo = node.findValue("repository").findValue("full_name").textValue();
        res = System.lineSeparator() + "head_commit :" + System.lineSeparator();
        res = res + TAB + "branch : " + node.findValue("ref").asText() + System.lineSeparator();
        res = res + TAB + "message: " + node.findValue("head_commit").findValue("message").asText() + System.lineSeparator();
        res = res + TAB + "timestamp: " + node.findValue("head_commit").findValue("timestamp").asText() + System.lineSeparator();
        res = res + TAB + "url: " + node.findValue("head_commit").findValue("url").asText() + System.lineSeparator();
        res = res + TAB + "author: " + node.findValue("head_commit").findValue("author").findValue("name").asText() + System.lineSeparator();
        res = res + TAB + "added: " + System.lineSeparator() + node.findValue("head_commit").findValue("added").toString().replace(",", System.lineSeparator()) + System.lineSeparator();
        res = res + TAB + "removed: " + System.lineSeparator() + node.findValue("head_commit").findValue("removed").toString().replace(",", TAB + System.lineSeparator()) + System.lineSeparator();
        res = res + TAB + "modified: " + System.lineSeparator() + node.findValue("head_commit").findValue("modified").toString().replace(",", System.lineSeparator()) + System.lineSeparator();
        return res;
    }

    public static void main(String[] args) throws IOException {
        InputStream stream = GithubMessage.class.getClassLoader().getResourceAsStream("payload.json.txt");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(stream);
        stream.close();
        GithubMessage msg = new GithubMessage(node.toString());
        msg.send();
        System.out.println("done");
    }
}
