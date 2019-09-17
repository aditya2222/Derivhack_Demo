
package com.algorand.demo;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.security.KeyPair;
import javax.crypto.SecretKey;

import com.algorand.algosdk.algod.client.model.Transaction;
import org.apache.commons.codec.digest.DigestUtils;

@WebServlet(
        name = "NetworkCommitServlet",
        urlPatterns = "/NetworkCommit"
)
public class NetworkCommitServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String contract = req.getParameter("editTemplate").toString();
        Transaction txdetails = null;
        try {
            // KeyPair generateKeyPair = CryptographyUtil.generateKeyPair();
            // SecretKey secKey = CryptographyUtil.generateSymmetricKey();

            // byte[] encrypted_contract = CryptographyUtil.symmetricEncrypt(secKey, contract.getBytes());

            // byte[] publicKey = generateKeyPair.getPublic().getEncoded();
            // byte[] privateKey = generateKeyPair.getPrivate().getEncoded();

            // byte[] encryptedKey = CryptographyUtil.encrypt(publicKey, secKey.getEncoded());

            // String message = new String(encrypted_contract) + " | " + new String(encryptedKey);
            // System.out.println(message);
            String message = DigestUtils.sha256Hex(contract)+"";
            System.out.println("The hashcode is: " + message);
            txdetails = NotesTransaction.commitNotes(message);

            String txlink = "https://testnet.algoexplorer.io/tx/"+txdetails.getTx();
            req.setAttribute("txdetails", txdetails.toString());
            req.setAttribute("txlink", txlink);
            RequestDispatcher view = req.getRequestDispatcher("committed.jsp");
            view.forward(req, resp);

        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }
}