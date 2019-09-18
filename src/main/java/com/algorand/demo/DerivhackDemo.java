
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
import org.json.JSONArray;
import org.json.JSONObject;
import org.isda.cdm.*;
import java.math.BigDecimal;
import org.isda.cdm.metafields.MetaFields;

import com.algorand.algosdk.algod.client.model.Transaction;
import org.apache.commons.codec.digest.DigestUtils;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.*;
import org.isda.cdm.functions.example.services.identification.IdentifierService;
import org.isda.cdm.processor.PostProcessorProvider;
import org.isda.cdm.Party;
import org.isda.cdm.Account;
import org.isda.cdm.AllocationPrimitive;
import org.isda.cdm.AllocationPrimitive.AllocationPrimitiveBuilder;
import org.isda.cdm.Trade.TradeBuilder;
import org.isda.cdm.Trade;
import org.isda.cdm.ContractualProduct.ContractualProductBuilder;
import org.isda.cdm.Contract.ContractBuilder;
import org.isda.cdm.TradeDate.TradeDateBuilder;
import com.rosetta.model.lib.records.DateImpl;
import org.isda.cdm.EconomicTerms.EconomicTermsBuilder;
import org.isda.cdm.Cashflow.CashflowBuilder;
import org.isda.cdm.Money.MoneyBuilder;
import org.isda.cdm.Payout.PayoutBuilder;
import org.isda.cdm.PayerReceiver.PayerReceiverBuilder;
import com.google.inject.Inject;
import org.isda.cdm.metafields.FieldWithMetaString.FieldWithMetaStringBuilder;
import org.isda.cdm.metafields.FieldWithMetaDate.FieldWithMetaDateBuilder;
import org.isda.cdm.metafields.FieldWithMetaDate;
import org.isda.cdm.metafields.ReferenceWithMetaParty.ReferenceWithMetaPartyBuilder;
import org.isda.cdm.metafields.ReferenceWithMetaAccount.ReferenceWithMetaAccountBuilder;
import org.isda.cdm.Party.PartyBuilder;
import org.isda.cdm.Account.AccountBuilder;
import org.isda.cdm.Execution.ExecutionBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import org.isda.cdm.ExecutionTypeEnum;
import org.isda.cdm.Quantity.QuantityBuilder;
import org.isda.cdm.Product.ProductBuilder;
import org.isda.cdm.Security.SecurityBuilder;
import org.isda.cdm.rosettakey.SerialisingHashFunction;
import org.isda.cdm.Identifier;
import org.isda.cdm.ActionEnum;
import org.isda.cdm.metafields.FieldWithMetaString;
import org.isda.cdm.Account.AccountBuilder;
import org.isda.cdm.metafields.*;
import org.isda.cdm.PartyRole.PartyRoleBuilder;
import org.isda.cdm.Price.PriceBuilder;

import org.isda.cdm.Security.SecurityBuilder;
import org.isda.cdm.Bond.BondBuilder;
import org.isda.cdm.ProductIdentifier.ProductIdentifierBuilder;
import org.isda.cdm.Event.EventBuilder;
import org.isda.cdm.EventEffect.EventEffectBuilder;

import org.isda.cdm.metafields.ReferenceWithMetaEvent.ReferenceWithMetaEventBuilder;
import org.isda.cdm.metafields.ReferenceWithMetaExecution.ReferenceWithMetaExecutionBuilder;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.regnosys.rosetta.common.serialisation.RosettaObjectMapper;
import org.isda.cdm.Trade.TradeBuilder;
import org.isda.cdm.PrimitiveEvent.PrimitiveEventBuilder;
import org.isda.cdm.AllocationOutcome.AllocationOutcomeBuilder;

public  class DerivhackDemo {

    

protected static String readFile(String filePath)
{
    StringBuilder contentBuilder = new StringBuilder();
    try (Stream<String> stream = Files.lines( Paths.get(filePath), StandardCharsets.UTF_8))
    {
        stream.forEach(s -> contentBuilder.append(s).append("\n"));
    }
    catch (IOException e)
    {
        e.printStackTrace();
    }
    return contentBuilder.toString();
}


