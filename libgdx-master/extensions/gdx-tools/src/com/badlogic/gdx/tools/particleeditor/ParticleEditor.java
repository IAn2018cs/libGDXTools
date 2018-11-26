/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.badlogic.gdx.tools.particleeditor;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.CompoundBorder;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl.LwjglCanvas;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.GradientColorValue;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.NumericValue;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;

public class ParticleEditor extends JFrame implements OnBackgroundChangeListener {
	public static final String DEFAULT_PARTICLE = "particle.png";

	public static final String DEFAULT_PREMULT_PARTICLE = "pre_particle.png";

	LwjglCanvas lwjglCanvas;
	JPanel rowsPanel;
	JPanel editRowsPanel;
	EffectPanel effectPanel;
	private JSplitPane splitPane;
	OrthographicCamera worldCamera;
	OrthographicCamera textCamera;
	NumericValue pixelsPerMeter;
	NumericValue zoomLevel;
	NumericValue deltaMultiplier;
	GradientColorValue backgroundColor;

	float pixelsPerMeterPrev;
	float zoomLevelPrev;

	ParticleEffect effect = new ParticleEffect();
	File effectFile;
	final HashMap<ParticleEmitter, ParticleData> particleData = new HashMap();

	public ParticleEditor () {
		super("粒子编辑器--AmberWeather汉化版");

		lwjglCanvas = new LwjglCanvas(new Renderer());
		addWindowListener(new WindowAdapter() {
			public void windowClosed (WindowEvent event) {
				System.exit(0);
				// Gdx.app.quit();
			}
		});

		initializeComponents();

		setSize(1000, 950);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	void reloadRows () {
		EventQueue.invokeLater(new Runnable() {
			public void run () {
				editRowsPanel.removeAll();
				addEditorRow(new NumericPanel(pixelsPerMeter, "每米像素", ""));
				addEditorRow(new NumericPanel(zoomLevel, "缩放级别", ""));
				addEditorRow(new NumericPanel(deltaMultiplier, "δ乘法器", ""));
				addEditorRow(new GradientPanel(backgroundColor, "背景颜色", "", true));
				// TODO 添加按钮
				addEditorRow(new ImageChoicePanel(ParticleEditor.this, "选择背景图片", ""));

				rowsPanel.removeAll();
				ParticleEmitter emitter = getEmitter();
				addRow(new ImagePanel(ParticleEditor.this, "粒子图像", ""));
				addRow(new CountPanel(ParticleEditor.this, "粒子数量",
					"同一时间内粒子数量的最大值和最小值."));
				addRow(new RangedNumericPanel(emitter.getDelay(), "延迟发射",
					"当粒子系统开始后，发射器等待多少时间开始发射."));
				addRow(new RangedNumericPanel(emitter.getDuration(), "持续时间", "粒子效果持续时间, 以毫秒为单位."));
				addRow(new ScaledNumericPanel(emitter.getEmission(), "持续时间", "每秒粒子数量",
					"每秒钟产生的粒子数量."));
				addRow(new ScaledNumericPanel(emitter.getLife(), "持续时间", "粒子生命", "粒子的存活时间, 以毫秒为单位."));
				addRow(new ScaledNumericPanel(emitter.getLifeOffset(), "持续时间", "生命偏移量",
					"粒子显示前用掉的生命值, 以毫秒为单位."));
				addRow(new RangedNumericPanel(emitter.getXOffsetValue(), "X 偏移量",
					"粒子出现 X 位置相对中心位置的像素偏移量."));
				addRow(new RangedNumericPanel(emitter.getYOffsetValue(), "Y 偏移量",
					"粒子出现 Y 位置相对中心位置的像素偏移量."));
				addRow(new SpawnPanel(ParticleEditor.this, emitter.getSpawnShape(), "发射器形状", "用于产生粒子的发射器形状."));
				addRow(new ScaledNumericPanel(emitter.getSpawnWidth(), "持续时间", "发射器宽度",
					"发射器形状的宽度."));
				addRow(new ScaledNumericPanel(emitter.getSpawnHeight(), "持续时间", "发射器高度",
					"发射器形状的高度."));
				addRow(new ScaledNumericPanel(emitter.getXScale(), "粒子生命", "X 的大小",
					"粒子 x 的大小. 如果 Y 的大小没有启用, 那么它也控制  Y 的大小"));
				addRow(new ScaledNumericPanel(emitter.getYScale(), "粒子生命", "Y 的大小", "粒子 Y 的大小."));
				addRow(new ScaledNumericPanel(emitter.getVelocity(), "粒子生命", "速度", "粒子每秒的速度."));
				addRow(new ScaledNumericPanel(emitter.getAngle(), "粒子生命", "角度", "粒子发射角度, 以度为单位."));
				addRow(new ScaledNumericPanel(emitter.getRotation(), "粒子生命", "旋转", "粒子旋转, 以度为单位."));
				addRow(new ScaledNumericPanel(emitter.getWind(), "粒子生命", "风", "风力强度, 每秒的偏移量."));
				addRow(new ScaledNumericPanel(emitter.getGravity(), "粒子生命", "重力", "重力强度, 每秒的偏移量."));
				addRow(new GradientPanel(emitter.getTint(), "粒子颜色", "", false));
				addRow(new PercentagePanel(emitter.getTransparency(), "粒子生命", "粒子透明度", ""));
				addRow(new OptionsPanel(ParticleEditor.this, "选项", ""));
				for (Component component : rowsPanel.getComponents())
					if (component instanceof EditorPanel) ((EditorPanel)component).update(ParticleEditor.this);
				rowsPanel.repaint();
			}
		});
	}

	void addEditorRow (JPanel row) {
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		editRowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));
	}

	void addRow (JPanel row) {
		row.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, java.awt.Color.black));
		rowsPanel.add(row, new GridBagConstraints(0, -1, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.HORIZONTAL,
			new Insets(0, 0, 0, 0), 0, 0));
	}

	public void setVisible (String name, boolean visible) {
		for (Component component : rowsPanel.getComponents())
			if (component instanceof EditorPanel && ((EditorPanel)component).getName().equals(name)) component.setVisible(visible);
	}

	public ParticleEmitter getEmitter () {
		return effect.getEmitters().get(effectPanel.editIndex);
	}

	public void setEnabled (ParticleEmitter emitter, boolean enabled) {
		ParticleData data = particleData.get(emitter);
		if (data == null) particleData.put(emitter, data = new ParticleData());
		data.enabled = enabled;
		emitter.reset();
	}

	public boolean isEnabled (ParticleEmitter emitter) {
		ParticleData data = particleData.get(emitter);
		if (data == null) return true;
		return data.enabled;
	}

	private void initializeComponents () {
		// {
		// JMenuBar menuBar = new JMenuBar();
		// setJMenuBar(menuBar);
		// JPopupMenu.setDefaultLightWeightPopupEnabled(false);
		// JMenu fileMenu = new JMenu("File");
		// menuBar.add(fileMenu);
		// }
		splitPane = new JSplitPane();
		splitPane.setUI(new BasicSplitPaneUI() {
			public void paint (Graphics g, JComponent jc) {
			}
		});
		splitPane.setDividerSize(4);
		getContentPane().add(splitPane, BorderLayout.CENTER);
		{
			JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			rightSplit.setUI(new BasicSplitPaneUI() {
				public void paint (Graphics g, JComponent jc) {
				}
			});
			rightSplit.setDividerSize(4);
			splitPane.add(rightSplit, JSplitPane.RIGHT);

			{
				JPanel propertiesPanel = new JPanel(new GridBagLayout());
				rightSplit.add(propertiesPanel, JSplitPane.TOP);
				propertiesPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
					.createTitledBorder("属性编辑")));
				{
					JScrollPane scroll = new JScrollPane();
					propertiesPanel.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					{
						editRowsPanel = new JPanel(new GridBagLayout());
						scroll.setViewportView(editRowsPanel);
						scroll.getVerticalScrollBar().setUnitIncrement(70);
					}
				}
			}

			{
				JPanel propertiesPanel = new JPanel(new GridBagLayout());
				rightSplit.add(propertiesPanel, JSplitPane.BOTTOM);
				propertiesPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(3, 0, 6, 6), BorderFactory
					.createTitledBorder("粒子发射器属性")));
				{
					JScrollPane scroll = new JScrollPane();
					propertiesPanel.add(scroll, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.NORTH,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
					scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
					{
						rowsPanel = new JPanel(new GridBagLayout());
						scroll.setViewportView(rowsPanel);
						scroll.getVerticalScrollBar().setUnitIncrement(70);
					}
				}
			}
			rightSplit.setDividerLocation(200);

		}
		{
			JSplitPane leftSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
			leftSplit.setUI(new BasicSplitPaneUI() {
				public void paint (Graphics g, JComponent jc) {
				}
			});
			leftSplit.setDividerSize(4);
			splitPane.add(leftSplit, JSplitPane.LEFT);
			{
				JPanel spacer = new JPanel(new BorderLayout());
				leftSplit.add(spacer, JSplitPane.BOTTOM);
				spacer.add(lwjglCanvas.getCanvas());
				spacer.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
			}
			{
				JPanel emittersPanel = new JPanel(new BorderLayout());
				leftSplit.add(emittersPanel, JSplitPane.TOP);
				emittersPanel.setBorder(new CompoundBorder(BorderFactory.createEmptyBorder(0, 6, 6, 0), BorderFactory
					.createTitledBorder("粒子发射器效果")));
				{
					effectPanel = new EffectPanel(this);
					emittersPanel.add(effectPanel);
				}
			}
			leftSplit.setDividerLocation(575);
		}
		splitPane.setDividerLocation(325);
	}

	
	Texture texture;
	
	// TODO 添加的方法
	@Override
	public void backgroundChange (String imagePath) {
		FileHandle file = Gdx.files.absolute(imagePath);
		texture = new Texture(file);
	}

	@Override
	public void clearBackground () {
		texture = null;
	}

	// 粒子效果预览
	class Renderer implements ApplicationListener, InputProcessor {
		private float maxActiveTimer;
		private int maxActive, lastMaxActive;
		private boolean mouseDown;
		private int activeCount;
		private int mouseX, mouseY;
		private BitmapFont font;
		private SpriteBatch spriteBatch;
		private Sprite bgImage; // BOZO - Add setting background image to UI.

		private Image bg;

		private TextureRegion region;

		public void create () {
			if (spriteBatch != null) return;

			spriteBatch = new SpriteBatch();

			worldCamera = new OrthographicCamera();
			textCamera = new OrthographicCamera();

			pixelsPerMeter = new NumericValue();
			pixelsPerMeter.setValue(1.0f);
			pixelsPerMeter.setAlwaysActive(true);

			zoomLevel = new NumericValue();
			zoomLevel.setValue(1.0f);
			zoomLevel.setAlwaysActive(true);

			deltaMultiplier = new NumericValue();
			deltaMultiplier.setValue(1.0f);
			deltaMultiplier.setAlwaysActive(true);

			backgroundColor = new GradientColorValue();
			backgroundColor.setColors(new float[] {0f, 0f, 0f});

			font = new BitmapFont(Gdx.files.getFileHandle("default.fnt", FileType.Internal), Gdx.files.getFileHandle("default.png",
				FileType.Internal), true);
			effectPanel.newExampleEmitter("Untitled", true);
			// if (resources.openFile("/editor-bg.png") != null) bgImage = new Image(gl, "/editor-bg.png");

			// 添加开始
			// texture = new Texture(Gdx.files.internal("splash.jpg"));
			// 结束

			Gdx.input.setInputProcessor(this);
		}

		@Override
		public void resize (int width, int height) {
			Gdx.gl.glViewport(0, 0, width, height);

			if (pixelsPerMeter.getValue() <= 0) {
				pixelsPerMeter.setValue(1);
			}
			worldCamera.setToOrtho(false, width / pixelsPerMeter.getValue(), height / pixelsPerMeter.getValue());
			worldCamera.update();

			textCamera.setToOrtho(true, width, height);
			textCamera.update();

			effect.setPosition(worldCamera.viewportWidth / 2, worldCamera.viewportHeight / 2);
		}

		public void render () {
			int viewWidth = Gdx.graphics.getWidth();
			int viewHeight = Gdx.graphics.getHeight();

			float delta = Math.max(0, Gdx.graphics.getDeltaTime() * deltaMultiplier.getValue());

			float[] colors = backgroundColor.getColors();
			Gdx.gl.glClearColor(colors[0], colors[1], colors[2], 1.0f);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

			if ((pixelsPerMeter.getValue() != pixelsPerMeterPrev) || (zoomLevel.getValue() != zoomLevelPrev)) {
				if (pixelsPerMeter.getValue() <= 0) {
					pixelsPerMeter.setValue(1);
				}

				worldCamera.setToOrtho(false, viewWidth / pixelsPerMeter.getValue(), viewHeight / pixelsPerMeter.getValue());
				worldCamera.zoom = zoomLevel.getValue();
				worldCamera.update();
				effect.setPosition(worldCamera.viewportWidth / 2, worldCamera.viewportHeight / 2);
				zoomLevelPrev = zoomLevel.getValue();
				pixelsPerMeterPrev = pixelsPerMeter.getValue();
			}

			spriteBatch.setProjectionMatrix(worldCamera.combined);

			spriteBatch.begin();
			spriteBatch.enableBlending();
			spriteBatch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

			if (bgImage != null) {
				bgImage.setPosition(viewWidth / 2 - bgImage.getWidth() / 2, viewHeight / 2 - bgImage.getHeight() / 2);
				bgImage.draw(spriteBatch);
			}

			// 添加功能开始
			if (texture != null) {
				// TODO 用于图片的剪裁
				int width = texture.getWidth();
				int height = texture.getHeight();
				
				// 高宽比之差
				int temp = viewHeight/viewWidth - height/width;
				// 判断高宽比例，如果目标高宽比例大于原图，则原图高度不变，宽度为(w1 = (height * viewWidth) / viewHeight)拉伸
				// 画布宽高(w1,height),在原图的((width - w1) / 2, 0)位置进行切割
				if (temp > 0) {
					int w1 = (height * viewWidth) / viewHeight;
					
					int left = (width - w1) / 2;
					int top = 0;
					int right = w1;
					int bottom = height;
					
					region = new TextureRegion(texture, left, top, right, bottom);
					// 显示图片
					spriteBatch.draw(region, 0, 0, viewWidth, viewHeight);
				} else {
					int h1 = (viewHeight * width) / viewWidth;
					
					int left = 0;
					int top =(height - h1)/2;
					int right = width;
					int bottom = h1;
					
					region = new TextureRegion(texture, left, top, right, bottom);
					// 显示图片
					spriteBatch.draw(region, 0, 0, viewWidth, viewHeight);
				}
			}
			// 添加功能结束

			activeCount = 0;
			boolean complete = true;
			for (ParticleEmitter emitter : effect.getEmitters()) {
				if (emitter.getSprites().size == 0 && emitter.getImagePaths().size > 0) loadImages(emitter);
				boolean enabled = isEnabled(emitter);
				if (enabled) {
					if (emitter.getSprites().size > 0) emitter.draw(spriteBatch, delta);
					activeCount += emitter.getActiveCount();
					if (!emitter.isComplete()) complete = false;
				}
			}
			if (complete) effect.start();

			maxActive = Math.max(maxActive, activeCount);
			maxActiveTimer += delta;
			if (maxActiveTimer > 3) {
				maxActiveTimer = 0;
				lastMaxActive = maxActive;
				maxActive = 0;
			}

			if (mouseDown) {
				// gl.drawLine(mouseX - 6, mouseY, mouseX + 5, mouseY);
				// gl.drawLine(mouseX, mouseY - 5, mouseX, mouseY + 6);
			}

			spriteBatch.setProjectionMatrix(textCamera.combined);

			font.draw(spriteBatch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 5, 15);
			font.draw(spriteBatch, "Count: " + activeCount, 5, 35);
			font.draw(spriteBatch, "Max: " + lastMaxActive, 5, 55);
			font.draw(spriteBatch, (int)(getEmitter().getPercentComplete() * 100) + "%", 5, 75);

			spriteBatch.end();

			// gl.drawLine((int)(viewWidth * getCurrentParticles().getPercentComplete()), viewHeight - 1, viewWidth, viewHeight -
			// 1);
		}

		private void loadImages (ParticleEmitter emitter) {
			String imagePath = null;
			try {
				Array<Sprite> sprites = new Array<Sprite>();
				Array<String> imagePaths = emitter.getImagePaths();
				for (int i = 0; i < imagePaths.size; i++) {
					imagePath = imagePaths.get(i);
					String imageName = new File(imagePath.replace('\\', '/')).getName();
					FileHandle file;
					if (imagePath.equals(ParticleEditor.DEFAULT_PARTICLE) || imagePath.equals(ParticleEditor.DEFAULT_PREMULT_PARTICLE)) {
						file = Gdx.files.classpath(imagePath);
					} else {
						if ((imagePath.contains("/") || imagePath.contains("\\")) && !imageName.contains("..")) {
							file = Gdx.files.absolute(imagePath);
							if (!file.exists()) {
								// 尝试在效果目录中使用图像
								file = Gdx.files.absolute(new File(effectFile.getParentFile(), imageName).getAbsolutePath());
							}
						} else {
							file = Gdx.files.absolute(new File(effectFile.getParentFile(), imagePath).getAbsolutePath());
						}
					}
					sprites.add(new Sprite(new Texture(file)));
				}
				emitter.setSprites(sprites);
			} catch (GdxRuntimeException ex) {
				ex.printStackTrace();
				final String imagePathFinal = imagePath;
				EventQueue.invokeLater(new Runnable() {
					public void run () {
						JOptionPane.showMessageDialog(ParticleEditor.this, "Error loading image:\n" + imagePathFinal);
					}
				});
				emitter.getImagePaths().clear();
			}
		}

		public boolean keyDown (int keycode) {
			return false;
		}

		public boolean keyUp (int keycode) {
			return false;
		}

		public boolean keyTyped (char character) {
			return false;
		}

		public boolean touchDown (int x, int y, int pointer, int newParam) {
			Vector3 touchPoint = new Vector3(x, y, 0);
			worldCamera.unproject(touchPoint);
			effect.setPosition(touchPoint.x, touchPoint.y);
			return false;
		}

		public boolean touchUp (int x, int y, int pointer, int button) {
			ParticleEditor.this.dispatchEvent(new WindowEvent(ParticleEditor.this, WindowEvent.WINDOW_LOST_FOCUS));
			ParticleEditor.this.dispatchEvent(new WindowEvent(ParticleEditor.this, WindowEvent.WINDOW_GAINED_FOCUS));
			ParticleEditor.this.requestFocusInWindow();
			return false;
		}

		public boolean touchDragged (int x, int y, int pointer) {
			Vector3 touchPoint = new Vector3(x, y, 0);
			worldCamera.unproject(touchPoint);
			effect.setPosition(touchPoint.x, touchPoint.y);
			return false;
		}

		@Override
		public void dispose () {
		}

		@Override
		public void pause () {
		}

		@Override
		public void resume () {
		}

		@Override
		public boolean mouseMoved (int x, int y) {
			return false;
		}

		@Override
		public boolean scrolled (int amount) {
			return false;
		}
	}

	static class ParticleData {
		public boolean enabled = true;
	}

	public static void main (String[] args) {
		for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
			if ("Nimbus".equals(info.getName())) {
				try {
					UIManager.setLookAndFeel(info.getClassName());
				} catch (Throwable ignored) {
				}
				break;
			}
		}
		EventQueue.invokeLater(new Runnable() {
			public void run () {
				new ParticleEditor();
			}
		});
	}
}
