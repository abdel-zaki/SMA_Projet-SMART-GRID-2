import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.swing.*;

/***************************************************************************
JPanel pour afficher les informations liées au fournisseur :
 - Fournisseurs
 - Nbr clients
 - KWh achetes
 - KWh consommes
 - Chiffre d'affaire
 - Benefices
 - Nbr Transporteurs
****************************************************************************/

public class Interface extends JPanel{
	List<Entry<String, String[]>> entries = new ArrayList<Entry<String, String[]>>();
	JLabel lblNbConsom = new JLabel("Nombre de consommateurs : ");
	JLabel lblNbConsomVal = new JLabel("0");
	JLabel lblNbFourn = new JLabel("Nombre de fournisseurs : ");
	JLabel lblNbFournVal = new JLabel("0");
	JLabel lblAccueil = new JLabel("Abonnement des consommateurs ...");
	boolean boolStart = false;
	boolean boolData = true;
	String[] entetes = {"Fournisseurs", "Nbr clients", "KWh achetes", "KWh consommes", "Chiffre d'affaire", "Benefices", "Nbr Transporteurs"};
	int nbCols = 7;
	int nbRows;
	int cmp = 0;
	Object[][] tDonnees;
	JTable tableau;
	JScrollPane sp;
	boolean boolFirst = true;
	Interface(){
		lblAccueil.setAlignmentX(CENTER_ALIGNMENT);
		lblAccueil.setBounds(200,160,600,50);
		Font f = new Font("",0,30);
		Color c = new Color(255,255,255);
		lblAccueil.setFont(f);
		lblAccueil.setForeground(c);
		lblNbConsom.setBounds(70,20,230,30);
		lblNbConsomVal.setBounds(280,20,100,30);
		lblNbFourn.setBounds(320,20,280,30);
		lblNbFournVal.setBounds(510,20,100,30);
		setLayout(null);
		setBackground(Color.getHSBColor(0,0,0.8f));
	}
	public void paintComponent(Graphics g)
	{
		remove(lblAccueil);
		if(!boolStart){
			add(lblAccueil);
			return;
		}
		super.paintComponent(g);
		
		add(lblNbConsom);
		add(lblNbConsomVal);
		add(lblNbFourn);
		add(lblNbFournVal);
		
		if(boolData){
			boolData = false;
			tDonnees = new Object[nbRows][nbCols];
		}
		
		nbRows = entries.size();
		for (final Entry<String, String[]> entry : entries){
			for(int i=0; i<nbCols; i++){
				tDonnees[cmp][i] = entry.getValue()[i];
			}
			cmp++;
		}
		cmp = 0;
		tableau = new JTable(tDonnees, entetes);
		tableau.setEnabled(false);
		tableau.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		for(int i=0; i<tableau.getColumnModel().getColumnCount(); i++){
			tableau.getColumnModel().getColumn(i).setResizable(false);
		}
		tableau.setRowHeight(24);
		tableau.getColumnModel().getColumn(0).setPreferredWidth(120);
		tableau.getColumnModel().getColumn(1).setPreferredWidth(90);
		tableau.getColumnModel().getColumn(2).setPreferredWidth(120);
		tableau.getColumnModel().getColumn(3).setPreferredWidth(140);
		tableau.getColumnModel().getColumn(4).setPreferredWidth(130);
		tableau.getColumnModel().getColumn(5).setPreferredWidth(120);
		tableau.getColumnModel().getColumn(6).setPreferredWidth(150);
		tableau.getTableHeader().setFont(new Font("Arial",Font.BOLD,12));
		
		sp = new JScrollPane(tableau);
		sp.setBounds(20, 70, 880, 410);
		if(boolFirst){
			boolFirst = false;
			add(sp);
		}
	}
}
