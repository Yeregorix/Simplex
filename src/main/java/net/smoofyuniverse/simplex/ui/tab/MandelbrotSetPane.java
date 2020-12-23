/*
 * Copyright (c) 2019-2020 Hugo Dupanloup (Yeregorix)
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

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.smoofyuniverse.common.app.App;
import net.smoofyuniverse.common.app.State;
import net.smoofyuniverse.common.fx.field.DoubleField;
import net.smoofyuniverse.common.fx.field.IntegerField;
import net.smoofyuniverse.common.fx.task.ObservableProgressTask;
import net.smoofyuniverse.common.task.supplier.AutoCancellingSupplier;
import net.smoofyuniverse.common.util.GridUtil;
import net.smoofyuniverse.logger.core.Logger;
import net.smoofyuniverse.simplex.generator.MandelbrotSet;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MandelbrotSetPane extends GridPane {
	private static final Logger logger = App.getLogger("MandelbrotSetPane");
	private final ExecutorService executor = Executors.newCachedThreadPool();
	private final AutoCancellingSupplier<ObservableProgressTask> taskSupplier = new AutoCancellingSupplier<>(ObservableProgressTask::new);
	private final ImageView view = new ImageView();
	private final ProgressBar progressBar = new ProgressBar();
	private final DoubleField centerX = new DoubleField(-0.75), centerY = new DoubleField(0);
	private final DoubleField scale = new DoubleField(0, 0.1, 0.005);
	private final IntegerField iterations = new IntegerField(1, 50000, 100);
	private final IntegerField threads = new IntegerField(1, 4);
	private final Button colorModeB = new Button("Linéaire");
	private int colorMode = 0;
	private volatile boolean zooming;

	public MandelbrotSetPane() {
		State.SHUTDOWN.newListener(e -> this.executor.shutdown(), 0).register();

		this.progressBar.setMaxWidth(Double.MAX_VALUE);
		this.colorModeB.setPrefWidth(150);

		this.view.setOnScroll(e -> {
			double scale = this.scale.getValue();
			if (e.getDeltaY() > 0) {
				this.scale.setValue(scale * 0.9);
			} else {
				this.scale.setValue(Math.min(scale / 0.9, 0.1));
			}
		});

		this.view.setOnMouseClicked(e -> {
			if (this.zooming)
				return;

			double scale = this.scale.getValue();
			double x = this.centerX.getValue() + (e.getX() - 350) * scale, y = this.centerY.getValue() + (e.getY() - 350) * scale;

			this.centerX.setValue(x);
			this.centerY.setValue(y);
		});

		this.colorModeB.setOnAction(e -> {
			switch (this.colorMode) {
				case 0:
					this.colorMode = 1;
					this.colorModeB.setText("Racine");
					break;
				case 1:
					this.colorMode = 2;
					this.colorModeB.setText("Quadratique");
					break;
				case 2:
					this.colorMode = 0;
					this.colorModeB.setText("Linéaire");
					break;
			}
			generate();
		});

		this.centerX.valueProperty().addListener(this::update);
		this.centerY.valueProperty().addListener(this::update);
		this.scale.valueProperty().addListener((v, oldV, newV) -> {
			this.zooming = true;
			generate();
		});
		this.iterations.valueProperty().addListener(this::update);

		add(new StackPane(this.view), 0, 0, 4, 1);
		add(this.progressBar, 0, 1, 4, 1);

		addRow(2, new Label("Centre X:"), this.centerX, new Label("Centre Y:"), this.centerY);
		addRow(3, new Label("Echelle:"), this.scale, new Label("Itérations:"), this.iterations);
		addRow(4, new Label("Threads:"), this.threads, new Label("Coloration:"), this.colorModeB);

		getColumnConstraints().addAll(GridUtil.createColumn(15), GridUtil.createColumn(35), GridUtil.createColumn(15), GridUtil.createColumn(35));
		getRowConstraints().addAll(GridUtil.createRow(Priority.ALWAYS), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow(), GridUtil.createRow());

		setVgap(5);
		setHgap(5);
		setPadding(new Insets(10));

		generate();
	}

	public void generate() {
		double scale = this.scale.getValue();
		if (scale == 0)
			return;

		int threads = this.threads.getValue();
		MandelbrotSet set = new MandelbrotSet(threads == 1 ? null : this.executor, threads, this.centerX.getValue() - 350 * scale, this.centerY.getValue() - 350 * scale, scale, scale, this.iterations.getValue());

		// Custom color mapping
		if (this.colorMode == 1) {
			for (int i = 0; i < set.maxIterations; i++)
				set.colors[i] = Color.color(Math.sqrt(i / (double) set.maxIterations), 0, 0);
			set.colors[set.maxIterations - 1] = Color.BLACK;
		} else if (this.colorMode == 2) {
			for (int i = 0; i < set.maxIterations; i++)
				set.colors[i] = Color.color(Math.pow(i / (double) set.maxIterations, 2), 0, 0);
			set.colors[set.maxIterations - 1] = Color.BLACK;
		}

		ObservableProgressTask task = this.taskSupplier.get();
		Platform.runLater(() -> this.progressBar.progressProperty().bind(task.progressProperty()));
		this.executor.execute(() -> {
			try {
				WritableImage image = set.generate(700, 700, task.expect(700 * 700));

				if (!task.isCancelled()) {
					this.zooming = false;
					Platform.runLater(() -> this.view.setImage(image));
				}
			} catch (Exception e) {
				logger.error("Failed to generate the mandelbrot set", e);
			}
		});
	}

	private <T> void update(ObservableValue<? extends T> observable, T oldValue, T newValue) {
		generate();
	}
}
