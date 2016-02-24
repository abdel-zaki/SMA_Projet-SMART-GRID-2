import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/***************************************************************************
	Behaviour de l'agent Transporteur (crée par un fournisseur)
	 - permet d'executer les tâches de l'agent Transporteur
****************************************************************************/

public class TransporteurBehaviour extends CyclicBehaviour {
	String monCreateur = "";
	Object demande;
	String obj = "";
	String senderID = "";
	float [] listConsommations;
	ACLMessage msgReponse = new ACLMessage(ACLMessage.INFORM);
	TransporteurBehaviour(String monCreateur){
		this.monCreateur = monCreateur;
	}
	public void action() {
		ACLMessage msg = myAgent.receive();
		if(msg != null){
			try {
				demande = msg.getContentObject();
			} catch (UnreadableException e) {
				demande = msg.getContent();
			}
			senderID = msg.getSender().getLocalName();
			// JE N'ACCEPTE QUE LES DEMANDES DE MON PROPRE CREATEUR
			if(senderID.equals(monCreateur)){
				if(demande instanceof String){
					if(demande.toString().equals("honorer contrat")){
						msgReponse.clearAllReceiver();
						msgReponse.addReceiver(new AID(senderID, AID.ISLOCALNAME));
						msgReponse.setContent("honorer");
						myAgent.send(msgReponse);
					}
				}
			}
		}
	}
}