import jade.core.Agent;


/***************************************************************************
	agent visualiseur
	 - affiche les informations à jour sur le fournisseur et ses cients (à chaque tic d'horloge)
****************************************************************************/

public class Visualiseur extends Agent{
	Fonctions f = new Fonctions();
	Interface inter;
	int nbFourn;
	Visualiseur(Interface inter, int nbFourn){
		this.inter = inter;
		this.nbFourn = nbFourn;
	}
	public void setup(){
		addBehaviour(new VisualiseurBehaviour(inter, nbFourn));
	}
}
