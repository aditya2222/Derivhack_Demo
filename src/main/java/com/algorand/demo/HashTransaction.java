
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

import org.isda.cdm.rosettakey.SerialisingHashFunction;
import org.isda.cdm.AllocationPrimitive;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;


public class HashTransaction  {

    public static void main(String [] args) throws Exception{
        //Take a CDM contract as input
        String contract = args[0];
        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getDefaultRosettaObjectMapper();

        //Create an Algorand Transaction
        Transaction txdetails = null;
        try {

            //Deserialize input CDM object to Java
            AllocationPrimitive allocationPrimitive = rosettaObjectMapper
                .readValue(DerivhackDemo.readFile(contract), AllocationPrimitive.class);

            //Compute global key of object using Rosetta provided hash function
            SerialisingHashFunction hashFunction = new SerialisingHashFunction();
            String message = hashFunction.hash(allocationPrimitive);
            
            //Commit the transaction
            txdetails = NotesTransaction.commitNotes(message);

            //Output transaction details including ID
            System.out.println("Transaction: "+ txdetails.getTx());
            String txlink = "https://testnet.algoexplorer.io/tx/"+txdetails.getTx();
            String result= txdetails.toString();
            System.out.println("Transaction Details: " + result);
            System.out.println("Blockchain URL: " + txlink);
            System.out.println("HashCode: " + message);
            return;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}