/*
 * Copyright (c) 2019-2021 Hugo Dupanloup (Yeregorix)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.smoofyuniverse.simplex.ui.tab;

import javafx.beans.binding.Bindings;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.smoofyuniverse.common.util.GridUtil;
import net.smoofyuniverse.simplex.generator.PythagorasTree;
import net.smoofyuniverse.simplex.util.PathHelper;
import org.spongepowered.math.TrigMath;

import static net.smoofyuniverse.simplex.generator.PythagorasTree.*;

public class PythagorasTreePane extends GridPane {
	private final Canvas canvas = new Canvas(700, 700);

	private final Slider widthFactor = new Slider(MIN_FACTOR, MAX_FACTOR, Math.sqrt(2) / 2), lengthFactor = new Slider(MIN_FACTOR, MAX_FACTOR, Math.sqrt(2) / 2);
	private final Slider leftAngle = new Slider(MIN_ANGLE, MAX_ANGLE, 50 * TrigMath.DEG_TO_RAD), rightAngle = new Slider(MIN_ANGLE, MAX_ANGLE, 40 * TrigMath.DEG_TO_RAD);
	private final Slider initialWidth = new Slider(1, 200, 50), initialLength = new Slider(1, 300, 140);
	private final Slider level = new Slider(0, 18, 15);

	public PythagorasTreePane() {
		Label widthFactorL = new Label(), lengthFactorL = new Label(), leftAngleL = new Label(), rightAngleL = new Label(),
				initialWidthL = new Label(), initialLengthL = new Label(), levelL = new Label();

		widthFactorL.textProperty().bind(Bindings.format("Facteur de largeur: %.3f", this.widthFactor.valueProperty()));
		lengthFactorL.textProperty().bind(Bindings.format("Facteur de longueur: %.3f", this.lengthFactor.valueProperty()));
		leftAngleL.textProperty().bind(Bindings.format("Angle de gauche: %.3f", this.leftAngle.valueProperty()));
		rightAngleL.textProperty().bind(Bindings.format("Angle de droite: %.3f", this.rightAngle.valueProperty()));
		initialWidthL.textProperty().bind(Bindings.format("Largeur initiale: %.3f", this.initialWidth.valueProperty()));
		initialLengthL.textProperty().bind(Bindings.format("Longueur initiale: %.3f", this.initialLength.valueProperty()));
		levelL.textProperty().bind(Bindings.format("Niveau: %.0f", this.level.valueProperty()));

		this.widthFactor.valueProperty().addListener(this::update);
		this.lengthFactor.valueProperty().addListener(this::update);
		this.leftAngle.valueProperty().addListener(this::update);
		this.rightAngle.valueProperty().addListener(this::update);
		this.initialWidth.valueProperty().addListener(this::update);
		this.initialLength.valueProperty().addListener(this::update);
		this.level.valueProperty().addListener(this::update);

		add(new StackPane(this.canvas), 0, 0, 4, 1);

		addRow(1, widthFactorL, this.widthFactor, lengthFactorL, this.lengthFactor);
		addRow(2, leftAngleL, this.leftAngle, rightAngleL, this.rightAngle);
		addRow(3, initialWidthL, this.initialWidth, initialLengthL, this.initialLength);
		addRow(4, levelL, this.level);

		getColumnConstraints().addAll(GridUtil.createColumn(15), GridUtil.createColumn(35), GridUtil.createColumn(15), GridUtil.createColumn(35));
		getRowConstraints().addAll(GridUtil.createRow(Priority.ALWAYS), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow());

		setVgap(5);
		setHgap(5);
		setPadding(new Insets(10));

		generate();
	}

	private <T> void update(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		generate();
	}

	public void generate() {
		GraphicsContext g = this.canvas.getGraphicsContext2D();
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, 700, 700);

		PathHelper h = new PathHelper(g);
		h.currentX = 350;
		h.currentY = 700;
		h.rotate(TrigMath.THREE_PI_HALVES);

		new PythagorasTree(this.widthFactor.getValue(), this.lengthFactor.getValue(), this.leftAngle.getValue(), this.rightAngle.getValue())
				.generate(h, (int) this.level.getValue(), this.initialWidth.getValue(), this.initialLength.getValue());
	}
}
