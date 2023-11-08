/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practicaSID.Agents;

/**
 *
 * @author guillemrbaiges
 */
import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.ContractNetResponder;
import java.util.ArrayList;
import java.util.List;

public class IndustryAgent extends Agent
{     
    private int quantityPollutantWaste;  
    protected String performs;
    protected int evaluation;

    public class IndustryAgentBehaviour extends ContractNetResponder
    {
        
        public IndustryAgentBehaviour(Agent a, MessageTemplate mt) {
            super(a,mt);
        }
        
        private boolean performAction()
        {
            // Simulate action execution by generating a random number
            return (Math.random() > 0.2);
        }

        protected ACLMessage prepareResponse(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
            System.out.println("Agent '"+getLocalName()+"' receives a CFP from Agent '"+cfp.getSender().getName()+"' to perform action '"+cfp.getContent() + "'");
            if (performs.equalsIgnoreCase("YES")) {
                // We provide a proposal
                System.out.println("Agent '"+getLocalName()+"' proposes  '"+evaluation + "'");
                ACLMessage propose = cfp.createReply();
                propose.setPerformative(ACLMessage.PROPOSE);
                propose.setContent(String.valueOf(quantityPollutantWaste));
                return propose;
            }
            else {
                // We refuse to provide a proposal
                System.out.println("Agent '"+getLocalName()+"' is not interested in the proposal");
                throw new RefuseException("evaluation-failed");
            }
        }
        
        protected ACLMessage prepareResultNotification(ACLMessage cfp, ACLMessage propose,ACLMessage accept) throws FailureException {
            System.out.println("Agent '"+getLocalName()+"' accepts proposal and is about to perform an action");
            if (performAction()) {
                System.out.println("Agent '"+getLocalName()+"' succesfully performs action");
                ACLMessage inform = accept.createReply();
                inform.setPerformative(ACLMessage.INFORM);
                return inform;
            }
            else {
                System.out.println("Agent '"+getLocalName()+"' failed to perform action");
                throw new FailureException("unexpected-error");
            }
        }
        
        protected void handleRejectProposal(ACLMessage reject)
        {
                System.out.println("Agent '"+getLocalName()+"' rejects proposal");
        }
    }

    protected void setup()
    {
        System.out.println("Agent '"+getLocalName()+"' is waiting for a CFP...");
        Object[] args = getArguments();         
        quantityPollutantWaste = Integer.parseInt(args[0].toString());
        performs = args[1].toString();
        evaluation = Integer.parseInt( args[2].toString());

        //Create a message template for the CFP
        MessageTemplate template = MessageTemplate.and(
        MessageTemplate.MatchProtocol(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET),
        MessageTemplate.MatchPerformative(ACLMessage.CFP) );

        addBehaviour(new IndustryAgentBehaviour(this, template));
    }
        
}
