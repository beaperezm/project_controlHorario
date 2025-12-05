package com.ue.controller;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.event.ActionEvent;

public class EjemploViewController {
	@FXML
	private Button BotonEjemplo;

	// Event Listener on Button[#BotonEjemplo].onAction
	@FXML
	public void actionEjemplo(ActionEvent event) {
		System.out.println("botón pulsado");
	}
}
