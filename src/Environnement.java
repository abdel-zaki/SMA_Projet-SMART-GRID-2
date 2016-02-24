import jade.core.Agent;
import jade.wrapper.StaleProxyException;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/***************************************************************************
	Classe principale contenant la methode main()
	 - instancie la classe Interface et crée les agents Fournisseurs, Consommateurs, Transporteur principal, horloge et Observateur
	 - inscrit les agents consommateurs et fournisseur(s) dans DF
****************************************************************************/

public class Environnement extends Agent{
	static Fonctions fo = new Fonctions();
	DFAgentDescription dfd = new DFAgentDescription();
	ServiceDescription sd = new ServiceDescription();
	static Interface inter = new Interface();
	static JFrame f;
	Fonctions fonc = new Fonctions();
	int nbFourn = 5;
	int nbConsom = 40;
	Environnement(){
		
	}
	public static void main(String[]args) throws StaleProxyException{
		Environnement me = new Environnement();
		// L'INTERFACE
		f = new JFrame("Smart Grid v2.0");
		f.setLayout(null);
		f.setContentPane(inter);
		f.setBackground(Color.getHSBColor(0,0,0.4f));
		Dimension SizeEcran = Toolkit.getDefaultToolkit().getScreenSize();
		f.setBounds((SizeEcran.width - 915) / 2, (SizeEcran.height - 490) / 2,915,490);
		f.setResizable(false);
		f.setDefaultCloseOperation(3);
		// AFFICHER L'INTERFACE
		f.show();
		// INITIALISER L'ENVIRONEMENT
		fo.init();
		fo.createAgent(me, "environnement");
	}
	public void setup(){
		// CREER L'AGENT VISUALISEUR
		Visualiseur visualiseur = new Visualiseur(inter, nbFourn);
		fo.createAgent(visualiseur, "visualiseur");
		// CREER L'AGENT HORLOGE
		Horloge horloge = new Horloge(nbConsom);
		fo.createAgent(horloge, "horloge");
		// CREER L'AGENT TRANSPORTEUR PRINCIPALE
		TransporteurPrincipal transporteurPrincipal = new TransporteurPrincipal();
		fo.createAgent(transporteurPrincipal, "transporteurPrincipal");
		// CREER LES AGENTS FOURNISSEURS
		for(int i=1; i<=nbFourn; i++){
			Fournisseurs fournisseur = new Fournisseurs(fo);
			fo.createAgent(fournisseur, "fournisseur"+fo.addZeros(i));
			// AJOUTER L'AGENT FOURNISSEUR DANS DF
			fo.setPageJaune(this, fournisseur.getAID(), "fournisseur", "f"+i);
		}
		// CREER LES AGENTS CONSOMMATEURS
		for(int i=1; i<=nbConsom; i++){
			Consommateurs consommateur = new Consommateurs();
			consommateur.producteur = fo.randBool();
			if(consommateur.producteur) consommateur.prodKWh = (fonc.rand(0.25f,1.5f));
			else consommateur.prodKWh = 0;
			fo.createAgent(consommateur, "consommateur"+fo.addZeros(i));
			// AJOUTER L'AGENT CONSOMMATEUR DANS DF
			fo.setPageJaune(this, consommateur.getAID(), "consommateur", "consommateur"+i);
		}
		// AJOUTER UN BEHAVIOUR A L'AGENT ENVIRONNEMENT
		addBehaviour(new EnvironnementBehaviour(nbFourn, nbConsom));
	}
}
