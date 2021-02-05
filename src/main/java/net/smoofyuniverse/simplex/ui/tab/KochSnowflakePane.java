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

import com.flowpowered.math.TrigMath;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import net.smoofyuniverse.simplex.util.PathHelper;

public class KochSnowflakePane extends StackPane {
	public static final double PI_OVER_3 = TrigMath.PI / 3;

	private final Canvas canvas = new Canvas(600, 600);

	public KochSnowflakePane() {
		getChildren().add(this.canvas);

		GraphicsContext g = this.canvas.getGraphicsContext2D();
		g.setFill(Color.WHITE);
		g.fillRect(0, 0, 600, 600);

		g.setStroke(Color.BLACK);
		g.setLineWidth(0.1);

		PathHelper h = new PathHelper(g);
		h.currentX = 50;
		h.currentY = 445;
		generateKochSnowflake(h, 9, 500);
	}

	public static void generateKochSnowflake(PathHelper h, int n, double length) {
		generateKochCurve(h, n, length);
		h.rotate(-2 * PI_OVER_3);
		generateKochCurve(h, n, length);
		h.rotate(-2 * PI_OVER_3);
		generateKochCurve(h, n, length);
		h.rotate(-2 * PI_OVER_3);
	}

	public static void generateKochCurve(PathHelper h, int n, double length) {
		if (n == 0)
			h.move(length, true);
		else {
			length /= 3;
			n--;

			generateKochCurve(h, n, length);
			h.rotate(PI_OVER_3);
			generateKochCurve(h, n, length);
			h.rotate(-2 * PI_OVER_3);
			generateKochCurve(h, n, length);
			h.rotate(PI_OVER_3);
			generateKochCurve(h, n, length);
		}
	}
}
