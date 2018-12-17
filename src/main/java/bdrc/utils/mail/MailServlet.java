package bdrc.utils.mail;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "MailServlet", urlPatterns = "/sendMail")
public class MailServlet extends HttpServlet{

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String json=request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        try {
            GithubMessage msg=new GithubMessage(json);
            msg.send();
            response.setStatus(200);
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response.sendError(500);
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response.sendError(500);
        }
    }
}
