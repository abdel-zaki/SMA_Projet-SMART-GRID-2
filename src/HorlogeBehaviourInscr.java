import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


/***************************************************************************
	behaviour de l'agent horloge
	 - attend que tout les consommateurs soient abonnées
****************************************************************************/

public class HorlogeBehaviourInscr extends CyclicBehaviour{
	Fonctions f = new Fonctions();
	ACLMessage msgHorloge = new ACLMessage(ACLMessage.INFORM);
	int nbrConsom;
	String obj;
	String senderID = "";
	int nbrConsomInscr = 0;
	boolean boolInscrit = true;
	HorlogeBehaviourInscr(int nbrConsom){
		this.nbrConsom = nbrConsom;
	}
	public void action() {
		if(boolInscrit){
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
					if(rep.equals("je suis inscrit")){
						nbrConsomInscr++;
						if(nbrConsomInscr == nbrConsom){
							boolInscrit = false;
							msgHorloge.clearAllReceiver();
							msgHorloge.addReceiver(new AID("Horloge", AID.ISLOCALNAME));
							obj = "commencer";
							msgHorloge.setContent(obj);
							myAgent.send(msgHorloge);
						}
					}
				}
			}
		}
	}
}
