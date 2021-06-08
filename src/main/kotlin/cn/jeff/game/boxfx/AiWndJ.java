package cn.jeff.game.boxfx;

import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class AiWndJ {

	public AiWnd k;
	public Label label1;
	public Label label2;
	public Label label3;
	public Button btnReset;
	public Button btnNext;
	public Label stepLabel;

	public void abort() {
		k.abort();
	}

	public void testIt() {
		k.testIt();
	}

	public void demoReset() {
		k.demoReset();
	}

	public void demoNext() {
		k.demoNext();
	}

}
