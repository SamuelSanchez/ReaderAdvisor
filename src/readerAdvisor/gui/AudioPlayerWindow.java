package readerAdvisor.gui;

import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.PlayingTimer;
import readerAdvisor.speech.audioPlayer.AudioPlayer;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;

public class AudioPlayerWindow extends JDialog implements ActionListener {
	private AudioPlayer player = new AudioPlayer();
	private Thread playbackThread;
	private PlayingTimer timer;

	private boolean isPlaying = false;
	private boolean isPause = false;
	
	private String audioFilePath;
	private String lastOpenPath;
	
	private JLabel labelFileName = new JLabel("Playing File:");
	private JLabel labelTimeCounter = new JLabel("00:00:00");
	private JLabel labelDuration = new JLabel("00:00:00");

    //-------------------- Create action buttons --------------------//
	private JButton buttonOpen = new JButton("Open");
	private JButton buttonPlay = new JButton("Play");
	private JButton buttonPause = new JButton("Pause");
	
	private JSlider sliderTime = new JSlider();
	
	//-------------------- Create action button icons --------------------//
	private ImageIcon iconOpen = MenuBarUtils.createIcon("open_audio.png");
	private ImageIcon iconPlay = MenuBarUtils.createIcon("play_audio.png");
	private ImageIcon iconStop = MenuBarUtils.createIcon("stop_audio.png");
	private ImageIcon iconPause = MenuBarUtils.createIcon("pause_audio.png");

    // TODO: Should we make this class a singleton as well? Take a look at the GC output
    @SuppressWarnings("unused")
	public AudioPlayerWindow() {
		setUpWindow(null);
	}

    public AudioPlayerWindow(JFrame windowToAdaptRelativeLocation) {
        setUpWindow(windowToAdaptRelativeLocation);
    }

