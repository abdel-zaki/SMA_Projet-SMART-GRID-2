import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;

/***************************************************************************
	classe pour des fonctions utils :
	 - init()         : INITIALISER L'ENVIRONEMENT
	 - createAgent()  : CREE UN NOUVEAU AGENT
	 - getPageJaune() : CHERCHE DANS DF ET RETOURNE UNE LISTE D'AGENTS D'UN TYPE PASSE EN PARAMETRE
	 - setPageJaune() : SAUVEGARDE UN AGENT PASSE EN PARAMETRE DANS DF
	 - createAgent()  : CREE UN NOUVEAU AGENT
	 - rand(MIN, MAX) : RETOURNER UNE VALEUR ALEATOIRE ENTRE MIN et MAX
	 - randBool()     : RETOURNER UNE VALEUR ALEATOIRE TRUE OU FALSE
	 - isNumeric()    : TESTE SI UN STRING EST NUMERIQUE
	 - arrondir()     : ARRONDI UN NOMBRE FLOTTANT EN 2 CHIFFRES APRES LA VIRGULE
	 - addZeros()     : AJOUTER DES ZEROS A GAUCHE D'UN CHIFFRE
****************************************************************************/

public class Fonctions{
	Random rd = new Random();
	DFAgentDescription d = new DFAgentDescription();
	ServiceDescription sd = new ServiceDescription();
	AgentController a = null;
	AgentContainer ac;
	Fonctions(){
		
	}
	// INITIALISER L'ENVIRONEMENT
	void init(){
		Runtime rt = Runtime.instance();
		rt.setCloseVM(true);
		Profile p = new ProfileImpl(null,1234,null);
		ac = rt.createMainContainer(p);
	}
	// CREE UN NOUVEAU AGENT
	void createAgent(Agent agent, String nom){
		try {
			a = ac.acceptNewAgent(nom, agent);
			a.start();
		} catch (StaleProxyException e) {
			e.printStackTrace();
		}
	}
	// RETOURNE UNE LISTE D'AGENTS D'UN TYPE PASSE EN PARAMETRE
	DFAgentDescription [] getPageJaune(String type, Agent agent) {
		DFAgentDescription [] lesFournisseurs;
		try {
			sd.setType(type);
			d.addServices(sd);
			lesFournisseurs = DFService.search(agent, d);
			return lesFournisseurs;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new DFAgentDescription [0];
	}
	// SAUVEGARDE UN AGENT PASSE EN PARAMETRE DANS DF
	void setPageJaune(Agent agent, AID AgentAID, String type, String nom) {
		try{
			d.setName(AgentAID);
			sd.setType(type);
			sd.setName(nom);
			d.addServices(sd);
			DFService.register(agent, d);
		}
		catch(FIPAException e){
			e.printStackTrace();
		}
	}
	// RETOURNER UNE VALEUR ALEATOIRE ENTRE min et MAX
	static float rand(float min, float max){
		return min + (float)Math.random() * (max - min);
	}
	// RETOURNER UNE VALEUR ALEATOIRE TRUE OU FALSE
	boolean randBool(){
		return ((int)rd.nextInt(2)) == 1;
	}
	// TESTE SI UN STRING EST NUMERIQUE
	public boolean isNumeric(String str){
		try
		{
			float d = Float.parseFloat(str);
		}
		catch(NumberFormatException nfe){
			return false;
		}
		return true;
	}
	// ARRONDI UN NOMBRE FLOTTANT EN 2 CHIFFRES APRES LA VIRGULE
	float arrondir(float val){
		return Float.parseFloat(""+(Math.round(val*100.0)/100.0));
	}
	// AJOUTER DES ZEROS A GAUCHE D'UN CHIFFRE
	String addZeros(Integer n){
		switch(n.toString().length()){
			case 1 : return "00"+n;
			case 2 : return "0"+n;
			default : return ""+n;
		}
	}
}