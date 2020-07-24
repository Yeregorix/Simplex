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

package net.smoofyuniverse.simplex.ui;

import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import net.smoofyuniverse.simplex.ui.tab.KochSnowflakePane;
import net.smoofyuniverse.simplex.ui.tab.MandelbrotSetPane;
import net.smoofyuniverse.simplex.ui.tab.PerlinNoisePane;
import net.smoofyuniverse.simplex.ui.tab.PythagorasTreePane;

public class UserInterface extends TabPane {

	public UserInterface() {
		getTabs().addAll(createTab("Perlin", new PerlinNoisePane(this)),
				createTab("Koch", new KochSnowflakePane()),
				createTab("Pythagoras", new PythagorasTreePane()),
				createTab("Mandelbrot", new MandelbrotSetPane()));

		setSide(Side.BOTTOM);
		setCache(true);
	}

	private static Tab createTab(String text, Node content) {
		Tab tab = new Tab(text, content);
		tab.setClosable(false);
		return tab;
	}

	public boolean isSelected(Node node) {
		Tab tab = getSelectionModel().getSelectedItem();
		return tab != null && tab.getContent() == node;
	}
}
