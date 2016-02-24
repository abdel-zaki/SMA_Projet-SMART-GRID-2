import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;

/***************************************************************************
	behaviour de l'agent visualiseur
	 - permet d'executer la tâche de l'agent visualiseur
****************************************************************************/

public class VisualiseurBehaviour extends CyclicBehaviour{
	HashMap<String, String[]> donnees;
	HashMap<String, String[]> map = new HashMap<String, String[]>();
	List<Entry<String, String[]>> entries = new ArrayList<Entry<String, String[]>>();
	Fonctions f = new Fonctions();
	Interface inter;
	int cmp = 0;
	int nbFourn;
	VisualiseurBehaviour(Interface inter, int nbFourn){
		this.inter = inter;
		this.nbFourn = nbFourn;
	}
	public void action() {
		ACLMessage msg = myAgent.receive();
		Object reponse = new Object();
		if(msg != null){
			try {
				reponse = msg.getContentObject();
			} catch (UnreadableException e) {
				reponse = msg.getContent();
			}
			// AFFICHER LES INFORMATIONS RECUS DU FOURNISSEUR
			if(reponse instanceof HashMap<?, ?>){
				donnees = (HashMap<String, String[]>) reponse;
				map.putAll(donnees);
				cmp++;
				if(cmp == nbFourn){
					// Ajout des entrées de la map à une liste
					entries = new ArrayList<Entry<String, String[]>>(map.entrySet());
					// Tri de la liste sur la valeur de l'entrée
					Collections.sort(entries, new Comparator<Entry<String, String[]>>() {
						public int compare(final Entry<String, String[]> e1, final Entry<String, String[]> e2) {
							return e1.getKey().compareTo(e2.getKey());
						}
					});
					inter.entries = entries;
					inter.repaint();
					cmp = 0;
				}
			}
			if(reponse instanceof String[]){
				String tab [] = (String[])reponse;
				inter.lblNbFournVal.setText(tab[0]);
				inter.lblNbConsomVal.setText(tab[1]);
				inter.nbRows = Integer.parseInt(tab[0]);
				inter.boolStart = true;
				inter.repaint();
			}
		}
	}
}