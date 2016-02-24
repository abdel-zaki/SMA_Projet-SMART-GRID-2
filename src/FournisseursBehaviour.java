import jade.core.AID;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/***************************************************************************
	Behaviour de l'agent Fournisseur
	 - permet d'executer les deux tâches d'un agent fournisseur
****************************************************************************/

public class FournisseursBehaviour extends CyclicBehaviour{
	Fonctions f;
	HashMap<String, String[]> donnees = new HashMap<String, String[]>();
	ACLMessage msgReponse = new ACLMessage(ACLMessage.INFORM);
	// Prix / unité
	float tarifsAbonne = 0.32f;
	float prixKWh = 0.15f;
	float TabNbrKWh [];
	float prixCreationAgent = 40;
	float prixTransKWh = 0.08f;
	float capacite = 80;
	HashMap<String, Float> consommations = new HashMap<String, Float>();
	float consommationTotal = 0;
	String obj = "";
	String senderID = "";
	ArrayList<String> transporteurs = new ArrayList<String>();
	String transporteurTemp = "";
	Object demande = "";
	HashMap<String, Float> prodClients = new HashMap<String, Float>();
	float prodClientTotal = 0;
	float chiffreA = 0;
	float benefices = 0;
	String thisAgent;
	boolean boolCreeTrans = true;
	int nbrTransporteurs = 0;
	int nbrTransUtilises = 0;
	int nbrTransrep = 0;
	FournisseursBehaviour(Fonctions f){
		this.f = f;
	}
	public void action(){
		thisAgent = getAgent().getLocalName();
		// RECEPTION DES DEMANDES DE [TARIFS, ABONNEMENT & DESABONNEMENT]
		ACLMessage msg = myAgent.receive();
		if(msg != null){
			try {
				demande = msg.getContentObject();
			} catch (UnreadableException e) {
				demande = msg.getContent();
			}
			senderID = msg.getSender().getLocalName();
			if(demande instanceof String){
				msgReponse.clearAllReceiver();
				msgReponse.addReceiver(new AID(senderID, AID.ISLOCALNAME));
				String demand = (String)demande;
				// REPONSE A LA DEMANDE DES TARIFS
				if(demand.equals("tarifs")) {
					String [] ob = {"tarifs", tarifsAbonne+""};
					try {
						msgReponse.setContentObject(ob);
						myAgent.send(msgReponse);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				// L'HORLOGE ME PREVIENT DE CONTACTER MON TRANSPORTEUR D'ELECTRICITE
				if(demand.equals("debut de tour")){
					chiffreA = 0;
					benefices = 0;
					// DECIDER D'UTILISER LE RESEAU PRINCIPAL OU PAS
					utiliserReseauPrinc();
				}
				// LES AGENTS TRANSPORTEURS CONFIRMENT QU'ILS HONORENT TOUJOURS LE CONTRAT
				if(demand.equals("honorer")){
					nbrTransrep++;
					if(nbrTransrep >= nbrTransUtilises){
						facturationClients();
						nbrTransrep = 0;
					}
				}
			}
			if(demande instanceof String []){
				String [] demand = (String [])demande;
				float val = Float.parseFloat(demand[1]);
				msgReponse.clearAllReceiver();
				msgReponse.addReceiver(new AID(senderID, AID.ISLOCALNAME));
				// ABONNER UN CONSOMMATEUR
				if(demand[0].equals("abonner")){
					prodClients.put(senderID,val);
					obj = "abonOK";
					msgReponse.setContent(obj);
					myAgent.send(msgReponse);
				}
				// UN CONSOMMATEUR VIENT DE METTRE A JOUR SA CAPACITE
				if(demand[0].equals("prodKWh")){
					prodClients.put(senderID, val);
				}
				// L'AGENT TRANSPORTEUR PRINCIPAL M'ENVOI LA FACTURE
				if(demand[0].equals("facture transport")){
					benefices -= val;
					nbrTransrep++;
					if(nbrTransrep >= nbrTransUtilises){
						facturationClients();
						nbrTransrep = 0;
					}
				}
				// CONFIRMATION DE LA RECEPTION DE LA FACTURE DE LA PART DU CLIENT
				if(demand[0].equals("facture_clients_recue")){
					float facture = consommations.get(senderID)*prixKWh - prodClients.get(senderID)*prixKWh;
					if(facture >= 0) chiffreA += facture;
					benefices += facture;
					// DESABONNER UN CONSOMMATEUR
					if(val == 1){
						prodClients.remove(senderID);
						consommations.remove(senderID);
						obj = "desabonOK";
						msgReponse.setContent(obj);
						myAgent.send(msgReponse);
					}
					toVisualiseur();
				}
			}
		}
		else block();
	}
	// ENVOYER A L'AGENT VISUALISEUR
	void toVisualiseur(){
		String [] values = {thisAgent, prodClients.size()+"", f.arrondir(prodClientTotal)+"", f.arrondir(consommationTotal)+"", f.arrondir(chiffreA)+"", f.arrondir(benefices)+"", nbrTransporteurs+""};
		donnees.put(thisAgent, values);
		msgReponse.clearAllReceiver();
		msgReponse.addReceiver(new AID("Visualiseur", AID.ISLOCALNAME));
		try {
			msgReponse.setContentObject(donnees);
			myAgent.send(msgReponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	// DECIDER D'UTILISER LE RESEAU PRINCIPAL OU PAS
	void utiliserReseauPrinc(){
		if(boolCreeTrans){
			creerTransporteurs();
			boolCreeTrans = false;
		}
		reestimerConsom();
		consommationTotal = 0;
		prodClientTotal = 0;
		Object [] keys = (Object [])prodClients.keySet().toArray();
		for(int i=0; i<keys.length; i++){
			consommationTotal += consommations.get(keys[i]);
			prodClientTotal += prodClients.get(keys[i]);
		}
		float kwhRestant = consommationTotal + prodClientTotal - (nbrTransporteurs * capacite);
		// CONTACTER L'AGENT TRANSPORTEUR PRINCIPAL
		if(kwhRestant > 0){
			nbrTransUtilises = transporteurs.size()+1;
			msgReponse.clearAllReceiver();
			msgReponse.addReceiver(new AID("transporteurPrincipal", AID.ISLOCALNAME));
			msgReponse.setContent(""+kwhRestant);
			myAgent.send(msgReponse);
		}
		else{
			nbrTransUtilises = transporteurs.size();
			msgReponse.clearAllReceiver();
			for(int i=0; i<transporteurs.size(); i++){
				msgReponse.addReceiver(new AID(transporteurs.get(i), AID.ISLOCALNAME));
			}
			msgReponse.setContent("honorer contrat");
			myAgent.send(msgReponse);
		}
	}
	// FACTURER LES CLIENTS
	void facturationClients(){
		Object [] keys = consommations.keySet().toArray();
		for(int i=0; i<keys.length; i++){
			// Facture = consommation - production
			String ob [] = {"facture_clients", (consommations.get(keys[i])*prixKWh - prodClients.get(keys[i])*prixKWh)+""};
			msgReponse.clearAllReceiver();
			msgReponse.addReceiver(new AID(keys[i].toString(), AID.ISLOCALNAME));
			try {
				msgReponse.setContentObject(ob);
				myAgent.send(msgReponse);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	// REESTIMER LA CONSOMMATION DES CLIENTS
	void reestimerConsom(){
		Object [] keys = prodClients.keySet().toArray();
		// REESTIMER LA CONSOMMATION DE MES CLIENTS (EN KWh)
		float nbrKWh;
		TabNbrKWh = new float[prodClients.size()];
		for(int i=0; i<keys.length; i++){
			nbrKWh = f.rand(100, 160);
			TabNbrKWh[i] = nbrKWh;
			consommations.put((String)keys[i],nbrKWh);
		}
	}
	// CREER DES AGENTS TRANSPORTEURS
	void creerTransporteurs(){
		int nbrClients = prodClients.size();
		float moyenneProdKWh = 114;
		float moyenneConsomKWh = 130;
		float moyenneKWhATransporter = nbrClients * moyenneProdKWh * moyenneConsomKWh;
		nbrTransporteurs = (int)(nbrClients * (moyenneProdKWh + moyenneConsomKWh) / capacite);
		for(int i=0; i<nbrTransporteurs; i++){
			transporteurTemp = "transporteur"+i+"_"+thisAgent;
			transporteurs.add(transporteurTemp);
			Transporteur trans = new Transporteur(getAgent().getLocalName());
			f.createAgent(trans, transporteurTemp);
			benefices -= prixCreationAgent;
		}
	}
}