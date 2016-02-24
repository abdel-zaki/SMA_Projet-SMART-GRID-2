import java.io.IOException;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

/***************************************************************************
	Behaviour de l'agent environnement
	 - envoi le nombre de fournisseur et de consommateurs à l'agent visualiseur
****************************************************************************/

public class EnvironnementBehaviour extends OneShotBehaviour{
	int nbFourn;
	int nbConsom;
	ACLMessage msgReponse = new ACLMessage(ACLMessage.INFORM);
	public EnvironnementBehaviour(int nbFourn, int nbConsom){
		this.nbFourn = nbFourn;
		this.nbConsom = nbConsom;
	}
	public void action(){
		msgReponse.clearAllReceiver();
		msgReponse.addReceiver(new AID("visualiseur", AID.ISLOCALNAME));
		String ob [] = {nbFourn+"",nbConsom+""};
		try {
			msgReponse.setContentObject(ob);
			myAgent.send(msgReponse);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