        private static MetaFields buildMeta(String hash, String externalKey){
            return MetaFields.builder().setGlobalKey(hash).setExternalKey(externalKey).build();

        }
    
        private static MetaFields buildMeta(String hash){
            return MetaFields.builder().setGlobalKey(hash).build();

        }
   
        private static AllocationPrimitive buildAllocationPrimitive(BigDecimal quantity, Party party, Account account,IdentifierService identifierService){
            //System.out.println("Party: " + party.toString());
            //System.out.println("Party key:" + party.getMeta());
            //System.out.println("Party external key" + party.getMeta().getExternalKey());
            //System.out.println("Identifier Service" + identifierService);
            //System.out.println(AllocationPrimitive.class.getSimpleName());
            Identifier id = identifierService.nextType(party.getMeta().getExternalKey(), AllocationPrimitive.class.getSimpleName());
            //Identifier id = null;

            AllocationPrimitiveBuilder builder = new AllocationPrimitiveBuilder();
            builder.setBefore( 
                new TradeBuilder().
                    setExecution(new ExecutionBuilder().
                    setExecutionType(ExecutionTypeEnum.ELECTRONIC).
                    addParty(party).
                    addIdentifier(id).
                    setTradeDate(
                        new FieldWithMetaDateBuilder().
                            setValue( new DateImpl(1,1,2001)).
                        build()).
                    setQuantity(
                        new QuantityBuilder().
                            setAmount(quantity).
                        build()).
                    setProduct(
                        new ProductBuilder().
                            setSecurity(
                                new SecurityBuilder().

                                build()).
                        build()).
                build()).
            build());

       // postProcessorProvider.getPostProcessor().forEach(step -> step.runProcessStep(AllocationPrimitive.class, builder));


        return builder.build();

        }


        private static Event buildEvent(List<EventTimestamp> timestamps, DateImpl eventDate, ActionEnum action, Lineage lineage, PrimitiveEvent primitiveEvent, EventEffect eventEffect, List<Party> parties , SerialisingHashFunction hashFunction,IdentifierService identifierService ){

            EventBuilder builder = new EventBuilder();
            builder.setEventDate(eventDate).
            setAction(action).
            setLineage(lineage).
            setPrimitive(primitiveEvent).
            setEventEffect(eventEffect);

            for(Party party: parties){
                builder.addParty(party);
            }


            for(EventTimestamp timestamp: timestamps){
                builder.addTimestamp(timestamp);
            }

            Event  event = builder.build();
            String hash = hashFunction.hash(event);
            event = event.toBuilder().setMeta(buildMeta(hash)).build();

            Identifier id = identifierService.nextType(event.getMeta().getExternalKey(),Event.class.getSimpleName());
            event = event.toBuilder().addEventIdentifier(id).build();
            
            return event;

       // postProcessorProvider.getPostProcessor().forEach(step -> step.runProcessStep(AllocationPrimitive.class, builder));


        }

        private static Execution buildExecution(ExecutionTypeEnum executionType, List<PartyRole> partyRoles, Price price, Product product, Quantity quantity, SettlementTerms settlementTerms, FieldWithMetaDate tradeDate, String externalKey, IdentifierService identifierService, SerialisingHashFunction hashFunction){

            ExecutionBuilder builder = new ExecutionBuilder();
            builder.setExecutionType(executionType).
            setPrice(price).
            setProduct(product).
            setQuantity(quantity).
            setSettlementTerms(settlementTerms).
            setTradeDate(tradeDate);

            for(PartyRole partyRole: partyRoles){
                builder.addPartyRole(partyRole);
            }

            Execution  execution = builder.build();
            String hash = hashFunction.hash(execution);
            execution = execution.toBuilder().setMeta(buildMeta(hash, externalKey)).build();

            Identifier id = identifierService.nextType(execution.getMeta().getExternalKey(),Execution.class.getSimpleName());
            execution = execution.toBuilder().addIdentifier(id).build();
            
            return execution;

       // postProcessorProvider.getPostProcessor().forEach(step -> step.runProcessStep(AllocationPrimitive.class, builder));


        }

