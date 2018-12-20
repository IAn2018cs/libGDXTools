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

import java.awt.Container;
import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpriteMode;
import com.badlogic.gdx.utils.Array;

class ImageChoicePanel extends EditorPanel {
	JPanel imagesPanel;
	JList imageList;
	DefaultListModel<String> imageListModel;
	String lastDir;
	final String twoDLogo = "Image Path";
	final String threeDLogo = "assets:";
	final String threeDLogo2 = "filename:";
	final String pngEndIndex = ".png";
	final String jpgEndIndex = ".jpg";
	ImgJudegFrame frame;
	List<ImgModel> imgModels = new ArrayList();

	public ImageChoicePanel (final ParticleEditor editor, String name, String description) {
		super(null, name, description);
		JPanel contentPanel = getContentPanel();
		{
			JButton configFileButton = new JButton("验证图片是否存在");
			contentPanel.add(configFileButton);
			configFileButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent arg0) {
					try {
						JFileChooser jFileChooser = new JFileChooser(ImageChoicePanel.this.lastDir);
						jFileChooser.setFileSelectionMode(2);
						jFileChooser.showDialog(new JLabel(), "选择");

						File file = jFileChooser.getSelectedFile();
						if (file.isDirectory())
							System.out.println("文件夹:" + file.getAbsolutePath());
						else if (file.isFile()) {
							System.out.println("文件:" + file.getAbsolutePath());
						}

						if (file.isFile()) return;

						File[] enableFileList = file.listFiles();

						for (int i = 0; i < enableFileList.length; i++) {
							if ((!enableFileList[i].isFile()) || (!enableFileList[i].getName().endsWith(".p"))) continue;
							ImageChoicePanel.ImgModel model = new ImageChoicePanel.ImgModel();

							model.setpFileName(enableFileList[i].getCanonicalPath());
							model.setpFName(enableFileList[i].getName());
							System.out.println(enableFileList[i].getName());

							ImageChoicePanel.this.forEach2D(model, enableFileList[i]);

							if (model.getImgName().size() == 0) {
								model.setFileType("3D");
								ImageChoicePanel.this.forEach3D(model, enableFileList[i]);
							} else {
								model.setFileType("2D");
							}

							ImageChoicePanel.this.imgModels.add(model);
						}

						ImageChoicePanel.this.judegExists(ImageChoicePanel.this.imgModels);

						System.out.println(ImageChoicePanel.this.imgModels.toString());

						frame = new ImgJudegFrame();

						frame.setVisible(true);
					} catch (IOException e) {
						e.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
			contentPanel.add(buttonsPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			// 选择图片按钮
			JButton addButton = new JButton("选择背景图片");
			buttonsPanel.add(addButton);
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					FileDialog dialog = new FileDialog(editor, "选择背景图片", FileDialog.LOAD);
					if (lastDir != null) dialog.setDirectory(lastDir);
					dialog.setVisible(true);
					final String file = dialog.getFile();
					final String dir = dialog.getDirectory();
					if (dir == null || file == null || file.trim().length() == 0) return;
					lastDir = dir;
					editor.backgroundChange(new File(dir, file).getAbsolutePath());
				}
			});
			// 清除图片
			JButton clearButton = new JButton("清除背景图片");
			buttonsPanel.add(clearButton);
			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					editor.clearBackground();
				}
			});
		}

	}

	protected void judegExists (List<ImgModel> imgModels) throws Exception {
		for (int i = 0; i < imgModels.size(); i++) {
			ImgModel model = (ImgModel)imgModels.get(i);
			File file = new File(((ImgModel)imgModels.get(i)).getpFileName());
			if (!file.exists()) return;

			File genFile = file.getParentFile();

			for (int picIndex = 0; picIndex < model.getImgName().size(); picIndex++) {
				List nameList = model.getImgName();
				File[] genFileList = genFile.listFiles();
				for (int j = 0; j < genFileList.length; j++) {
					File imgFile = genFileList[j];
					if (imgFile.getName().endsWith(".p")) continue;
					if (imgFile.getName().equals(nameList.get(picIndex))) {
						model.getIsExist().add(Boolean.valueOf(true));
						break;
					}
				}
				if (model.getIsExist().size() >= picIndex + 1) continue;
				model.getIsExist().add(Boolean.valueOf(false));
			}
		}
	}

	protected void forEach2D (ImgModel model, File file) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(file));
		String s = "";
		while ((s = reader.readLine()) != null) {
			if (!s.contains("Image Path")) continue;
			String perImgName = "";
			while (((perImgName = reader.readLine()) != null) && (perImgName.endsWith(".png"))) {
				if (model.getImgName().contains(perImgName)) continue;
				model.getImgName().add(perImgName);
			}
		}

		reader.close();
		System.out.println(model.getImgName().toString());
	}

	protected void forEach3D (ImgModel model, File file) throws Exception {
		FileInputStream fileInputStream = new FileInputStream(file);
		BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
		byte[] b = new byte[1024];
		int cursor = 0;
		String data = "";
		while ((cursor = bufferedInputStream.read(b, 0, b.length)) != -1) {
			data = data + new String(b);
		}

		if (data.contains("assets:")) {
			int index = -1;
			while ((index = data.indexOf("filename:")) != -1) {
				int pngIndex = data.lastIndexOf(".png");
				String imgPathName = data.substring(index + "filename:".length(), pngIndex + 4);

				if (!model.getImgName().contains(imgPathName)) model.getImgName().add(imgPathName);

				data = data.replaceFirst("filename:" + imgPathName, "");
			}

			fileInputStream.close();
			System.out.println(model.getImgName().toString());
		}
	}

	class ImgJudegFrame extends JFrame {
		private List<ImageChoicePanel.ImgModel> models;
		private int x = 0;
		private int y = 0;

		private int length = 1800;
		private int height = 1800;

		private int left = 50;
		private int perLabelHeight = 20;

		private int twoRange = 400;
		private int midRange = 1350;
		private int threeRange = 1400;

		public ImgJudegFrame () {
			this.models = imgModels;

			setTitle("图片情况:");
			Container cont = getContentPane();
			JScrollPane scroll = new JScrollPane();
			add(scroll);
			scroll.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
			setLayout(null);

			setBounds(this.x, this.y, this.length, this.height);

			setResizable(true);
			setVisible(true);

			JLabel jlIndex = new JLabel("索引");
			jlIndex.setBounds(this.x + this.left, this.y, this.x + this.left + 300, this.y + this.perLabelHeight);
			add(jlIndex);

			JLabel jlImgName = new JLabel("图片名字");
			jlImgName.setBounds(this.x + this.left + this.twoRange, this.y, this.x + this.left + 1300, this.y + this.perLabelHeight);
			add(jlImgName);

			JLabel jlImgType = new JLabel("類型");
			jlImgType.setBounds(this.x + this.left + this.midRange, this.y, this.x + this.left + 1450, this.y + this.perLabelHeight);
			add(jlImgType);

			JLabel jlIsLost = new JLabel("是否缺少图片");
			jlIsLost.setBounds(this.x + this.left + this.threeRange, this.y, this.x + this.left + this.length, this.y
				+ this.perLabelHeight);
			add(jlIsLost);
			createLabel();
		}

		private void createLabel () {
			int indexLabelHeight = this.perLabelHeight;
			for (int i = 0; i < this.models.size(); i++) {
				ImageChoicePanel.ImgModel model = (ImageChoicePanel.ImgModel)this.models.get(i);
				boolean needShow = false;

				String lostName = "缺少:";
				for (int index = 0; index < model.getIsExist().size(); index++) {
					if (!((Boolean)model.getIsExist().get(index)).booleanValue()) {
						lostName = lostName + (String)model.getImgName().get(index);
						lostName = lostName + "、";
						needShow = true;
					}

				}

				if (needShow) {
					JLabel jlIndex = new JLabel(i + "  文件名: " + model.getpFName());
					jlIndex.setBounds(this.x + this.left, indexLabelHeight, this.x + this.left + 300, indexLabelHeight
						+ this.perLabelHeight);
					add(jlIndex);

					JLabel jlImgName = new JLabel(model.getImgName().toString());
					jlImgName.setBounds(this.x + this.left + this.twoRange, indexLabelHeight, this.x + this.left + 1300,
						indexLabelHeight + this.perLabelHeight);
					add(jlImgName);

					JLabel jlImgType = new JLabel(model.getFileType());
					jlImgType.setBounds(this.x + this.left + this.midRange, indexLabelHeight, this.x + this.left + 1450,
						indexLabelHeight + this.perLabelHeight);
					add(jlImgType);

					JLabel jlIsLost = new JLabel(lostName.equals("缺少:") ? "不缺图" : lostName);
					jlIsLost.setBounds(this.x + this.left + this.threeRange, indexLabelHeight, this.x + this.left + 1000,
						indexLabelHeight + this.perLabelHeight);
					add(jlIsLost);

					indexLabelHeight += this.perLabelHeight;
				}
			}
		}
	}

	public static class ImgModel {
		String pFileName = "";
		String pFName = "";
		String fileType = "";
		List<String> imgName = new ArrayList();
		List<Boolean> isExist = new ArrayList();

		public List<String> getImgName () {
			return this.imgName;
		}

		public void setImgName (List<String> imgName) {
			this.imgName = imgName;
		}

		public List<Boolean> getIsExist () {
			return this.isExist;
		}

		public void setIsExist (List<Boolean> isExist) {
			this.isExist = isExist;
		}

		public String getpFileName () {
			return this.pFileName;
		}

		public void setpFileName (String pFileName) {
			this.pFileName = pFileName;
		}

		public String getFileType () {
			return this.fileType;
		}

		public void setFileType (String fileType) {
			this.fileType = fileType;
		}

		public String getpFName () {
			return this.pFName;
		}

		public void setpFName (String pFName) {
			this.pFName = pFName;
		}

		public String toString () {
			return "ImgModel [pFileName=" + this.pFileName + ", pFName=" + this.pFName + ", fileType=" + this.fileType
				+ ", imgName=" + this.imgName + ", isExist=" + this.isExist + "]";
		}
	}
}
