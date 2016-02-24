import java.io.IOException;
import java.util.HashMap;
import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;


/***************************************************************************
	Behaviour des agents Consommateurs
	 - permet d'executer les tâches d'un agent consommateur à partir de la réception des tarifs du fournisseur
****************************************************************************/

public class ConsommateursBehaviour extends CyclicBehaviour {
	float moyenneProdKWh = 130;
	Fonctions f = new Fonctions();
	DFAgentDescription [] lesFournisseurs;
	HashMap<String, Float> mapTarifs = new HashMap<String, Float>();
	String monFournisseur = "";
	String senderID = "";
	float prodKWhTmp = 0;
	float prodKWh = 0;
	boolean producteur = false;
	boolean boolinscr = true;
	boolean boolFacture = true;
	float valRand;
	int desabonner = 0;
	ACLMessage msgFournisseur = new ACLMessage(ACLMessage.INFORM);
	ACLMessage msgHorloge = new ACLMessage(ACLMessage.INFORM);
	ConsommateursBehaviour(DFAgentDescription [] lesFournisseurs, float prodKWh, boolean producteur){
		this.lesFournisseurs = lesFournisseurs;
		this.prodKWhTmp = prodKWh;
		this.producteur = producteur;
	}
	public void action() {
		// RECUPERATION DES [TARIFS & CONFIRMATIONS D'ABONNEMENT ET DESABONNEMENT]
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
				// CONFIRMATIONS D'ABONNEMENT
				if(rep.equals("abonOK")){
					monFournisseur = senderID;
					boolFacture = true;
					// INFORMER L'HORLOGE QUE JE SUIS INSCRIT
					msgHorloge.clearAllReceiver();
					msgHorloge.addReceiver(new AID("horloge", AID.ISLOCALNAME));
					msgHorloge.setContent("je suis inscrit");
					myAgent.send(msgHorloge);
				}
				// CONFIRMATIONS DE DESABONNEMENT
				if(rep.equals("desabonOK")){
					sabonner();
				}
				// ON CHANGE LE TARIF DE PRODUCTION D'ELECTRICITE A CHAQUE TIC D'HORLOGE
				if(rep.equals("change_prix_prod")){
					if(!monFournisseur.isEmpty()){
						if(producteur){
							prodKWh += f.rand(-2, 2);
							if(prodKWh < 5) prodKWh = 5;
						}
						msgFournisseur.clearAllReceiver();
						msgFournisseur.addReceiver(new AID(monFournisseur, AID.ISLOCALNAME));
						String [] obj = {"prodKWh",""+prodKWh};
						try{
							msgFournisseur.setContentObject(obj);
							myAgent.send(msgFournisseur);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
			if(reponse instanceof String []){
				String [] rep = (String [])reponse;
				float val = Float.parseFloat(rep[1]);
				// LES TARIFS
				if(rep[0].equals("tarifs")){
					if(mapTarifs.size() < lesFournisseurs.length){
						mapTarifs.put(senderID, val);
						// RECEPTION DES TARIFS DE TOUT LES FOURNISSEURS
						if(mapTarifs.size() == lesFournisseurs.length){
							// DEMANDE D'ABONNEMENT
							sabonner();
						}
					}
				}
				// RECEPTION DE LA FACTURE
				if(rep[0].equals("facture_clients")){
					if(boolFacture){
						// DECIDER DE CHANGER DE FOURNISSEUR OU NON
						valRand = f.rand(0, 60);
						if(valRand < 30.9 && valRand > 30.5){
							desabonner = 1;
							monFournisseur = "";
							boolFacture = false;
						}
						else desabonner = 0;
						msgFournisseur.clearAllReceiver();
						msgFournisseur.addReceiver(new AID(senderID, AID.ISLOCALNAME));
						String obj [] = {"facture_clients_recue", ""+desabonner};
						try{
							msgFournisseur.setContentObject(obj);
							myAgent.send(msgFournisseur);
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		else block();
	}
	// S'ABONNER CHEZ UN FOURNISSEUR
	void sabonner(){
		// DEMANDE D'ABONNEMENT
		int index = (int)f.rand(1, lesFournisseurs.length);
		String fournisChoisi = lesFournisseurs[index-1].getName().getLocalName();
		prodKWh = prodKWhTmp * moyenneProdKWh;
		msgFournisseur.clearAllReceiver();
		msgFournisseur.addReceiver(new AID(fournisChoisi, AID.ISLOCALNAME));
		String [] obj = {"abonner", ""+prodKWh};
		try{
			msgFournisseur.setContentObject(obj);
			myAgent.send(msgFournisseur);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
