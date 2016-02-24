import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/***************************************************************************
	behaviour de l'agent horloge
	 - permet d'executer les tâches de l'agent horloge
****************************************************************************/

public class HorlogeBehaviour extends TickerBehaviour{
	Fonctions f = new Fonctions();
	ACLMessage msgFournisseur = new ACLMessage(ACLMessage.INFORM);
	ACLMessage msgConsommateur = new ACLMessage(ACLMessage.INFORM);
	int nbrConsom;
	DFAgentDescription [] lesFournisseurs;
	DFAgentDescription [] lesConsommateurs;
	Interface inter;
	String obj = "";
	String senderID = "";
	int nbrConsomInscr = 0;
	boolean boolAttente = true;
	boolean boolFourn = true;
	boolean start = false;
	HorlogeBehaviour(Agent agt, int nbrConsom){
		super(agt, 1500);
		this.nbrConsom = nbrConsom;
	}
	public void onTick() {
		if(boolAttente){
			ACLMessage msg = myAgent.receive();
			Object reponse = "";
			if(msg != null){
				try {
					reponse = msg.getContentObject();
				} catch (UnreadableException e) {
					reponse = msg.getContent();
				}
				senderID = msg.getSender().getLocalName();
				if(reponse instanceof String){
					String rep = (String)reponse;
					// TOUT LES CONSOMMATEURS SONT ILS ABONNÉS ?
					if(rep.equals("commencer")){
						boolAttente = false;
						start = true;
					}
				}
			}
		}
		if(start){
			// PREVENIR LES FOURNISSEURS D'ALLER CHERCHER UN TRANSPORTEUR D'ELECTRICITE
			if(boolFourn){
				boolFourn = false;
				lesFournisseurs = f.getPageJaune("fournisseur", getAgent());
				lesConsommateurs = f.getPageJaune("consommateur", getAgent());
			}
			msgFournisseur.clearAllReceiver();
			for(int i=0; i<lesFournisseurs.length; i++){
				msgFournisseur.addReceiver(new AID(lesFournisseurs[i].getName().getLocalName(), AID.ISLOCALNAME));
			}
			obj = "debut de tour";
			msgFournisseur.setContent(obj);
			myAgent.send(msgFournisseur);
			// PREVENIR LES CONSOMMATEURS DE REESTIMER LEURS CAPACITES DE PRODUCTION D'ELECTRICITE
			msgConsommateur.clearAllReceiver();
			for(int i=0; i<lesConsommateurs.length; i++){
				msgConsommateur.addReceiver(new AID(lesConsommateurs[i].getName().getLocalName(), AID.ISLOCALNAME));
			}
			obj = "change_prix_prod";
			msgConsommateur.setContent(obj);
			myAgent.send(msgConsommateur);
		}
	}
}