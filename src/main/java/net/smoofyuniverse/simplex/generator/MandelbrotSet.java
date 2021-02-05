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

package net.smoofyuniverse.simplex.generator;

import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import net.smoofyuniverse.common.task.IncrementalListener;
import net.smoofyuniverse.logger.core.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class MandelbrotSet {
	private static final Logger logger = Logger.get("MandelbrotSet");
	public final int threads;
	public final double fromX, fromY;
	public final double xScale, yScale;
	public final int maxIterations;
	public final Color[] colors;
	private final ExecutorService executor;

	public MandelbrotSet(ExecutorService executor, int threads, double fromX, double fromY, double xScale, double yScale, int maxIterations) {
		if (threads <= 0)
			throw new IllegalArgumentException("threads");
		if (executor == null && threads != 1)
			throw new IllegalArgumentException("executor");
		if (xScale == 0)
			throw new IllegalArgumentException("xScale");
		if (yScale == 0)
			throw new IllegalArgumentException("yScale");
		if (maxIterations <= 0)
			throw new IllegalArgumentException("maxIterations");

		this.executor = executor;
		this.threads = threads;
		this.fromX = fromX;
		this.fromY = fromY;
		this.xScale = xScale;
		this.yScale = yScale;
		this.maxIterations = maxIterations;
		this.colors = new Color[maxIterations];

		// Default color mapping
		for (int i = 0; i < maxIterations; i++)
			this.colors[i] = Color.color(i / (double) maxIterations, 0, 0);
		this.colors[maxIterations - 1] = Color.BLACK;
	}

	public WritableImage generate(int width, int height, IncrementalListener listener) {
		WritableImage image = new WritableImage(width, height);
		generate(image.getPixelWriter(), width, height, listener);
		return image;
	}

	public void generate(PixelWriter writer, int width, int height, IncrementalListener listener) {
		if (this.executor == null) {
			for (int pX = 0; pX < width; pX++) {
				double x = this.fromX + pX * this.xScale;
				for (int pY = 0; pY < height; pY++) {
					double y = this.fromY + pY * this.yScale;

					if (listener.isCancelled())
						return;

					writer.setColor(pX, pY, this.colors[getIterations(x, y) - 1]);
					listener.increment(1);
				}
			}
		} else {
			CountDownLatch latch = new CountDownLatch(this.threads);
			int partialWidth = width / this.threads;

			for (int i = 0; i < this.threads; i++) {
				if (listener.isCancelled())
					return;

				int offset = partialWidth * i;

				this.executor.execute(() -> {
					try {
						for (int pX = 0; pX < partialWidth; pX++) {
							double x = this.fromX + (offset + pX) * this.xScale;
							for (int pY = 0; pY < height; pY++) {
								double y = this.fromY + pY * this.yScale;

								if (listener.isCancelled())
									return;

								writer.setColor(offset + pX, pY, this.colors[getIterations(x, y) - 1]);
								listener.increment(1);
							}
						}
					} finally {
						latch.countDown();
					}
				});
			}

			try {
				latch.await();
			} catch (InterruptedException e) {
				logger.error(e);
			}
		}
	}

	public int getIterations(double x, double y) {
		double r = 0, i = 0, r2 = 0, i2 = 0;

		int c = 0;
		while (r2 + i2 < 4 && c < this.maxIterations) {
			i = 2 * i * r + y;
			r = r2 - i2 + x;

			r2 = r * r;
			i2 = i * i;

			c++;
		}

		return c;
	}
}
