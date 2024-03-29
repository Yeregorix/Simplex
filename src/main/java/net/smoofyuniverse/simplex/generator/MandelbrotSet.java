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
import net.smoofyuniverse.common.logger.ApplicationLogger;
import net.smoofyuniverse.common.task.IncrementalListener;
import org.slf4j.Logger;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;

public class MandelbrotSet {
	private static final Logger logger = ApplicationLogger.get(MandelbrotSet.class);
	public final int blocks;
	public final double minX, minY;
	public final double xScale, yScale;
	public final int maxIterations;
	public final Color[] colors;
	private final ExecutorService executor;

	public MandelbrotSet(ExecutorService executor, int blocks, double minX, double minY, double xScale, double yScale, int maxIterations) {
		if (blocks <= 0)
			throw new IllegalArgumentException("blocks");
		if (executor == null)
			throw new IllegalArgumentException("executor");
		if (xScale == 0)
			throw new IllegalArgumentException("xScale");
		if (yScale == 0)
			throw new IllegalArgumentException("yScale");
		if (maxIterations <= 0)
			throw new IllegalArgumentException("maxIterations");

		this.executor = executor;
		this.blocks = blocks;
		this.minX = minX;
		this.minY = minY;
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
		CountDownLatch latch = new CountDownLatch(this.blocks);
		int size = width * height;
		int blockSize = (int) Math.ceil(size / (double) this.blocks);

		for (int i = 0; i < this.blocks; i++) {
			if (listener.isCancelled())
				return;

			int blockMin = blockSize * i;
			this.executor.execute(() -> {
				try {
					int blockMax = blockMin + blockSize;
					if (blockMax > size)
						blockMax = size;

					for (int p = blockMin; p < blockMax; p++) {
						if (listener.isCancelled())
							return;

						int pX = p % width, pY = p / width;

						double x = this.minX + pX * this.xScale;
						double y = this.minY + pY * this.yScale;

						writer.setColor(pX, pY, this.colors[getIterations(x, y) - 1]);
						listener.increment(1);
					}
				} finally {
					latch.countDown();
				}
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			logger.error("Interruption", e);
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
