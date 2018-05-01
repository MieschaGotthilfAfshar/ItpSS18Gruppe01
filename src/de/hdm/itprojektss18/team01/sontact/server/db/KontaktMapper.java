package de.hdm.itprojektss18.team01.sontact.server.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import de.hdm.itprojektss18.team01.sontact.shared.bo.Kontakt;


	/**
	 * Mapper-Klasse, die <code>Kontakt</code>-Objekte auf eine relationale
	 * Datenbank abbildet. Hierzu wird eine Reihe von Methoden zur Verf�gung
	 * gestellt, mit deren Hilfe z.B. Objekte gesucht, erzeugt, modifiziert und
	 * gel�scht werden k�nnen. Das Mapping ist bidirektional. D.h., Objekte k�nnen
	 * in DB-Strukturen und DB-Strukturen in Objekte umgewandelt werden.
	 * 
	 * @author Thies
	 */

public class KontaktMapper {
	
	
	/**
	 * Die Klasse KontaktMapper wird nur einmal instantiiert. Man spricht hierbei
	 * von einem sogenannten <b>Singleton</b>.
	 * <p>
	 * Diese Variable ist durch den Bezeichner <code>static</code> nur einmal f�r
	 * s�mtliche eventuellen Instanzen dieser Klasse vorhanden. Sie speichert die
	 * einzige Instanz dieser Klasse.
	 * 
	 * @see kontaktMapper()
	 */
	
	private static KontaktMapper kontaktMapper = null;
	
	
	/**
	 * Gesch�tzter Konstruktor - verhindert die M�glichkeit, mit "new" 
	 * neue Instanzen dieser Klasse zu erzeugen
	 */
	
	protected KontaktMapper(){
		
	}
	
	
	/**
	 * Diese statische Methode kann aufgrufen werden durch
	 * <code>KontaktMapper.kontaktMapper()</code>. Sie stellt die
	 * Singleton-Eigenschaft sicher, indem Sie daf�r sorgt, dass nur eine einzige
	 * Instanz von <code>KontaktMapper</code> existiert.
	 * <p>
	 * 
	 * <b>Fazit:</b> KontaktMapper sollte nicht mittels <code>new</code>
	 * instantiiert werden, sondern stets durch Aufruf dieser statischen Methode.
	 * 
	 * @return DAS <code>KontaktMapper</code>-Objekt.
	 * @see kontaktMapper
	 */
	
	public static KontaktMapper kontaktMapper() {
		if(kontaktMapper == null) {
			kontaktMapper = new KontaktMapper();
		}
		
		return kontaktMapper;
	}
	
	
	/**
	 * Suchen eines Kontaktes mit vorgegebener KontaktID. Da diese eindeutig ist, 
	 * wird genau ein Objekt zur�ckgegeben.
	 * 
	 * @param id Prim�rschl�sselattribut (->DB)
	 * @return Konto-Objekt, das dem �bergebenen Schl�ssel entspricht, null bei nicht vorhandenem DB-Tupel.
	 */
	
	public Kontakt findKontaktById (int id) {
		/**
		 * DB Verbindung holen
		 */
		
		Connection con = DBConnection.connection();
		
		try{
			/**
			 * Leeres SQL-Statement (JDBC) anlegen
			 */
			Statement stmt = con.createStatement();
			
			/**
			 * Statement ausf�llen und als Query an die DB schicken
			 */
			ResultSet rs = stmt.executeQuery("SELECT id FROM Kontakt" + "WHERE id = " + id);
			
			/**
			 * Da id Prim�rschl�ssel ist, kann max. nur ein Tupel zur�ckgegeben werden.
			 * Pr�fe, ob ein Ergebnis vorliegt.
			 */
			if (rs.next()){
				/**
				 * Ergebnis-Tupel in Objekt umwandeln
				 */
				Kontakt k = new Kontakt();
				k.setId(rs.getInt("id"));
				return k;
			}
		}
		
		catch (SQLException e2){
			e2.printStackTrace();
			return null;
		}
		
		return null;
	}
	
	
	/**
	 * Auslesen aller Kontakte eines durch ein weiteres Attribut (Name) gegebenen Kontakts.
	 * 
	 * @see findKontaktByName
	 * @param String name f�r zugeh�rige Kontakte
	 * @return ein Vektor mit Kontakt-Objekten, die durch den gegebenen Namen repr�sentiert werden. 
	 * Bei evtl. Exceptions wird ein partiell gef�llter oder ggf. auch leerer Vektor zur�ckgeliefert.
	 * 
	 */
	