        private static PartyRole buildPartyRole(ReferenceWithMetaParty party, PartyRoleEnum role){
            PartyRoleBuilder builder = new PartyRoleBuilder();
            builder.setPartyReference(party);
            builder.setRole(role);

            return builder.build();
        }

        private static Price buildPrice(ActualPrice grossPrice, ActualPrice netPrice, BigDecimal accruedInterest){
            PriceBuilder builder = new PriceBuilder();
            builder.setGrossPrice(grossPrice).
            setNetPrice(netPrice).
            setAccruedInterest(accruedInterest);


            return builder.build();
        }

        private static Product buildProduct(String identifier){
        //THIS IS A TODO. RIGHT NOW IT ONLY BUIDLDS BONDS
            ProductBuilder builder = new ProductBuilder();
            builder.setSecurity(
                new SecurityBuilder().
                setBond(
                    new BondBuilder().
                        setProductIdentifier(
                            new ProductIdentifierBuilder().
                                addIdentifier(
                                    new FieldWithMetaStringBuilder().
                                        setValue(identifier).
                                    build()).

                            build()).
                    build()).
                build());

            return builder.build();
        }

        private static EventEffect buildEventEffect(List<ReferenceWithMetaExecution> effectedExecutions){
            EventEffectBuilder builder = new EventEffectBuilder();
            for(ReferenceWithMetaExecution effectedExecution: effectedExecutions){
                builder.addEffectedExecution(effectedExecution);
            }
            EventEffect eventEffect = builder.build();
            return eventEffect;

        }

        private static Account buildAccount(FieldWithMetaString accountName, FieldWithMetaString accountNumber, ReferenceWithMetaParty servicingParty,  SerialisingHashFunction hashFunction){
            AccountBuilder builder = new AccountBuilder();
            builder.setAccountName(accountName).
            setAccountNumber(accountNumber).
            setServicingParty(servicingParty);

            Account account = builder.build();
            
            String hash = hashFunction.hash(account);
            account = account.toBuilder().setMeta(buildMeta(hash)).build();

            
            return account;
        }
    
    

        private static Party buildParty(FieldWithMetaString partyId, FieldWithMetaString partyName,String externalKey,SerialisingHashFunction hashFunction){
            PartyBuilder builder = new PartyBuilder();
            builder.addPartyId(partyId)
                .setName(partyName);

            Party party = builder.build();

            String hash = hashFunction.hash(party);
            party = party.toBuilder().setMeta(buildMeta(hash,externalKey)).build();
            return party;

        }


