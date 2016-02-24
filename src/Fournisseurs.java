import jade.core.Agent;
import jade.wrapper.AgentContainer;

/***************************************************************************
	Agent fournisseur
	 - répond au demandes de tarifs, d'abonnement et de désabonnement
	 - réagit aux préventions de l'agent horloge
	 - cree des agents transporteurs d'electricite et peut aussi se servir de l'agent transporteur principal
	 - facture ses clients
****************************************************************************/

public class Fournisseurs extends Agent{
	Fonctions f = new Fonctions();
	Fournisseurs(Fonctions f){
		this.f = f;
	}
	public void setup(){
		addBehaviour(new FournisseursBehaviour(f));
	}
}
