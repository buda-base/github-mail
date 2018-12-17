package bdrc.utils.mail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GithubMessage {

    private static Properties props=null;

    static {
        try {
            FileInputStream stream=new FileInputStream(System.getProperty("mailConfigPath")+"mail.properties");
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

    public GithubMessage(JsonNode node) throws AddressException, MessagingException {
        final Properties p=props;
        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                  return new PasswordAuthentication(p.getProperty("mail.user"), p.getProperty("mail.password"));
            }
        });
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("tbrctest@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse("agate.marc@gmail.com"));
        message.setSubject("Test github Mail Subject");
        String msg = "This is my first github email ";
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.setContent(msg, "text/plain");
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(mimeBodyPart);
        message.setContent(multipart);
        Transport.send(message);

    }

    public static void main(String[] args) throws IOException, AddressException, MessagingException {

        InputStream stream = GithubMessage.class.getClassLoader().getResourceAsStream("payload.json.txt");
        ObjectMapper mapper=new ObjectMapper();
        JsonNode node=mapper.readTree(stream);
        stream.close();
        GithubMessage msg=new GithubMessage(node);
        System.out.println("done");
    }
}
