import jade.core.Agent;
import jade.domain.FIPAAgentManagement.DFAgentDescription;

/***************************************************************************
	L'agent transporteur principal d'électricité
	 - facture ses clients (fournisseurs)
****************************************************************************/

public class TransporteurPrincipal extends Agent{
	TransporteurPrincipal(){
		
	}
	public void setup(){
		addBehaviour(new TransporteurPrincipalBehaviour());
	}
}