    public static void main(String [] args) throws Exception{

        //Take as input an Allocation Instructions File (in DerivHack format) and a CDM executions file
        // Example on how to run with Maven
        // mvn -s settings.xml exec:java -Dexec.mainClass="com.algorand.demo.DerivhackDemo" -Dexec.args="./Files/input_allocations.json ./Files/output_executions.json"  -l "./Files/output_allocations.json" -q  



        //Set an identifier service and a hash function to generate Identifiers and Rosetta Keys
        IdentifierService identifierService = new IdentifierService();
        SerialisingHashFunction hashFunction = new SerialisingHashFunction();
        ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getDefaultRosettaObjectMapper();

        //Create an allocation event and the relevant builders
        Event allocationEvent;
        EventBuilder builder = new EventBuilder();
        PrimitiveEventBuilder primitiveEventBuilder = new PrimitiveEventBuilder();
        AllocationPrimitiveBuilder allocationBuilder = new AllocationPrimitiveBuilder();
        AllocationOutcomeBuilder afterBuilder = new AllocationOutcomeBuilder();
        TradeBuilder originalTradeBuilder = new TradeBuilder();


        //Read the input arguments and read them into files
        String allocationInstructionFile = args[0];
        String executionsCDMFile = args[1];
        String allocationInstructions = readFile(allocationInstructionFile);
        String executionsCDM = readFile(executionsCDMFile);

         //Read the executions CDM into a CDM object using the Rosetta object mapper
        Event executionEvent = rosettaObjectMapper
                .readValue(executionsCDM, Event.class);
        Execution beforeExecution = executionEvent
                                            .getPrimitive()
                                            .getExecution()
                                            .get(0)
                                            .getAfter()  
                                            .getExecution(); 

       
        //First, assign all the accounts in the execution to the allocation event
        List<Account> accounts = executionEvent.getAccount();
        for(Account account: accounts){
            builder.addAccount(account);
        }

        //Then, set the Event action as the same as the Event action of the execution event
        builder.setAction(executionEvent.getAction());

        //Then, set the event date to be the same as the date of the execution event
        builder.setEventDate(executionEvent.getEventDate());

        //Set the lineage. The previous event to the allocation is the execution event
        builder.setLineage( new Lineage.LineageBuilder()
                                .addEventReference(
                                    new ReferenceWithMetaEventBuilder()
                                        .setGlobalReference(executionEvent.getMeta().getGlobalKey())
                                    .build())
                            .build());

        //Set the parties from the execution event
        List<Party> parties = executionEvent.getParty();
        for(Party party: parties){
            builder.addParty(party);
        }

        //Read the allocation instructions into a JSON array
        JSONArray jArray = new JSONArray(allocationInstructions);

        //Use the allocation instructions to create the ``after'' executions
        JSONObject jObject = (JSONObject) jArray.get(0);



        JSONArray allocations = (JSONArray) jObject.get("Allocations");
        for(int allocation_idx = 0; allocation_idx < allocations.length(); allocation_idx++){
            
            JSONObject allocation = (JSONObject) allocations.get(allocation_idx);
            BigDecimal quantity = new BigDecimal((Double) allocation.get("Quantity"));
            JSONObject clientAccount = (JSONObject) allocation.get("ClientAccount");

            JSONObject partyJSON = (JSONObject) clientAccount.get("Party");
            String partyID = (String) partyJSON.get("partyId");
            String partyName = (String) partyJSON.get("name");

            JSONObject accountJSON = (JSONObject) clientAccount.get("Account");
            String accountNumber = (String) accountJSON.get("accountNumber");
            String accountName = (String) accountJSON.get("accountName");

            String role= (String) clientAccount.get("role");

            FieldWithMetaString partyIDWithMeta = new FieldWithMetaStringBuilder().setValue(partyID).build();
            FieldWithMetaString partyNameWithMeta = new FieldWithMetaStringBuilder().setValue(partyName).build();

            FieldWithMetaString accountNumberWithMeta = new FieldWithMetaStringBuilder().setValue(accountNumber).build();
            FieldWithMetaString accountNameWithMeta = new FieldWithMetaStringBuilder().setValue(accountName).build();


        
            Party party = buildParty(partyIDWithMeta,partyNameWithMeta,partyID,hashFunction);
            ReferenceWithMetaParty partyReference = new ReferenceWithMetaPartyBuilder()
                                                    .setMeta(
                                                         MetaFields.builder()
                                                        .setReference(party.getMeta().getGlobalKey())
                                                        .build())
                                                    .build();

            Account cdmAccount = buildAccount(accountNameWithMeta,accountNumberWithMeta,partyReference,hashFunction);
            builder.addAccount(cdmAccount).addParty(party);

            ExecutionBuilder executionBuilder = beforeExecution.toBuilder();


            PartyRole partyRole = buildPartyRole(partyReference, PartyRoleEnum.valueOf(role.toUpperCase()));
            executionBuilder.addPartyRole(partyRole);
            executionBuilder.setQuantity(
                                new QuantityBuilder()
                                    .setAmount(quantity)
                                .build());

            Execution  afterExecution = executionBuilder.build();
            TradeBuilder allocatedTradeBuilder = new TradeBuilder();

           

            String hash = hashFunction.hash(afterExecution);
            afterExecution = afterExecution.toBuilder().setMeta(buildMeta(hash)).build();

             builder.setEventEffect(
                new EventEffectBuilder()
                .addEffectedExecution(
                    new ReferenceWithMetaExecutionBuilder()
                        .setMeta(
                            MetaFields.builder()
                                .setReference(hash)
                            .build())
                    .build())
                .build());

            Identifier id = identifierService.nextType(afterExecution.getMeta().getExternalKey(),Execution.class.getSimpleName());
            afterExecution = afterExecution.toBuilder().addIdentifier(id).build();
            
            allocatedTradeBuilder.setExecution(afterExecution);
            afterBuilder.addAllocatedTrade(allocatedTradeBuilder.build());


        }
       



        originalTradeBuilder = new TradeBuilder().setExecution(beforeExecution);
                                        

        afterBuilder.setOriginalTrade(originalTradeBuilder.build());

        allocationBuilder.setAfter(afterBuilder.build());
        allocationBuilder.setBefore(originalTradeBuilder.build());


        primitiveEventBuilder.addAllocation(allocationBuilder.build());
        builder.setPrimitive(primitiveEventBuilder.build());


        allocationEvent = builder.build();
        String hash = hashFunction.hash(allocationEvent);
        allocationEvent = allocationEvent.toBuilder().setMeta(buildMeta(hash)).build();
         

        Identifier id = identifierService.nextType(allocationEvent.getMeta().getExternalKey(),Event.class.getSimpleName());
        allocationEvent = allocationEvent.toBuilder().addEventIdentifier(id).build();

        String json = rosettaObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(allocationEvent);

        System.out.println(json);



        //Read the details of the Allocation from the Allocation JSON file
        //For this demo, we will only use the first allocation that we read
        /*
        JSONObject jb = jArray.getJSONObject(0);
        Integer DH_TradeID = (Integer) jb.get("DH_TradeID");
        JSONArray allocations = (JSONArray) jb.get("Allocations");
        for(int allocation_idx = 0; allocation_idx < allocations.length(); allocation_idx++ ){
               
                JSONObject allocation = allocations.getJSONObject(allocation_idx);
                BigDecimal quantity = new BigDecimal((Double) allocation.get("Quantity"));
                JSONObject partyJSON =  (JSONObject) ( (JSONObject) (allocation.get("ClientAccount"))).get("Party");
                JSONObject accountJSON = (JSONObject) ( (JSONObject) ( allocation.get("ClientAccount"))).get("Account");

                AccountBuilder accountBuilder = new AccountBuilder();
                PartyBuilder partyBuilder = new PartyBuilder();

                Account account = accountBuilder.
                        setAccountName(
                            new FieldWithMetaStringBuilder().
                                setValue((String) accountJSON.get("accountName") ).
                            build()).
                        setAccountNumber(
                            new FieldWithMetaStringBuilder().
                                setValue((String)accountJSON.get("accountNumber")  ).
                            build()).
                build();

                Party party = partyBuilder.
                            setAccount(account).
                            setName(
                            new FieldWithMetaStringBuilder().
                                setValue((String)accountJSON.get("accountNumber")  ).
                             build()).
                build();

                String hash = hashFunction.hash(party);
                party = party.toBuilder().setMeta(MetaFields.builder().setGlobalKey(hash).build()).build();
                party = party.toBuilder().setMeta(MetaFields.builder().setExternalKey(hash).build()).build();


                AllocationPrimitive allocationPrimitive = buildAllocationPrimitive(quantity,party,account,identifierService);
                //System.out.println(allocationPrimitive.toString());

                ObjectMapper rosettaObjectMapper = RosettaObjectMapper.getDefaultRosettaObjectMapper();

                String json = rosettaObjectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(allocationPrimitive);

                //System.out.println("Serialise to JSON");
                System.out.println(json);
            }
        }
        //Map<String,Object> objectMap;
        //List<Map<String,Object>> objectList = ObjectMapper.readValue(fileContents, new TypeReference<List<Map<String,Object>>>(){});
        //for(int i = 0; i < objectList.size(); i++){
         //   objectMap = objectList.get(i);
         //   System.out.println(objectMap.keySet().toString());
         //   System.out.println(objectMap.values().toString());


        
        */
    }
}
/*


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
*/
