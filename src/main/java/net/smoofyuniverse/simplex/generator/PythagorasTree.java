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

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import net.smoofyuniverse.simplex.util.PathHelper;
import org.spongepowered.math.TrigMath;

public class PythagorasTree {
	public static final double MIN_FACTOR = 0, MAX_FACTOR = 1;
	public static final double MIN_ANGLE = 0, MAX_ANGLE = TrigMath.HALF_PI;

	public final double widthFactor, lengthFactor;
	public final double leftAngle, rightAngle;

	public PythagorasTree(double widthFactor, double lengthFactor, double leftAngle, double rightAngle) {
		if (widthFactor < MIN_FACTOR || widthFactor > MAX_FACTOR)
			throw new IllegalArgumentException("widthFactor");
		if (lengthFactor < MIN_FACTOR || lengthFactor > MAX_FACTOR)
			throw new IllegalArgumentException("lengthFactor");

		if (leftAngle < MIN_ANGLE || leftAngle > MAX_ANGLE)
			throw new IllegalArgumentException("leftAngle");
		if (rightAngle < MIN_ANGLE || rightAngle > MAX_ANGLE)
			throw new IllegalArgumentException("rightAngle");

		this.widthFactor = widthFactor;
		this.lengthFactor = lengthFactor;

		this.leftAngle = leftAngle;
		this.rightAngle = rightAngle;
	}

	public Color getColor(int n) {
		if (n < 0)
			throw new IllegalArgumentException("n");
		return Color.GREEN.interpolate(Color.SADDLEBROWN, 1 - Math.pow(0.85, n - 2));
	}

	public void generate(PathHelper h, int n, double initialWidth, double initialLength) {
		if (n < 0)
			throw new IllegalArgumentException("n");
		if (initialWidth <= 0)
			throw new IllegalArgumentException("initialWidth");
		if (initialLength <= 0)
			throw new IllegalArgumentException("initialLength");

		Paint stroke = h.graphics.getStroke();
		double lw = h.graphics.getLineWidth();

		_generate(h, n, initialWidth, initialLength);

		h.graphics.setStroke(stroke);
		h.graphics.setLineWidth(lw);
	}

	private void _generate(PathHelper h, int n, double width, double length) {
		if (n == -1)
			return;

		h.graphics.setStroke(getColor(n));
		h.graphics.setLineWidth(width);
		h.move(length, true);

		n--;
		double newWidth = width * this.widthFactor, newLength = length * this.lengthFactor;
		if (newWidth == 0 || newLength == 0)
			return;

		h.rotate(-this.leftAngle);
		_generate(h, n, newWidth, newLength);
		h.rotate(this.leftAngle + this.rightAngle);
		_generate(h, n, newWidth, newLength);
		h.rotate(-this.rightAngle);

		h.move(-length, false);
	}
}
