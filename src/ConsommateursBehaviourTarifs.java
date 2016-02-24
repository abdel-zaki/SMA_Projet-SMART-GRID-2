import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;

import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;

/***************************************************************************
	Behaviour des agents Consommateurs
	 - DEMANDE LES TARIFS D'ABONNEMENT AU FOURNISSEUR
****************************************************************************/

public class ConsommateursBehaviourTarifs extends OneShotBehaviour {
	DFAgentDescription [] lesFournisseurs;
	ACLMessage msgFournisseur = new ACLMessage(ACLMessage.INFORM);
	ConsommateursBehaviourTarifs(DFAgentDescription [] lesFournisseurs){
		this.lesFournisseurs = lesFournisseurs;
	}
	public void action() {
		// DEMANDER LES TARIFS AU FOURNISSEUR
		for(int i=0; i<lesFournisseurs.length;i++){
			msgFournisseur.clearAllReceiver();
			msgFournisseur.addReceiver(new AID(lesFournisseurs[i].getName().getLocalName(), AID.ISLOCALNAME));
			String obj = "tarifs";
			msgFournisseur.setContent(obj);
			myAgent.send(msgFournisseur);
		}
	}
}
