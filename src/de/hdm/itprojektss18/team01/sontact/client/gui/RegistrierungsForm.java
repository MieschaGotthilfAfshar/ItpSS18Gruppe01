package de.hdm.itprojektss18.team01.sontact.client.gui;

import java.util.Date;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DateBox;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import de.hdm.itprojektss18.team01.sontact.client.ClientsideSettings;
import de.hdm.itprojektss18.team01.sontact.shared.EditorServiceAsync;
import de.hdm.itprojektss18.team01.sontact.shared.bo.Auspraegung;
import de.hdm.itprojektss18.team01.sontact.shared.bo.Eigenschaft;
import de.hdm.itprojektss18.team01.sontact.shared.bo.Kontakt;
import de.hdm.itprojektss18.team01.sontact.shared.bo.Nutzer;

/**
 * RegistrierungsFormular Klasse die beim Login aufgerufen wird, wenn der Nutzer
 * noch keinen Kontakt in der Datenbank gespeichert hat.
 * 
 * 
 * 
 * 
 * @author Kevin Batista, Dennis Lehle, Ugur Bayrak
 */
public class RegistrierungsForm extends VerticalPanel {

	private EditorServiceAsync ev = ClientsideSettings.getEditorVerwaltung();

	// TextBoxen
	TextBox vornameTxtBox = new TextBox();
	TextBox nachnameTxtBox = new TextBox();
	TextBox gmailTb = new TextBox();
	DateBox geburtsdatum = new DateBox();
	TextBox plz = new TextBox();
	TextBox wohnort = new TextBox();
	TextBox emailadresse = new TextBox();
	TextBox anschrift = new TextBox();

	// Labels
	Label willkommen = new Label("Dies ist Ihre erste Anmeldung, Bitte füllen Sie die unten stehenden Felder alle aus.");
	Label gmail = new Label("Google-Mail");
	Label vorname = new Label("Vorname");
	Label nachname = new Label("Nachname");
	Label geburtsdatumlb = new Label("Geburtsdatum");
	Label plzlb = new Label("Postleitzahl");
	Label wohnortlb = new Label("Ort");
	Label emailadresselb = new Label("Email");
	Label anschriftlb = new Label("Anschrift");

	Label auswahleigenschaften = new Label("Auswahleigenschaft");
	Label freitexteigenschaften = new Label("Freitexteigenschaft");

	Button speichern = new Button(
			"<image src='/images/user.png' width='20px' height='20px' align='center' /> Jetzt registrieren");

	HorizontalPanel hp = new HorizontalPanel();
	HorizontalPanel hp2 = new HorizontalPanel();

	// Hauptpanel fuer die Ansicht der Kontakterstellung
	VerticalPanel hauptPanel = new VerticalPanel();
	// Hauptpanel fuer die Ansicht der Kontakteigenschaftsangaben
	VerticalPanel hauptPanel2 = new VerticalPanel();

	FlexTable kontaktTable = new FlexTable();
	FlexTable infoTable = new FlexTable();

	public RegistrierungsForm(Nutzer n) {

		// RootPanel leeren damit neuer Content geladen werden kann.
		RootPanel.get("content").clear();
		RootPanel.get("contentHeader").clear();
		RootPanel.get("footer").clear();
		// Ueberschrift anzeigen
		RootPanel.get("contentHeader").add(new HTML("<image src='/images/group.png' width='23px' height='18px' align='center' /> "));
		RootPanel.get("contentHeader").add(new HTML("Willkommen bei Sontact"));
		RootPanel.get("contentHeader").add(new HTML("<image src='/images/group.png' width='23px' height='18px' align='center' /> "));
		willkommen.setStylePrimaryName("labelReg");
		RootPanel.get("content").add(willkommen);
		this.add(speichern);

		onLoad(n);

	}

