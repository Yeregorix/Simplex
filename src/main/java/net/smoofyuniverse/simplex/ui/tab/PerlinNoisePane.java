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

import com.flowpowered.noise.NoiseQuality;
import com.flowpowered.noise.module.source.Perlin;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.smoofyuniverse.common.app.Application;
import net.smoofyuniverse.logger.core.Logger;
import net.smoofyuniverse.simplex.ui.UserInterface;

public class PerlinNoisePane extends StackPane {
	private static final Logger logger = Logger.get("PerlinNoisePane");

	private final UserInterface ui;
	private final ImageView view = new ImageView();

	private final Perlin perlin = new Perlin();
	private double min = Double.MAX_VALUE, max = Double.MIN_VALUE;
	private long currentTime = 0;

	private final Task task = new Task();

	public PerlinNoisePane(UserInterface ui) {
		this.ui = ui;

		this.view.setScaleX(2);
		this.view.setScaleY(2);

		getChildren().add(this.view);

		this.perlin.setSeed(2);
		this.perlin.setOctaveCount(8);
		this.perlin.setFrequency(0.01d);
		this.perlin.setLacunarity(2d);
		this.perlin.setPersistence(0.5d);
		this.perlin.setNoiseQuality(NoiseQuality.STANDARD);

		this.task.start();
	}

	public boolean isSelected() {
		return this.ui.isSelected(this);
	}

	private WritableImage generateImage(int width, int height) {
		if (width <= 0 || height <= 0)
			throw new IllegalArgumentException();

		WritableImage image = new WritableImage(width, height);
		generateImage(image.getPixelWriter(), width, height);
		return image;
	}

	private void generateImage(PixelWriter writer, int width, int height) {
		Perlin perlin = this.perlin;
		long t = this.currentTime;

		double min = this.min, max = this.max;
		double[][] pixels = new double[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				double value = perlin.getValue(x, y, t);

				if (value > max)
					max = value;
				if (value < min)
					min = value;

				pixels[x][y] = value;
			}
		}

		double range = max - min;
		this.min = min;
		this.max = max;

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++)
				writer.setColor(x, y, Color.gray((pixels[x][y] - min) / range));
		}
	}

	private class Task extends Thread {
		@Override
		public void run() {
			while (Application.get().getState() != net.smoofyuniverse.common.app.State.SHUTDOWN) {
				long start = System.currentTimeMillis();

				if (isSelected()) {
					PerlinNoisePane.this.currentTime++;
					WritableImage image = generateImage(400, 400);
					Platform.runLater(() -> PerlinNoisePane.this.view.setImage(image));
				}

				long dur = System.currentTimeMillis() - start;

				if (dur < 70) {
					try {
						Thread.sleep(70 - dur);
					} catch (InterruptedException e) {
						logger.error(e);
					}
				}
			}
		}
	}
}
