import java.io.IOException;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/***************************************************************************
	Behaviour de l'agent TransporteurPrincipal
	 - permet d'executer les tâches de l'agent TransporteurPrincipal
****************************************************************************/

public class TransporteurPrincipalBehaviour extends CyclicBehaviour {
	Fonctions f = new Fonctions();
	Object demande;
	String senderID = "";
	String ob = "";
	float kwhATrans;
	float facture = 0;
	float prixTransKWh = 0.08f;
	ACLMessage msgReponse = new ACLMessage(ACLMessage.INFORM);
	TransporteurPrincipalBehaviour(){
		
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
			if(demande instanceof String){
				// UN FOURNISSEUR M'ENVOI UNE QUANTITE D'ELECTRICITE (KWh) A TRANSMETTRE
				kwhATrans = Float.parseFloat(demande.toString());
				msgReponse.clearAllReceiver();
				// FACTURER LE FOURNISSEUR
				msgReponse.addReceiver(new AID(senderID, AID.ISLOCALNAME));
				String tab [] = {"facture transport",f.arrondir(kwhATrans*prixTransKWh)+""};
				try {
					msgReponse.setContentObject(tab);
					myAgent.send(msgReponse);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
