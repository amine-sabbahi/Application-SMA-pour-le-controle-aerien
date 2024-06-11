package Gestionnaire_Trafic;

import jade.core.behaviours.CyclicBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class gestionnaireAgent extends GuiAgent {

    protected  gestionnaireUI gui;


    @Override
    protected void setup() {
        // Initialize the GUI
        gui = (gestionnaireUI) getArguments()[0];

        // Add a behavior to receive messages from CompagnieAgent
        addBehaviour(new CyclicBehaviour(this) {
            @Override
            public void action() {
                // Create a template to match messages of type REQUEST
                MessageTemplate template = MessageTemplate.MatchAll();
                ACLMessage msg = receive(template);
                if (msg != null) {
                    // Handle different message types

                            handleRequestMessage(msg);

                        // Add cases for other message types if needed

                } else {
                    // If no message received, block and wait for incoming messages
                    block();
                }
            }
        });

    }

    private void handleRequestMessage(ACLMessage msg) {
        // Get the content of the message
        String content = msg.getContent();
        // Log the message content
        gui.logMessage(msg);
        // Formulate a reply based on the message type
        String replyContent;

        if(msg.getPerformative()==1){
            replyContent = "Gestionnaire : Votre réservation a bien été effectué !";
            // Create a reply message
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(replyContent);
            gui.logMessage(reply);
            // Send the reply
            send(reply);
        }
        if(msg.getPerformative()==14){
            replyContent = "Gestionnaire :  Désolé, cet horaire est déjà rempli. Veuillez choisir un autre horaire !";
            // Create a reply message
            ACLMessage reply = msg.createReply();
            reply.setPerformative(ACLMessage.INFORM);
            reply.setContent(replyContent);
            gui.logMessage(reply);
            // Send the reply
            send(reply);
        }
        if(msg.getPerformative()==2){
            replyContent = "Gestionnaire :  D'accord c'est noté !";
            // Create a reply message
            ACLMessage reply = msg.createReply();
            reply.setPerformative(3);
            reply.setContent(replyContent);
            gui.logMessage(reply);
            // Send the reply
            send(reply);
        }

 }

    @Override
    protected void onGuiEvent(GuiEvent guiEvent) {

    }
}
