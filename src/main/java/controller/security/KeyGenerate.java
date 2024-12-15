package controller.security;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

@WebServlet("/generate-key")
public class KeyGenerate  extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            //Tạo cặp khoá RSA - 2048.
            KeyPairGenerator  keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

            // base64.
            String pubKeyPEM = convertToPEM(keyPair.getPublic(), "PUBLIC KEY");
            String priKeyPEM = convertToPEM(keyPair.getPrivate(), "PRIVATE KEY");

            //Json
            resp.setContentType("application/json");
            PrintWriter out = resp.getWriter();
            out.print("{");
            out.println("\"publicKey\": \""+pubKeyPEM+"\",");
            out.println("\"privateKey\": \""+priKeyPEM+"\"");
            out.println("}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
    private String convertToPEM(Key key, String type) {
//        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
//        StringBuilder pemFormat = new StringBuilder();
//        pemFormat.append("-----BEGIN ").append(type).append("-----\n");
//        //Chia base 64.
//        for (int i = 0; i < encodedKey.length(); i += 64) {
//            int end = Math.min(i + 64, encodedKey.length());
//            pemFormat.append(encodedKey, i, end).append("\n");
//        }
//        pemFormat.append("-----END ").append(type).append("-----");
//        System.out.println(pemFormat);
//        return pemFormat.toString();
        String encodedKey = Base64.getEncoder().encodeToString(key.getEncoded());
        StringBuilder pemFormat = new StringBuilder();
        pemFormat.append("-----BEGIN ").append(type).append("-----\n");
        // Chia base 64.
        for (int i = 0; i < encodedKey.length(); i += 64) {
            int end = Math.min(i + 64, encodedKey.length());
            pemFormat.append(encodedKey, i, end).append("\n");
        }
        pemFormat.append("-----END ").append(type).append("-----");

        // Loại bỏ ký tự dòng mới và ký tự điều khiển
        String pemString = pemFormat.toString().replace("\n", "\\n").replace("\r", "\\r");
        System.out.println(pemString);
        return pemString;

    }
}
