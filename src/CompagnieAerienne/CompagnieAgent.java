package CompagnieAerienne;

import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class CompagnieAgent extends GuiAgent {
    //gui: objet pour loger les messages de l'agent compagnie
    private transient CompagnieUI gui;
    @Override
    protected void setup() {
        if (getArguments().length==1){
            gui= (CompagnieUI) getArguments()[0];
            gui.setCompagnieAgent(this);
        }

        // donner un comportement au agent
        ParallelBehaviour parallelBehaviour =new ParallelBehaviour();
        addBehaviour(parallelBehaviour);


        parallelBehaviour.addSubBehaviour(new CyclicBehaviour() {

            @Override
            public void action() {
                MessageTemplate messageTemplate = MessageTemplate.MatchAll();
                ACLMessage aclMessage = receive(messageTemplate);

                if (aclMessage!=null){
                    switch(aclMessage.getPerformative()){
                        case ACLMessage.INFORM:
                            gui.logMessage(aclMessage);
                            break;



                        default:
                            break;
                    }
                }
            }
        });


    }
    @Override
    protected void onGuiEvent(GuiEvent message) {
        int messageType = message.getType();
        String content = message.getParameter(0).toString();

        ACLMessage msgToGst = new ACLMessage(ACLMessage.REQUEST);
        msgToGst.setContent(content);
        msgToGst.addReceiver(new AID("GESTIONNAIRE", AID.ISLOCALNAME));
        if (messageType == 1){
            msgToGst.setPerformative(1);
            gui.logMessage(msgToGst);
            send(msgToGst);
        }
        if (messageType == 2){
            msgToGst.setPerformative(14);
            gui.logMessage(msgToGst);
            send(msgToGst);
        }


    }
}
