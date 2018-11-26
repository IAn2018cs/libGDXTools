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

import java.awt.FileDialog;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ListSelectionModel;

import com.badlogic.gdx.graphics.g2d.ParticleEmitter;
import com.badlogic.gdx.graphics.g2d.ParticleEmitter.SpriteMode;
import com.badlogic.gdx.utils.Array;

class ImageChoicePanel extends EditorPanel {
	JPanel imagesPanel;
	JList imageList;
	DefaultListModel<String> imageListModel;
	String lastDir;

	public ImageChoicePanel (final ParticleEditor editor, String name, String description) {
		super(null, name, description);
		JPanel contentPanel = getContentPanel();
		{
			JPanel buttonsPanel = new JPanel(new GridLayout(2, 1));
			contentPanel.add(buttonsPanel, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.NORTHWEST,
				GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));
			// Ñ¡ÔñÍ¼Æ¬°´Å¥
			JButton addButton = new JButton("Ñ¡Ôñ±³¾°Í¼Æ¬");
			buttonsPanel.add(addButton);
			addButton.addActionListener(new ActionListener() {
				public void actionPerformed (ActionEvent event) {
					FileDialog dialog = new FileDialog(editor, "Ñ¡Ôñ±³¾°Í¼Æ¬", FileDialog.LOAD);
					if (lastDir != null) dialog.setDirectory(lastDir);
					dialog.setVisible(true);
					final String file = dialog.getFile();
					final String dir = dialog.getDirectory();
					if (dir == null || file == null || file.trim().length() == 0) return;
					lastDir = dir;
					editor.backgroundChange(new File(dir, file).getAbsolutePath());
				}
			});
			// Çå³ýÍ¼Æ¬
			JButton clearButton = new JButton("Çå³ý±³¾°Í¼Æ¬");
			buttonsPanel.add(clearButton);
			clearButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed (ActionEvent e) {
					editor.clearBackground();
				}
			});
		}

	}

}
