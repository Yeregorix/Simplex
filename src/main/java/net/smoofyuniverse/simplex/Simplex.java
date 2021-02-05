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

package net.smoofyuniverse.simplex;

import net.smoofyuniverse.common.app.Application;
import net.smoofyuniverse.common.app.Arguments;
import net.smoofyuniverse.common.environment.ApplicationUpdater;
import net.smoofyuniverse.common.environment.DependencyInfo;
import net.smoofyuniverse.common.environment.DependencyManager;
import net.smoofyuniverse.common.environment.source.GithubReleaseSource;
import net.smoofyuniverse.simplex.ui.UserInterface;

public class Simplex extends Application {
	public static final DependencyInfo FLOW_MATH = new DependencyInfo("org.spongepowered:math:2.0.0-SNAPSHOT", "https://repo.spongepowered.org/repository/sponge-legacy/org/spongepowered/math/2.0.0-SNAPSHOT/math-2.0.0-20201013.013515-3.jar", 165471, "bdf567735a83ef1511f86078c1cf029c6e2b38bc", "sha1"),
			FLOW_NOISE = new DependencyInfo("org.spongepowered:noise:2.0.0-SNAPSHOT", "https://repo.spongepowered.org/repository/sponge-legacy/org/spongepowered/noise/2.0.0-SNAPSHOT/noise-2.0.0-20190606.000239-1.jar", 55099, "7cdb48fa0c018537d272dc57da311138c5c1d6d3", "sha1");

	public Simplex(Arguments args) {
		super(args, "Simplex", "1.0.4");
	}

	@Override
	public void init() {
		requireGUI();
		initServices();

		if (!this.devEnvironment) {
			new DependencyManager(this, FLOW_MATH, FLOW_NOISE).setup();
		}

		runLater(() -> {
			initStage(1000, 900, "favicon.png");
			setScene(new UserInterface()).show();
		});

		new ApplicationUpdater(this, new GithubReleaseSource("Yeregorix", "Simplex", null, "Simplex", getConnectionConfig())).run();
	}

	public static void main(String[] args) {
		new Simplex(Arguments.parse(args)).launch();
	}
}
