/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.upc.edu.Protocols;

import jade.core.Agent;
import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import jade.domain.FIPANames;
import jade.lang.acl.MessageTemplate;

import java.util.Date;
import java.util.Vector;
import java.util.Enumeration;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author igomez
 */
public class ContractNetInitiatorAgent extends Agent {
	private int nResponders;


         private class ContractNetInitiatorBehaviour extends ContractNetInitiator
         {
             public ContractNetInitiatorBehaviour(Agent a, ACLMessage mt)
            {
                super(a,mt);
            }

             protected void handlePropose(ACLMessage propose, Vector v)
             {
		System.out.println("Agent '"+propose.getSender().getName()+"' proposed '"+propose.getContent() + "'");
             }

            protected void handleRefuse(ACLMessage refuse)
            {
                    System.out.println("Agent '"+refuse.getSender().getName()+"' refused");
            }

            protected void handleFailure(ACLMessage failure)
            {
                    if (failure.getSender().equals(myAgent.getAMS())) {
                            // FAILURE notification from the JADE runtime: the receiver
                            // does not exist
                            System.out.println("Responder does not exist");
                    }
                    else {
                            System.out.println("Agent '"+failure.getSender().getName()+"' failed");
                    }
                    // Immediate failure --> we will not receive a response from this agent
                    nResponders--;
            }

            protected void handleAllResponses(Vector responses, Vector acceptances)
            {
                    if (responses.size() < nResponders) {
                            // Some responder didn't reply within the specified timeout
                            System.out.println("Timeout expired: missing "+(nResponders - responses.size())+" responses");
                    }
                    // Evaluate proposals.
                    int bestProposal = -1;
                    AID bestProposer = null;
                    ACLMessage accept = null;
                    Enumeration e = responses.elements();
                    while (e.hasMoreElements()) {
                            ACLMessage msg = (ACLMessage) e.nextElement();
                            if (msg.getPerformative() == ACLMessage.PROPOSE) {
                                    ACLMessage reply = msg.createReply();
                                    reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                                    acceptances.addElement(reply);
                                    int proposal = Integer.parseInt(msg.getContent());
                                    if (proposal > bestProposal) {
                                            bestProposal = proposal;
                                            bestProposer = msg.getSender();
                                            accept = reply;
                                    }
                            }
                    }
                    // Accept the proposal of the best proposer
                    if (accept != null) {
                            System.out.println("Accepting proposal '"+bestProposal+"' from responder '"+bestProposer.getName() + "'");
                            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                    }
            }

            protected void handleInform(ACLMessage inform)
            {
                    System.out.println("Agent '"+inform.getSender().getName()+"' successfully performed the requested action");
            }

             
         }

  protected void setup() {
  	// Read names of responders as arguments
  	Object[] args = getArguments();
  	if (args != null && args.length > 0) {
  		nResponders = args.length;
  		System.out.println("Trying to delegate the action Perform AIA exam to "+nResponders+" responders. Press any key to go on...");
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in) );
            try {
                String sample = br.readLine();
            } catch (IOException ex) {
                Logger.getLogger(ContractNetInitiatorAgent.class.getName()).log(Level.SEVERE, null, ex);
            }

  		// Create the CFP message
  		ACLMessage msg = new ACLMessage(ACLMessage.CFP);
  		for (int i = 0; i < args.length; ++i) {
  			msg.addReceiver(new AID((String) args[i], AID.ISLOCALNAME));
  		}
			msg.setProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET);
			// We want to receive a reply in 10 secs
			msg.setReplyByDate(new Date(System.currentTimeMillis() + 10000));
			msg.setContent("perform-AIA-exam");
                        ContractNetInitiatorBehaviour cib = new ContractNetInitiatorBehaviour(this,msg);
			addBehaviour(cib);
  	}
  	else {
  		System.out.println("No responder specified.");
  	}
  }
}


