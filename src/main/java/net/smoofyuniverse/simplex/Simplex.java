/*
 * Copyright (c) 2019 Hugo Dupanloup (Yeregorix)
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

import net.smoofyuniverse.common.app.App;
import net.smoofyuniverse.common.app.Application;
import net.smoofyuniverse.common.app.Arguments;
import net.smoofyuniverse.common.environment.DependencyInfo;
import net.smoofyuniverse.common.environment.source.GithubReleaseSource;
import net.smoofyuniverse.simplex.ui.UserInterface;

import java.util.concurrent.Executors;

public class Simplex extends Application {
	public static final DependencyInfo FLOW_MATH = new DependencyInfo("com.flowpowered:flow-math:1.0.3", "http://central.maven.org/maven2/com/flowpowered/flow-math/1.0.3/flow-math-1.0.3.jar", 167837, "d98020239e5015091ad3be927cef9dea0d61a234", "sha1"),
			FLOW_NOISE = new DependencyInfo("com.flowpowered:flow-noise:1.0.1-SNAPSHOT", "https://repo.spongepowered.org/maven/com/flowpowered/flow-noise/1.0.1-SNAPSHOT/flow-noise-1.0.1-20150609.030116-1.jar", 68228, "bfddff85287441521fb66ec22b59a463190966e1", "sha1");

	public Simplex(Arguments args) {
		super(args, "Simplex", "1.0.2");
	}

	@Override
	public void init() {
		requireUI();
		initServices(Executors.newSingleThreadExecutor());

		if (!this.devEnvironment) {
			if (!updateDependencies(this.workingDir.resolve("libraries"), FLOW_MATH, FLOW_NOISE)) {
				shutdown();
				return;
			}
			loadDependencies(FLOW_MATH, FLOW_NOISE);
		}

		App.runLater(() -> {
			initStage(1000, 900, true, "favicon.png");
			setScene(new UserInterface()).show();
		});

		tryUpdateApplication(new GithubReleaseSource("Yeregorix", "Simplex", null, "Simplex"));
	}

	public static void main(String[] args) {
		new Simplex(Arguments.parse(args)).launch();
	}
}
