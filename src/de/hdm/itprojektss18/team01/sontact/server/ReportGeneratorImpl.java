package de.hdm.itprojektss18.team01.sontact.server;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import de.hdm.itprojektss18.team01.sontact.shared.EditorService;
import de.hdm.itprojektss18.team01.sontact.shared.report.AlleKontakteNachTeilhabernReport;
import de.hdm.itprojektss18.team01.sontact.shared.report.AlleGeteiltenKontakteReport;
import de.hdm.itprojektss18.team01.sontact.shared.report.AlleKontakteNachEigenschaftenReport;
import de.hdm.itprojektss18.team01.sontact.shared.report.Column;
import de.hdm.itprojektss18.team01.sontact.shared.report.CompositeParagraph;

import de.hdm.itprojektss18.team01.sontact.shared.report.Row;

import de.hdm.itprojektss18.team01.sontact.shared.report.SimpleParagraph;
import de.hdm.itprojektss18.team01.sontact.shared.report.AlleKontakteReport;
import de.hdm.itprojektss18.team01.sontact.shared.bo.*;
import de.hdm.itprojektss18.team01.sontact.server.EditorServiceImpl;
import de.hdm.itprojektss18.team01.sontact.shared.ReportGenerator;
import java.util.Date;
import java.sql.Timestamp;

import java.util.Vector;

/**
 * Implementierung des serverseitigen RPC-Services fuer den prozessierenden
 * Report.
 */