    // TODO: Do not allow the user to resize this Window
    private void setUpWindow(JFrame windowToAdaptRelativeLocation){
        // Set the title of the Window
        // Do not allow any other Window in this application to be managed while this window is opened
        this.setTitle("Audio Player");
        this.setModal(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setLocationRelativeTo(windowToAdaptRelativeLocation);
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        this.setVisible(false);
        // Set Image Icon
        this.setIconImage(MenuBarUtils.createIcon("electronic-wave.png").getImage());
        // Stop playing the audio when this window is closed
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                setVisible(true);
                // If the audio is playing then stop it before the window closes
                System.out.println("Window is closing");
                if (isPlaying) {
                    System.out.println("Window is closing********************************");
                    // Interrupt threads
                    timer.reset();
                    timer.interrupt();
                    player.stop();
                    playbackThread.interrupt();
                    // Do not close until the clip stops running
                    while (player.getAudioClip().isRunning()) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                    }// while
                }// if
            }
        });

    }

    public void displayAudioPlayerWindowWithOpenButton() {
        displayAudioPlayerWindowWithOpenButton(true);
    }

    public void displayAudioPlayerWindowWithOpenButton(boolean displayOpenButton){
        // Create the GUI buttons
        createDisplayWindow(displayOpenButton);
        // Set this window properties
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
        // Display the window over all other windows
        this.setAlwaysOnTop(true);
    }

    private void createDisplayWindow(boolean displayOpenButton){
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.insets = new Insets(5, 5, 5, 5);
        constraints.anchor = GridBagConstraints.WEST;

        buttonOpen.setFont(new Font("Sans", Font.BOLD, 14));
        buttonOpen.setIcon(iconOpen);

        buttonPlay.setFont(new Font("Sans", Font.BOLD, 14));
        buttonPlay.setIcon(iconPlay);
        buttonPlay.setEnabled(false);

        buttonPause.setFont(new Font("Sans", Font.BOLD, 14));
        buttonPause.setIcon(iconPause);
        buttonPause.setEnabled(false);

        labelTimeCounter.setFont(new Font("Sans", Font.BOLD, 12));
        labelDuration.setFont(new Font("Sans", Font.BOLD, 12));

        sliderTime.setPreferredSize(new Dimension(400, 20));
        sliderTime.setEnabled(false);
        sliderTime.setValue(0);

        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 3;
        this.add(labelFileName, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        this.add(labelTimeCounter, constraints);

        constraints.gridx = 1;
        this.add(sliderTime, constraints);

        constraints.gridx = 2;
        this.add(labelDuration, constraints);

        // Add the buttons panel
        JPanel panelButtons = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));

        constraints.gridwidth = 3;
        constraints.gridx = 0;
        constraints.gridy = 2;

        panelButtons.add(buttonPlay);
        panelButtons.add(buttonPause);
        // Add action listener to the buttons
        buttonPlay.addActionListener(this);
        buttonPause.addActionListener(this);

        // Display the Open button
        if(displayOpenButton){
            panelButtons.add(buttonOpen);
            buttonOpen.addActionListener(this);
        }

        this.add(panelButtons, constraints);
    }

	/**
	 * Handle click events on the buttons.
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		Object source = event.getSource();
		if (source instanceof JButton) {
			JButton button = (JButton) source;
			if (button == buttonOpen) {
				openFile();
			} else if (button == buttonPlay) {
				if (!isPlaying) {
					playBack();
				} else {
					stopPlaying();
				}
			} else if (button == buttonPause) {
				if (!isPause) {
					pausePlaying();
				} else {
					resumePlaying();
				}
			}
		}
	}

    /*
     * Set the path of the directory where the JFileChooser should open
     */
    public void setLastOpenPath(String lastOpenPath){
        this.lastOpenPath = lastOpenPath;
    }

	private void openFile() {
		JFileChooser fileChooser;
		
		if (lastOpenPath != null && !lastOpenPath.equals("")) {
			fileChooser = new JFileChooser(lastOpenPath);
		} else {
			fileChooser = new JFileChooser();
		}
		
		FileFilter wavFilter = new FileFilter() {
			@Override
			public String getDescription() {
				return "Sound file (*.WAV)";
			}

			@Override
			public boolean accept(File file) {
				/*if (file.isDirectory()) {
					return true;
				} else {
					return file.getName().toLowerCase().endsWith(".wav");
				}*/
                return (file.isDirectory() || file.getName().toLowerCase().endsWith(".wav"));
			}
		};

		
		fileChooser.setFileFilter(wavFilter);
		fileChooser.setDialogTitle("Open Audio File");
		fileChooser.setAcceptAllFileFilterUsed(false);

		int userChoice = fileChooser.showOpenDialog(this);
		if (userChoice == JFileChooser.APPROVE_OPTION) {
			audioFilePath = fileChooser.getSelectedFile().getAbsolutePath();
			lastOpenPath = fileChooser.getSelectedFile().getParent();
			if (isPlaying || isPause) {
				stopPlaying();
				while (player.getAudioClip().isRunning()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException ex) {
						ex.printStackTrace();
					}
				}
			}
			playBack();
		}
	}

	/**
	 * Start playing back the sound.
	 */
	private void playBack() {
		timer = new PlayingTimer(labelTimeCounter, sliderTime);
		timer.start();
		isPlaying = true;
		playbackThread = new Thread(new Runnable() {

			@Override
			public void run() {
				try {

					buttonPlay.setText("Stop");
					buttonPlay.setIcon(iconStop);
					buttonPlay.setEnabled(true);
					
					buttonPause.setText("Pause");
					buttonPause.setEnabled(true);
					
					player.load(audioFilePath);
					timer.setAudioClip(player.getAudioClip());
					labelFileName.setText("Playing File: " + audioFilePath);
					sliderTime.setMaximum((int) player.getClipSecondLength());
					
					labelDuration.setText(player.getClipLengthString());
					player.play();
					
					resetControls();

				} catch (UnsupportedAudioFileException ex) {
					JOptionPane.showMessageDialog(AudioPlayerWindow.this,
							"The audio format is unsupported!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (LineUnavailableException ex) {
					JOptionPane.showMessageDialog(AudioPlayerWindow.this,
							"Could not play the audio file because line is unavailable!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				} catch (IOException ex) {
					JOptionPane.showMessageDialog(AudioPlayerWindow.this,
							"I/O error while playing the audio file!", "Error", JOptionPane.ERROR_MESSAGE);
					resetControls();
					ex.printStackTrace();
				}

			}
		});

		playbackThread.start();
	}

	private void stopPlaying() {
		isPause = false;
		buttonPause.setText("Pause");
		buttonPause.setEnabled(false);
		timer.reset();
		timer.interrupt();
		player.stop();
		playbackThread.interrupt();
	}
	
	private void pausePlaying() {
		buttonPause.setText("Resume");
		isPause = true;
		player.pause();
		timer.pauseTimer();
		playbackThread.interrupt();
	}
	
	private void resumePlaying() {
		buttonPause.setText("Pause");
		isPause = false;
		player.resume();
		timer.resumeTimer();
		playbackThread.interrupt();		
	}
	
	private void resetControls() {
		timer.reset();
		timer.interrupt();

		buttonPlay.setText("Play");
		buttonPlay.setIcon(iconPlay);
		
		buttonPause.setEnabled(false);
		
		isPlaying = false;		
	}
}