	public Vector<Kontakt> findKontaktByName (String nachname, String vorname) {
		Connection con = DBConnection.connection();
		Vector<Kontakt> result = new Vector<Kontakt>();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT nachname, vorname FROM Kontakt" + "WHERE nachname=" + nachname + "vorname=" + vorname + "ORDER BY nachname");
			
			/**
			 * F�r jeden Eintrag im Suchergebnis wird nun ein Kontakt-Objekt erstellt.
			 */
			while (rs.next()){
				Kontakt k = new Kontakt();
				k.setNachname(rs.getString("nachname"));
				k.setVorname(rs.getString("vorname"));
				
				/**
				 * Hinzuf�gen des neuen Objekts zum Ergebnisvektor
				 */
				result.addElement(k);
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
		
		/**
		 * Ergebnisvektor zur�ckgeben
		 */
		return result;
	}
	
	
	/**
	 * Auslesen aller Kontakte eines durch Fremdschl�ssel (kontaktlistenId) gegebenen Kontakts.
	 * 
	 * @see findKontaktByKontaktliste
	 * @param int kontaktlistenId f�r zugeh�rige Kontakte
	 * @return ein Vektor mit Kontakt-Objekten, die durch die gegebene Kontaktliste repr�sentiert werden. 
	 * Bei evtl. Exceptions wird ein partiell gef�llter oder ggf. auch leerer Vektor zur�ckgeliefert.
	 * 
	 */
	
	public Vector<Kontakt> findKontaktByKontaktliste (int kontaktlisteId) {
		Connection con = DBConnection.connection();
		Vector<Kontakt> result = new Vector<Kontakt>();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT kontaktlisteId FROM Kontakt" + "WHERE kontaktlisteId=" + kontaktlisteId);
			
			/**
			 * F�r jeden Eintrag im Suchergebnis wird nun ein Kontakt-Objekt erstellt.
			 */
			while (rs.next()){
				Kontakt k = new Kontakt();
				k.setKontaktlisteId(rs.getInt("kontaktlisteId"));
				
				/**
				 * Hinzuf�gen des neuen Objekts zum Ergebnisvektor
				 */
				result.addElement(k);
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
		
		/**
		 * Ergebnisvektor zur�ckgeben
		 */
		return result;
	}
	
	
	/**
	 * Auslesen aller Kontakte eines durch Fremdschl�ssel (ownerId) gegebenen Kontakts.
	 * 
	 * @see findKontaktByNutzerId
	 * @param int ownerId f�r zugeh�rige Kontakte
	 * @return ein Vektor mit Kontakt-Objekten, die durch den gegebenen Nutzer repr�sentiert werden. 
	 * Bei evtl. Exceptions wird ein partiell gef�llter oder ggf. auch leerer Vektor zur�ckgeliefert.
	 * 
	 */
	
	public Vector<Kontakt> findKontaktByNutzerId (int ownerId) {
		Connection con = DBConnection.connection();
		Vector<Kontakt> result = new Vector<Kontakt>();
		
		try{
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT ownerId FROM Kontakt" + "WHERE ownerId=" + ownerId);
			
			/**
			 * F�r jeden Eintrag im Suchergebnis wird nun ein Kontakt-Objekt erstellt.
			 */
			while (rs.next()){
				Kontakt k = new Kontakt();
				k.setOwnerId(rs.getInt("ownerId"));
				
				/**
				 * Hinzuf�gen des neuen Objekts zum Ergebnisvektor
				 */
				result.addElement(k);
			}
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
		
		/**
		 * Ergebnisvektor zur�ckgeben
		 */
		return result;
	}

	
	/**
	 * Einf�gen eins <code>Kontakt</code> in die Datenbank. Dabei wird auch der Prim�rschl�ssel des �bergebenen Objekt gepr�ft und ggf. berichtigt.
	 * @param k das zu speichernde Objekt
	 * @return das bereits �bergebene Objekt, jedoch mit ggf. korrigierter <code>id</code>.
	 */
	
	public Kontakt insertKontakt (Kontakt k) {
		Connection con = DBConnection.connection();
		
		try {
			Statement stmt = con.createStatement();
			
			/**
			 * Zun�chst wird gepr�ft, welches der momentan h�chste Prim�rschl�sselwert ist.
			 */
			ResultSet rs = stmt.executeQuery("SELECT MAX(id) AS maxid" + "FROM Kontakt");
			
			/**
			 * Wenn etwas zur�ckgegeben wird, kann dies nur einzeilig sein
			 */
			if (rs.next()){
				/**
				 * k erh�lt den bisher maximalen, nun um 1 inkrementierten Prim�rschl�ssel.
				 */
				k.setId(rs.getInt("maxid")+1);
				
				stmt = con.createStatement();
				
				/**
				 * Jetzt erst erfolgt die tats�chliche Einf�geoperation
				 */
				stmt.executeUpdate("INSERT INTO Kontakt (id, ownerId) " + "VALUES (" + k.getId() + "," + k.getOwnerId() + ")");
			}
		}
		catch (SQLException e2) {
			e2.printStackTrace();
		}
		
		/**
		 * R�ckgabe des evtl. korrigierten Accounts.
		 * 
		 * HINWEIS: Da in Java nur Referenzen auf Objekte und keine physischen Objekte �bergeben werden, w�re die Anpassung
		 * des Kontakt-Objekts auch ohne diese explizite R�ckgabe au�erhalb dieser Methode sichtbar. Die explizite
		 * R�ckgabe von k ist eher ein Stilmittel, um zu signalisieren, dass sich das Objekt evtl. im Laufe der Methode 
		 * ver�ndert hat.
		 */
		return k;
	}
	
	
	/**
	 * Wiederholtes Schreiben eines Objekts in die Datenbank.
	 * 
	 * @param k das Objekt, das in die DB geschrieben werden soll
	 * @return das als Parameter �bergebene Objekt
	 */
	
	public Kontakt update (Kontakt k) {
		Connection con = DBConnection.connection();
		
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("UPDATE Kontakt" + "SET ownerId=\"" + k.getOwnerId() + "\" " + "WHERE id =" + k.getId());
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
		/**
		 * Um Analogie zu insertKontakt(Kontakt k) zu wahren, geben wir k zur�ck
		 */
		return k;
	}
	
	
	/**
	 * L�schen der Daten eines <code>Kontakt</code>-Objekts aus der Datenbank.
	 * @param k das aus der DB zu l�schende "Objekt"
	 */
	
	public void deleteKontakt(Kontakt k){
		Connection con = DBConnection.connection();
		
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM Kontakt " + "WHERE id=" + k.getId());
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
	}

	
	/**
	 * Einf�gen eines <code>Kontakt</code>-Objekts in eine <code>Kontaktliste</code>. 
	 * @param k das einzuf�gende Objekt, kl die betreffende Kontaktliste
	 */
	
	public void insertKontaktToKontaktliste(Kontakt k, int kontaktlisteId){
		Connection con = DBConnection.connection();
		
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO Kontakt(kontaktlisteId) WHERE id=" + k.getId() + "VALUES (" + kontaktlisteId + ")");
			}
		catch (SQLException e2){
			e2.printStackTrace();
		}
	}
	
	
	/**
	 * Entfernen eines <code>Kontakt</code>-Objekts aus einer <code>Kontaktliste</code>. 
	 * @param k das zu entfernende Objekt, kl die betreffende Kontaktliste
	 */
	
	public void deleteKontaktFromKontaktliste(Kontakt k, int kontaktlisteId){
		Connection con = DBConnection.connection();
		
		try {
			Statement stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM Kontakt(kontaktlisteId) WHERE id=" + k.getId() + "VALUES (" + kontaktlisteId + ")");
		}
		catch (SQLException e2){
			e2.printStackTrace();
		}
	}
	
	
}

