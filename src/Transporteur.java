import jade.core.Agent;


/***************************************************************************
	L'agent transporteur d'électricité (crée par un fournisseur)
	 - repond à la demande de transporter l'electricite
****************************************************************************/

public class Transporteur extends Agent{
	float prix = 10.15f;
	String monCreateur = "";
	Transporteur(String monCreateur){
		this.monCreateur = monCreateur;
	}
	public void setup(){
		addBehaviour(new TransporteurBehaviour(monCreateur));
		addBehaviour(new TransporteurBehaviourSetup(monCreateur));
	}
	protected void takeDown(){
		
	}
}