	protected void onLoad(Nutzer n) {

		speichern.setStylePrimaryName("regButton");

		gmailTb.setText(n.getEmailAddress());
		gmailTb.setEnabled(false);

		gmail.setStylePrimaryName("label");
		vorname.setStylePrimaryName("label");
		nachname.setStylePrimaryName("label");
		plzlb.setStylePrimaryName("label");
		wohnortlb.setStylePrimaryName("label");
		anschriftlb.setStylePrimaryName("label");

		gmailTb.setStylePrimaryName("tbRundungReg");
		vornameTxtBox.setStylePrimaryName("tbRundungReg");
		nachnameTxtBox.setStylePrimaryName("tbRundungReg");
		plz.setStylePrimaryName("tbRundungReg");
		wohnort.setStylePrimaryName("tbRundungReg");
		anschrift.setStylePrimaryName("tbRundungReg");

		kontaktTable.setWidget(1, 0, vorname);
		kontaktTable.setWidget(1, 1, vornameTxtBox);
		kontaktTable.setWidget(2, 0, nachname);
		kontaktTable.setWidget(2, 1, nachnameTxtBox);
		kontaktTable.setWidget(3, 0, gmail);
		kontaktTable.setWidget(3, 1, gmailTb);

		infoTable.setWidget(1, 0, anschriftlb);
		infoTable.setWidget(1, 1, anschrift);
		infoTable.setWidget(2, 0, plzlb);
		infoTable.setWidget(2, 1, plz);
		infoTable.setWidget(3, 0, wohnortlb);
		infoTable.setWidget(3, 1, wohnort);

		kontaktTable.setStylePrimaryName("infoTable");
		infoTable.setStylePrimaryName("infoTable");

		hauptPanel.add(kontaktTable);
		hauptPanel2.add(infoTable);
		hauptPanel2.add(speichern);

		vornameTxtBox.getElement().setPropertyString("placeholder", "Vorname des Kontakts");
		nachnameTxtBox.getElement().setPropertyString("placeholder", "Nachname des Kontakts");
		plz.getElement().setPropertyString("placeholder", "Postleitzahl des Kontakts");
		wohnort.getElement().setPropertyString("placeholder", "Wohnort des Kontakts");
		anschrift.getElement().setPropertyString("placeholder", "Anschrift des Kontakts");

		RootPanel.get("content").add(hauptPanel);
		RootPanel.get("content").add(hauptPanel2);

		// Speichern des zu registrierenden Kontakts und seinen Ausprägungen
		speichern.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (vornameTxtBox.getValue() != "" && nachnameTxtBox.getValue() != "" && plz.getText() != ""
						&& wohnort.getText() != "" && anschrift.getText() != "") {

					ev.createKontaktRegistrierung(vornameTxtBox.getValue(), nachnameTxtBox.getValue(), n,
							new AsyncCallback<Kontakt>() {

								@Override
								public void onFailure(Throwable caught) {
									caught.getMessage().toString();

								}

								@Override
								public void onSuccess(Kontakt result2) {

									ev.getEigenschaftByBezeichnung(plzlb.getText(), new AsyncCallback<Eigenschaft>() {

										@Override
										public void onFailure(Throwable caught) {
											caught.getMessage().toString();

										}

										@Override
										public void onSuccess(Eigenschaft result) {

											ev.createAuspraegung(plz.getText(), result.getId(), result2.getId(),
													n.getId(), new AsyncCallback<Auspraegung>() {

														@Override
														public void onFailure(Throwable caught) {
															caught.getMessage().toString();

														}

														@Override
														public void onSuccess(Auspraegung result) {

															ev.getEigenschaftByBezeichnung(wohnortlb.getText(),
																	new AsyncCallback<Eigenschaft>() {

																		@Override
																		public void onFailure(Throwable caught) {
																			caught.getMessage().toString();

																		}

																		@Override
																		public void onSuccess(Eigenschaft result) {

																			ev.createAuspraegung(wohnort.getText(),result.getId(), result2.getId(), n.getId(),
																					new AsyncCallback<Auspraegung>() {

																						@Override
																						public void onFailure(
																								Throwable caught) {
																							caught.getMessage()
																									.toString();

																						}

																						@Override
																						public void onSuccess(Auspraegung result) {

																							ev.getEigenschaftByBezeichnung(anschriftlb.getText(),
																									new AsyncCallback<Eigenschaft>() {

																										@Override
																										public void onFailure(
																												Throwable caught) {
																											caught.getMessage().toString();

																										}

																										@Override
																										public void onSuccess(Eigenschaft result) {

																											ev.createAuspraegung(anschrift.getText(),
																													result.getId(),
																													result2.getId(),
																													n.getId(),
																													new AsyncCallback<Auspraegung>() {

																														@Override
																														public void onFailure(Throwable caught) {
																															caught.getMessage().toString();

																														}

																														@Override
																														public void onSuccess(Auspraegung result) {

																															RootPanel.get("contentHeader").clear();
																															RootPanel.get("content").clear();
																															Window.Location.reload();

																														}

																													});

																										}

																									});

																						}
																					});

																		}

																	});

														}

													});
										}

									});

								}
							});

				} else {
					MessageBox.alertWidget("Hinweis ", "Bitte füllen Sie die markierten Felder alle aus.");
					if (plz.getText() == "") {
						plz.getElement().getStyle().setBorderColor("red");
					}
					if (wohnort.getText() == "") {
						wohnort.getElement().getStyle().setBorderColor("red");
					}
					if (anschrift.getText() == "") {
						anschrift.getElement().getStyle().setBorderColor("red");
					}
					if (vornameTxtBox.getText() == "") {
						vornameTxtBox.getElement().getStyle().setBorderColor("red");
					}
					if (nachnameTxtBox.getText() == "") {
						nachnameTxtBox.getElement().getStyle().setBorderColor("red");
					}
				}

			}
		});

		/// ---------Abschnitt-für-KeyDownHandler-derTextBoxen-----------------///

		vornameTxtBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					speichern.click();

				}
			}

		});

		nachnameTxtBox.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					speichern.click();

				}
			}

		});

		plz.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					speichern.click();

				}
			}

		});

		wohnort.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					speichern.click();

				}
			}

		});

	}
}