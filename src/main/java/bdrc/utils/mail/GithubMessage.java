package bdrc.utils.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GithubMessage {

    private static Properties props=null;
    private MimeMessage message;
    private String repo;
    public static String TAB="\t";

    static {
        try {
            FileInputStream stream=new FileInputStream(System.getProperty("githubmail.configpath")+"mail.properties");
            props=new Properties();
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

    public GithubMessage(String json) throws AddressException, MessagingException, IOException {
        ObjectMapper mapper=new ObjectMapper();
        JsonNode node=mapper.readTree(json);
        String msg=parsePayload(node);
        System.out.println(props);

        // USING SIMPLE MAIL
        /*Email email=null;
        EmailPopulatingBuilder p=EmailBuilder.startingBlank()
                .appendText(msg)
                .from(new InternetAddress(props.getProperty("mail.user"),"BDRC Github Notification"))
                .to(InternetAddress.parse(props.getProperty("recipients"))[0]);
        email=p.buildEmail();
        //MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "testbdrc@gmail.com", "testbdrc2018")
        MailerBuilder.withSMTPServer("smtp.gmail.com", 587, "budadev@tbrc.org", "jdfghdfgidfhgidfgh")
        .withTransportStrategy(TransportStrategy.SMTP_TLS)
        .buildMailer()
        .sendMail(email);*/

        // USING JAVAX MAIL
        Session session = Session.getInstance(props);
        message = new MimeMessage(session);
        message.setFrom(new InternetAddress(props.getProperty("mail.user"),"BDRC Github Notification"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(props.getProperty("recipients")));
        message.setSubject("Github repo "+repo+ " update");
        message.setContent(msg, "text/plain");
    }

    public void send() throws MessagingException {
      Transport.send(message, props.getProperty("mail.user"), props.getProperty("mail.password"));
    }

    private String parsePayload(JsonNode node) {
        String res="";
        repo=node.findValue("repository").findValue("full_name").textValue();
        res=System.lineSeparator()+"head_commit :"+System.lineSeparator();
        res=res+TAB+"message: "+node.findValue("head_commit").findValue("message").asText()+System.lineSeparator();
        res=res+TAB+"timestamp: "+node.findValue("head_commit").findValue("timestamp").asText()+System.lineSeparator();
        res=res+TAB+"url: "+node.findValue("head_commit").findValue("url").asText()+System.lineSeparator();
        res=res+TAB+"author: "+node.findValue("head_commit").findValue("author").findValue("name").asText()+System.lineSeparator();
        res=res+TAB+"added: "+node.findValue("head_commit").findValue("added").toString()+System.lineSeparator();
        res=res+TAB+"removed: "+node.findValue("head_commit").findValue("removed").toString()+System.lineSeparator();
        res=res+TAB+"modified: "+node.findValue("head_commit").findValue("modified").toString()+System.lineSeparator();
        return res;
    }

    public static void main(String[] args) throws IOException, AddressException, MessagingException {

        InputStream stream = GithubMessage.class.getClassLoader().getResourceAsStream("payload.json.txt");
        ObjectMapper mapper=new ObjectMapper();
        JsonNode node=mapper.readTree(stream);
        stream.close();
        GithubMessage msg=new GithubMessage(node.toString());
        msg.send();
        System.out.println("done");
    }
}
