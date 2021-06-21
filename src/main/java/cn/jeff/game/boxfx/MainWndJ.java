package cn.jeff.game.boxfx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class MainWndJ {

	public MainWnd k;
	public Label statusLabel;
	public Button btnRegret;

	public void prevRoom() {
		k.prevRoom();
	}

	public void selectRoom() {
		k.selectRoom();
	}

	public void nextRoom() {
		k.nextRoom();
	}

	public void playAgain() {
		k.playAgain();
	}

	public void autoResolve() {
		k.autoResolve();
	}

	public void regret() {
		k.regret();
	}

}
