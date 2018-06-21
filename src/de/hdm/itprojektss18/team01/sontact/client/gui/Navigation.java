package de.hdm.itprojektss18.team01.sontact.client.gui;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTree;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

import de.hdm.itprojektss18.team01.sontact.shared.bo.Nutzer;

/**
 * Diese Klasse stellt das Herzstück der GUI dar.
 * 
 * Hier wird die <code>SontactTreeViewModel</code>-Klasse 
 * eingebunden und die Buttons für die Erstellung von neunen
 * <code>Kontakt</code> oder <code>Kontaktliste</code>-Objekten
 * realsiert.
 * 
 * @author Ugur Bayrak, Kevin Batista, Dennis Lehle
 *
 */

public class Navigation extends VerticalPanel {
	
	HorizontalPanel hp = new HorizontalPanel();
	Button neueKontaktlisteBtn = new Button("<image src='/images/kontaktliste.png' width='25px' height='25px' align='center'/>" + "<image src='/images/plus.png' width=22px' height='22px' align='center' />");
	Button neuerKontaktBtn = new Button("<image src='/images/user.png' width='25px' height='25px' align='center' />"+ "<image src='/images/plus.png' width=22px' height='22px' align='center' />");
	
	/**
	 * Konstruktor der Navigations-Klasse.
	 */
	public Navigation(final Nutzer nutzer) {

		/**
		 * ScrollPanel für den Baum.
		 */
		ScrollPanel sc = new ScrollPanel();
		sc.setSize("200px", "550px");
		sc.setVerticalScrollPosition(10);
		
		
		
		 //Anlegen des Baumes für die Navigation in Kontaktlisten.
		SontactTreeViewModel navTreeModel = new SontactTreeViewModel(nutzer);
	
		CellTree navTree = new CellTree(navTreeModel, null);

		//ClickHandler für das erstellen neuer Kontaktlisten.
		neueKontaktlisteBtn.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				//Wird instanziiert wenn eine neue Kontaktliste erstellt werden soll.
				RootPanel.get("content").clear();
				RootPanel.get("content").add(new KontaktlisteForm(nutzer));
			}

		});
		
		//ClickHandler für das erstellen neuen Kontakten.
		neuerKontaktBtn.addClickHandler(new ClickHandler() {
	
			@Override
			public void onClick(ClickEvent event) {
				RootPanel.get("content").clear();
				RootPanel.get("content").add(new KontaktForm(nutzer));
			
			}
		});
		
		//Das TreeViewModel dem ScrollPanel hinzufügen
		sc.add(navTree);
		
		//Größe der Buttons setzen
		neueKontaktlisteBtn.setPixelSize(100, 60); 
		neueKontaktlisteBtn.setStyleName("button1");
		neueKontaktlisteBtn.setTitle("Neue Kontaktliste erstellen");
	
		neuerKontaktBtn.setPixelSize(100, 60);
		neuerKontaktBtn.setStyleName("button1");
		neuerKontaktBtn.setTitle("Neuen Kontakt erstellen");
		
		hp.add(neueKontaktlisteBtn);
		hp.add(neuerKontaktBtn);
		this.add(hp);
		
		//Header für die Kontaktlisten setzen.
		this.add(new HTML("<center><h5>Meine Kontaktlisten</h5></center>"));
	
		//Das ScrollPanel dem VerticalPanel hinzufügen (this = der Klasse selbst)
		this.add(sc);

	}

}
