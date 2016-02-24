import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;


/***************************************************************************
	Agent horloge
	 - attend que tout les consommateurs soient abonnées
	 - PREVIENT LES FOURNISSEURS DE CONTACTER LEUR TRANSPORTEURS D'ELECTRICITE
****************************************************************************/

public class Horloge extends Agent{
	Fonctions f = new Fonctions();
	int nbrConsom;
	DFAgentDescription [] lesConsommateurs;
	Horloge(int nbrConsom){
		this.nbrConsom = nbrConsom;
	}
	public void setup(){
		addBehaviour(new HorlogeBehaviourInscr(nbrConsom));
		addBehaviour(new HorlogeBehaviour(this, nbrConsom));
	}
}