public class ReportGeneratorImpl extends RemoteServiceServlet implements ReportGenerator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private EditorService editorService = null;

	public ReportGeneratorImpl() throws IllegalArgumentException {
	}

	/*
	 * *************************************************************************
	 * ABSCHNITT BEGINN: INITIALISIERUNG
	 * *************************************************************************
	 */
	public void init() throws IllegalArgumentException {

		EditorServiceImpl impl = new EditorServiceImpl();
		impl.init();
		this.editorService = impl;
	}

	protected EditorService getEditorService() {
		return this.editorService;
	}
	/*
	 * *************************************************************************
	 * ABSCHNITT ENDE: INITIALISIERUNG
	 * *************************************************************************
	 */

	/**
	 * Abruf des Reports der alle Kontakte des eingeloggten Nutzers generiert.
	 * 
	 * @param n
	 *            Nutzer von dem der Report generiert werden soll
	 * @return report
	 */
	public AlleKontakteReport createAlleKontakteReport(Nutzer n) throws IllegalArgumentException {

		if (this.getEditorService() == null) {
			return null;
		}

		// Die Erstellung einer Instanz dieses Reports
		AlleKontakteReport report = new AlleKontakteReport();

		// Festlegung des Titels und des Generierungsdatums dieses Reports
		report.setTitle("Alle Kontakte von" + "  " + this.getNutzerByGMail(n) + "  ");
		report.setCreated(new Date());

		// Kopfzeile der Reporttabelle; mit den Ueberschriften der einzelnen Spalten
		Row head = new Row();
		head.addColumn(new Column("Vorname"));
		head.addColumn(new Column("Nachname"));
		head.addColumn(new Column("Erstellungszeitpunkt"));
		head.addColumn(new Column("Modifikationszeitpunkt"));

		// Spalte zur Darstellung des Eigentuemer eines Kontaktes
		head.addColumn(new Column("Kontakteigentuemer"));

		// Kopfzeile dem Report hinzufuegen
		report.addRow(head);

		// Angeforderte Kontaktdaten in den Vektor laden und dem Report hinzufuegen
		Vector<Kontakt> kontakt = new Vector<Kontakt>();

		// Kontakte der folgenden Vektoren aufrufen und dem ersten Vector hinzufuegen
		kontakt.addAll(this.getEditorService().getAllKontakteByOwner(n));
		kontakt.addAll(this.getEditorService().getAllSharedKontakteByReceiver(n.getId()));

		// Die Kontakte des gespeicherten Vectors, pro Zeile der Reporttabelle
		// hinzufuegen

		for (int i = 0; i < kontakt.size(); i++) {
			Vector<Relatable> auspraegungen = new Vector<Relatable>();

			if (kontakt.elementAt(i).getOwnerId() == n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllAuspraegungenByKontaktRelatable(kontakt.elementAt(i).getId()));

			} else if (kontakt.elementAt(i).getOwnerId() != n.getId()) {
				auspraegungen.addAll(
						getEditorService().getAllSharedAuspraegungenByKontaktAndNutzer(kontakt.elementAt(i), n));
			}

			Row kon = new Row();
			kon.addColumn(new Column(kontakt.elementAt(i).getVorname()));
			kon.addColumn(new Column(kontakt.elementAt(i).getNachname()));
			kon.addColumn(new Column(kontakt.elementAt(i).getErstellDat().toString()));
			kon.addColumn(new Column(kontakt.elementAt(i).getModDat().toString()));
			kon.addColumn(
					new Column(getEditorService().getNutzerById(kontakt.elementAt(i).getOwnerId()).getEmailAddress()));
			kon.addColumn(new Column(""));

			// Eine Zeile dem Report uebergeben
			report.addRow(kon);

			Row zwischen = new Row();

			// Abfrage ob der Kontakt ueber Kontakteigenschaften verfuegt
			if (auspraegungen.size() != 0) {

				zwischen.addColumn(new Column("Eigenschaft:"));
				zwischen.addColumn(new Column("Auspraegung:"));
				report.addRow(zwischen);
			}

			// Aufruf der Kontakteigenschaften des Reports, sowie hinzufuegen der
			// zusammengehoerigen Eigenschaften und Auspraegungen pro Zeile
			for (int j = 0; j < auspraegungen.size(); j++) {

				Row e = new Row();

				e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
				e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));
				e.addColumn(new Column(""));
				e.addColumn(new Column(""));
				e.addColumn(new Column(""));

				// hinzufuegen der Kontakteigenschaften zu der untergeordneten Tabelle
				report.addRow(e);

			}
			// Einzelne Zeile dem Report hinzufuegen

		}

		// Rueckgabe der Reportausgabe
		return report;
	}

	/**
	 * Report der alle Kontakte nach Eigenschaften mit ihrer Auspraegung ausgibt.
	 * Die uebergebenen Eigenschaften oder Auspraegungen, werden vom Nutzer mithilfe
	 * der Suchleiste uebergeben. Die daraufhin den Report mit dem entsprechenden
	 * Filter zurueckgeben.
	 * 
	 * @param eigenschaft,
	 *            auspraegung, n
	 * @return report
	 */
	public AlleKontakteNachEigenschaftenReport createAuspraegungReport(String eigenschaft, String auspraegung, Nutzer n)
			throws IllegalArgumentException {

		if (this.getEditorService() == null) {
			return null;
		}

		// Die Erstellung einer Instanz dieses Reports
		AlleKontakteNachEigenschaftenReport report = new AlleKontakteNachEigenschaftenReport();

		// Festlegung des Titels und des Generierungsdatums dieses Reports
		report.setTitle("Alle Kontakte nach" + "   " + eigenschaft + " : " + auspraegung);

		report.setCreated(new Date());

		// Kopfzeile der Reporttabelle; mit den Ueberschriften der einzelnen Spalten
		Row head = new Row();
		head.addColumn(new Column("Vorname"));
		head.addColumn(new Column("Nachname"));
		head.addColumn(new Column("Erstellungsdatum"));
		head.addColumn(new Column("Modifikationsdatum"));

		// Spalte zur Darstellung des Eigentuemer eines Kontaktes
		head.addColumn(new Column("Kontakteigentuemer"));

		// Kopfzeile dem Report hinzufuegen
		report.addRow(head);

		// Angeforderte Kontaktdaten in den Vektor laden und dem Report hinzufuegen
		Vector<Kontakt> kontakt = new Vector<Kontakt>();

		// Pruefung des uebergebenen Parameters, dieses durch die Suche den Report
		// ausgibt
		if (eigenschaft != null && auspraegung != null) {

			kontakt.addAll(this.getEditorService().getKontaktByAusEig(eigenschaft, auspraegung, n));
		}

		// Die Kontakte des gespeicherten Vectors, pro Zeile der Reporttabelle
		// hinzufuegen
		for (int i = 0; i < kontakt.size(); i++) {
			Vector<Relatable> auspraegungen = new Vector<Relatable>();

			if (kontakt.elementAt(i).getOwnerId() == n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllAuspraegungenByKontaktRelatable(kontakt.elementAt(i).getId()));

			} else if (kontakt.elementAt(i).getOwnerId() != n.getId()) {
				auspraegungen.addAll(
						getEditorService().getAllSharedAuspraegungenByKontaktAndNutzer(kontakt.elementAt(i), n));
			}

			Row kon = new Row();

			kon.addColumn(new Column(kontakt.elementAt(i).getVorname()));
			kon.addColumn(new Column(kontakt.elementAt(i).getNachname()));
			kon.addColumn(new Column(kontakt.elementAt(i).getErstellDat().toString()));
			kon.addColumn(new Column(kontakt.elementAt(i).getModDat().toString()));
			kon.addColumn(
					new Column(getEditorService().getNutzerById(kontakt.elementAt(i).getOwnerId()).getEmailAddress()));
			kon.addColumn(new Column(""));

			// Hinzufuegen der Zeile zum Report
			report.addRow(kon);

			Row zwischen = new Row();

			zwischen.addColumn(new Column("Eigenschaft:"));
			zwischen.addColumn(new Column("Auspraegung:"));

			report.addRow(zwischen);

			// Aufruf der Kontakteigenschaften des Reports, sowie hinzufuegen der
			// zusammengehoerigen Eigenschaften und Auspraegungen pro Zeile
			for (int j = 0; j < auspraegungen.size(); j++) {

				Row e = new Row();

				e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
				e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));
				e.addColumn(new Column(""));
				e.addColumn(new Column(""));
				e.addColumn(new Column(""));

				report.addRow(e);
			}
			// Einzelne Zeile dem Report hinzufuegen
		}

		// Rueckgabe der Reportausgabe
		return report;
	}

	/**
	 * Report der alle Kontakte nach Eigenschaften mit ihrer Auspraegung ausgibt.
	 * Die uebergebenen Eigenschaften oder Auspraegungen, werden vom Nutzer mithilfe
	 * der Suchleiste uebergeben. Die daraufhin den Report mit dem entsprechenden
	 * Filter zurueckgeben.
	 * 
	 * @param email,
	 *            n Nutzer von dem der Report genriert werden soll anhand seiner
	 *            Email-Adresse
	 * @return report
	 */
	public AlleKontakteNachTeilhabernReport createNachTeilhabernReport(String email, Nutzer n)
			throws IllegalArgumentException {

		if (this.getEditorService() == null) {
			return null;
		}

		// Die Erstellung einer Instanz dieses Reports
		AlleKontakteNachTeilhabernReport report = new AlleKontakteNachTeilhabernReport();

		// Festlegung des Titels und des Generierungsdatums dieses Reports
		report.setTitle("Alle Kontakte nach bestimmten Teilhaberschaften");
		report.setCreated(new Date());

		// Kopfzeile der Reporttabelle; mit den Ueberschriften der einzelnen Spalten
		Row head = new Row();
		head.addColumn(new Column("Vorname"));
		head.addColumn(new Column("Nachname"));
		head.addColumn(new Column("Erstellungsdatum"));
		head.addColumn(new Column("Modifikationsdatum"));
		head.addColumn(new Column("Kontaktteilhaber"));

		// Spalte zur Darstellung des Eigentuemer eines Kontaktes
		report.addRow(head);

		// Angeforderte Kontaktdaten in den entsprechenden Vektor laden und dem Report
		// hinzufuegen
		Vector<Kontakt> kontakt = new Vector<Kontakt>();
		Vector<Kontakt> receiv = new Vector<Kontakt>();
		Vector<Berechtigung> b = new Vector<Berechtigung>();

		// Der zu ubergebene Teilhaber in Form eines Nutzer wird instantitiert
		Nutzer receiver = new Nutzer();

		// Dem Nutzer wird die entsprechende Emailadresse zugewiesen
		receiver = this.editorService.getUserByGMail(email);

		// Alle geteilten Kontakte des eingeloggten Nutzers aufrufen
		kontakt.addAll(this.getEditorService().getAllSharedKontakteByOwner(n.getId()));
		b.addAll(this.getEditorService().getAllBerechtigungenByOwner(n.getId()));

		// Die geteilten Kontakte des Vectors werden pro Kontakt und Berechtigung
		// jeweils durchlaufen
		for (int i = 0; i < kontakt.size(); i++) {

			for (int j = 0; j < b.size(); j++) {

				// Pruefung und Vergleich des Kontakt- Objekts mit dem zugehörigen
				// Berechtigung-Objekt
				// die sich auf den Receiver beziehen, der als Nutzer eingetragen ist
				if (kontakt.elementAt(i).getId() == b.elementAt(j).getObjectId()
						&& n.getId() == b.elementAt(j).getOwnerId()
						&& receiver.getId() == b.elementAt(j).getReceiverId()) {

					Kontakt k = new Kontakt();
					k = this.editorService.getKontaktById(b.elementAt(j).getObjectId());

					if (receiv.contains(k)) {

					} else {
						receiv.add(k);
					}

				}
			}
		}

		// Die Berechtigungen des gespeicherten Vectors, werden pro Zeile der
		// Reporttabelle
		// hinzugefuegt
		for (int i = 0; i < receiv.size(); i++) {

			Vector<Relatable> auspraegungen = new Vector<Relatable>();

			if (receiv.elementAt(i).getOwnerId() == n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllAuspraegungenByKontaktRelatable(receiv.elementAt(i).getId()));

			} else if (receiv.elementAt(i).getOwnerId() != n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllSharedAuspraegungenByKontaktAndNutzer(receiv.elementAt(i), n));
			}

			Row kon = new Row();
			kon.addColumn(new Column(receiv.elementAt(i).getVorname()));
			kon.addColumn(new Column(receiv.elementAt(i).getNachname()));
			kon.addColumn(new Column(receiv.elementAt(i).getErstellDat().toString()));
			kon.addColumn(new Column(receiv.elementAt(i).getModDat().toString()));
			kon.addColumn(new Column(receiver.getEmailAddress()));
			kon.addColumn(new Column(""));

			// Hinzufuegen der Zeile zum Report
			report.addRow(kon);

			Row zwischen = new Row();

			if (auspraegungen.size() != 0) {

				zwischen.addColumn(new Column("Eigenschaft:"));
				zwischen.addColumn(new Column("Auspraegung:"));
				report.addRow(zwischen);
			}
			// kontakt.elementAt(i).getStatus()

			// Aufruf der Kontakteigenschaften des Reports, sowie hinzufuegen der
			// zusammengehoerigen Eigenschaften und Auspraegungen pro Zeile
			for (int j = 0; j < auspraegungen.size(); j++) {

				// Pruefung ob alle Auspraegungen dieses Kontakts geteilt wurden
				if (getEditorService().getStatusForObject(receiv.elementAt(i).getId(),
						receiv.elementAt(i).getType()) == true
						&& getEditorService().getStatusForObject(auspraegungen.elementAt(j).getId(),
								auspraegungen.elementAt(j).getType()) == true) {

					Row e = new Row();

					e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
					e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));
					e.addColumn(new Column(""));
					e.addColumn(new Column(""));
					e.addColumn(new Column(""));

					// Hinzufuegen der Zeile zum Report
					report.addRow(e);

				}

			}
		}
		// Rueckgabe der Reportausgabe
		return report;
	}

	/**
	 * Report der alle Kontakte nach Eigenschaften mit ihrer Auspraegung ausgibt.
	 * Die uebergebenen Eigenschaften oder Auspraegungen, werden vom Nutzer mithilfe
	 * der Suchleiste uebergeben. Die daraufhin den Report mit dem entsprechenden
	 * Filter zurueckgeben.
	 * 
	 * @param n
	 *            Nutzer von dem der Report erzeugt werden soll
	 * @return report
	 */
	public AlleGeteiltenKontakteReport createAlleGeteiltenReport(Nutzer n) throws IllegalArgumentException {

		if (this.getEditorService() == null) {
			return null;
		}

		// Die Erstellung einer Instanz dieses Reports
		AlleGeteiltenKontakteReport report = new AlleGeteiltenKontakteReport();

		// Festlegung des Titels und des Generierungsdatums dieses Reports
		report.setTitle("Alle Geteilten Kontakte von" + "  " + this.getNutzerByGMail(n) + "  ");
		report.setCreated(new Date());

		// Kopfzeile der Reporttabelle; mit den Ueberschriften der einzelnen Spalten
		Row head = new Row();
		head.addColumn(new Column("Vorname"));
		head.addColumn(new Column("Nachname"));
		head.addColumn(new Column("Erstellungsdatum"));
		head.addColumn(new Column("Modifikationsdatum"));
		head.addColumn(new Column("Kontaktteilhaber"));

		// Spalte zur Darstellung des Eigentuemer eines Kontaktes
		report.addRow(head);

		// Angeforderte Kontaktdaten in den entsprechenden Vektor laden und dem Report
		// hinzufuegen
		Vector<Kontakt> kontakt = new Vector<Kontakt>();
		Vector<Kontakt> receiv = new Vector<Kontakt>();
		Vector<Berechtigung> b = new Vector<Berechtigung>();

		// Alle geteilten Kontakte des eingeloggten Nutzers aufrufen
		kontakt.addAll(this.getEditorService().getAllSharedKontakteByOwner(n.getId()));
		b.addAll(this.getEditorService().getAllBerechtigungenByOwner(n.getId()));

		// Die geteilten Kontakte des Vectors werden pro Kontakt und Berechtigung
		// jeweils durchlaufen
		for (int i = 0; i < kontakt.size(); i++) {

			for (int j = 0; j < b.size(); j++) {

				if (kontakt.elementAt(i).getId() == b.elementAt(j).getObjectId()
						&& n.getId() == b.elementAt(j).getOwnerId()
						&& kontakt.elementAt(i).getType() == b.elementAt(j).getType())

				{
					Kontakt k = new Kontakt();
					k = this.editorService.getKontaktById(b.elementAt(j).getObjectId());
					receiv.add(k);

				}
			}
		}

		/*
		 * Receiver Vector durchgehen und Objekte mit Typ k herauslesen und dem Vector
		 * allNutzer hinzufuegen.
		 */
		Vector<Nutzer> allNutzer = new Vector<Nutzer>();
		for (int i = 0; i < receiv.size(); i++) {
			if (receiv.elementAt(i).getType() == 'k') {
				allNutzer.addAll(getEditorService().sharedWith(receiv.elementAt(i).getId(), 'k', n));
			}
		}

		// Die Berechtigungen des gespeicherten Vectors, werden pro Zeile der
		// Reporttabelle
		// hinzugefuegt
		for (int i = 0; i < receiv.size(); i++) {

			Vector<Relatable> auspraegungen = new Vector<Relatable>();

			if (receiv.elementAt(i).getOwnerId() == n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllAuspraegungenByKontaktRelatable(receiv.elementAt(i).getId()));

			} else if (receiv.elementAt(i).getOwnerId() != n.getId()) {
				auspraegungen
						.addAll(getEditorService().getAllSharedAuspraegungenByKontaktAndNutzer(receiv.elementAt(i), n));
			}

			Row kon = new Row();
			kon.addColumn(new Column(receiv.elementAt(i).getVorname()));
			kon.addColumn(new Column(receiv.elementAt(i).getNachname()));
			kon.addColumn(new Column(receiv.elementAt(i).getErstellDat().toString()));
			kon.addColumn(new Column(receiv.elementAt(i).getModDat().toString()));
			kon.addColumn(new Column(allNutzer.elementAt(i).getEmailAddress()));
			kon.addColumn(new Column(""));

			// Hinzufuegen der Zeile zum Report
			report.addRow(kon);

			Row zwischen = new Row();

			if (auspraegungen.size() != 0) {

				zwischen.addColumn(new Column("Eigenschaft:"));
				zwischen.addColumn(new Column("Auspraegung:"));
				report.addRow(zwischen);
			}

			// Vector<Relatable> ausgabe =
			// getEditorService().getAllSharedAuspraegungenByKontaktAndNutzer(receiv.elementAt(i),
			// n);

			// Aufruf der Kontakteigenschaften des Reports, sowie hinzufuegen der
			// zusammengehoerigen Eigenschaften und Auspraegungen pro Zeile
			for (int j = 0; j < auspraegungen.size(); j++) {

				// for (int l = 0; l < ausgabe.size(); l++) {

				// Pruefung ob alle Auspraegungen dieses Kontakts geteilt wurden
				if (getEditorService().getStatusForObject(receiv.elementAt(i).getId(),
						receiv.elementAt(i).getType()) == true
						&& getEditorService().getStatusForObject(auspraegungen.elementAt(j).getId(),
								auspraegungen.elementAt(j).getType()) == true) {

					Row e = new Row();

					e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
					e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));
					e.addColumn(new Column(""));
					e.addColumn(new Column(""));
					e.addColumn(new Column(""));

					// Hinzufuegen der Zeile zum Report
					report.addRow(e);

				}
			}

		}
		// Einzelne Zeile dem Report hinzufuegen

		// Rueckgabe der Reportausgabe
		return report;
	}

	/*
	 * *************************************************************************
	 * ABSCHNITT - Anfang: Hilfsmethoden
	 * *************************************************************************
	 */

	/**
	 * Den Nutzer anhand der Registrierungsmail auslesen und die identifzierende
	 * Mail als Zeichkette anzeigen lassen.
	 * 
	 * @param n
	 * @return
	 * @throws IllegalArgumentException
	 */
	private String getNutzerByGMail(Nutzer n) throws IllegalArgumentException {

		String name = "";
		Nutzer nutzer = editorService.getUserByGMail(n.getEmailAddress());
		name = nutzer.getEmailAddress();

		return name;
	}

	/**
	 * Zur korrekten Ausgabe der Kopfdaten, wird diese Hilfsmethode einheitlich fuer
	 * alle Berichtsausgaben verwendet.
	 * 
	 * @param n
	 * @param report
	 * @return
	 */
	private CompositeParagraph createHeaderData(Nutzer n) {

		// Generierung der Kopfdaten des Reports
		CompositeParagraph headerData = new CompositeParagraph();
		try {
			headerData.addSubParagraph(new SimpleParagraph("Nutzer: " + n.getEmailAddress()));

		} catch (NullPointerException e) {
			headerData.addSubParagraph(new SimpleParagraph("Nutzer: " + "Unbekannter Nutzer"));
			e.printStackTrace();
		}
		headerData
				.addSubParagraph(new SimpleParagraph("Erstellungsdatum: " + new Timestamp(System.currentTimeMillis())));

		return headerData;
	}

	// e.getNumColumns(); --> 0 bis 6 == (int 7);

	// e.addColumn(e.getColumns().elementAt(5)) new
	// Column(auspraegungen.elementAt(j).getBezeichnung());
	// e.addColumn(e.getColumns().elementAt(6));new
	// Column(auspraegungen.elementAt(j).getWert());

	// e.getColumnAt(5 >= 0);
	// e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
	// e.getColumnAt(6);
	// e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));

	// e.addColumn(e.getColumnAt(5)(new
	// Column(auspraegungen.elementAt(j).getBezeichnung()))));

	// if(e.getColumnAt(5) != null) {
	// e.addColumn(new Column(auspraegungen.elementAt(j).getBezeichnung()));
	// } else if (e.getColumnAt(6) != null) {
	// e.addColumn(new Column(auspraegungen.elementAt(j).getWert()));
	// }
	// else if (eigenschaft != null && auspraegung == null) {
	// kontakt.addAll(this.getEditorService().getKontakteByEigenschaft(eigenschaft,
	// n));
	// } else if (auspraegung != null && eigenschaft == null) {
	// kontakt.addAll(this.getEditorService().getKontakteByAuspraegung(auspraegung,
	// n));
	// }

	// for (int i = 0; i < kontakt.size(); i++) {
	// Vector<Relatable> ausgabe = getEditorService()
	// .getAllAuspraegungenByKontaktRelatable(kontakt.elementAt(i).getId());
	//
	// for (int j = 0; j < ausgabe.size(); j++) {
	// if (auspraegung != ausgabe.elementAt(j).getWert()) {
	// kontakt.removeElementAt(i);
	// } else if (eigenschaft != ausgabe.elementAt(j).getBezeichnung()) {
	// kontakt.removeElementAt(i);
	// }
	// }
	// }

	// else if (auspraegung != null) {
	// kontakt.addAll(this.getEditorService().getKontakteByAuspraegung(auspraegung,
	// n));
	//
	// }
	// Die Kontakte des gespeicherten Vectors, pro Zeile der Reporttabelle
	// hinzufuegen

	// Vector<Relatable> relatable = new Vector<Relatable>();
}
