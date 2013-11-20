package readerAdvisor.gui;

import edu.cmu.sphinx.frontend.window.RaisedCosineWindower;
import edu.cmu.sphinx.tools.audio.AudioData;
import edu.cmu.sphinx.tools.audio.AudioPanel;
import edu.cmu.sphinx.util.props.PropertySheet;
import readerAdvisor.gui.tool.MenuBarUtils;
import readerAdvisor.gui.tool.PlayingTimer;
import readerAdvisor.speech.audioPlayer.AudioPlayer;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
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

    private AudioInputStream microphoneAudio = null;
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

    // ------------------- Audio and Spectrogram Panel ------------------- //
    private PropertySheet propertySheet = null;
    private JPanel audioWaveAndCheckBoxPanel = new JPanel(new BorderLayout());
    private JCheckBox audioWaveCheckBox = new JCheckBox("Display waves and spectrogram");
    private JPanel audioWavePanel = new JPanel(new FlowLayout());
    private JScrollPane audioWaveScrollPanel = new JScrollPane(audioWavePanel);
    private boolean displayVisualAudio = false;

	//-------------------- Create action button icons --------------------//
	private ImageIcon iconOpen = MenuBarUtils.createIcon("open_audio.png");
	private ImageIcon iconPlay = MenuBarUtils.createIcon("play_audio.png");
	private ImageIcon iconStop = MenuBarUtils.createIcon("stop_audio.png");
	private ImageIcon iconPause = MenuBarUtils.createIcon("pause_audio.png");

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
                if (isPlaying) {
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

    /*
     * Display the audio player with the button to select the audio files
     * if no audio stream input has been passed to this class
     */
    @SuppressWarnings("unused")
    public void displayAudioPlayerWindow(){
        displayAudioPlayerWindowWithOpenButton(microphoneAudio == null);
    }

    /*
     * Display the audio player with the button to select wave files
     */
    @SuppressWarnings("unused")
    public void displayAudioPlayerWindowWithOpenButton() {
        displayAudioPlayerWindowWithOpenButton(true);
    }

    /*
     * Display the audio player with/without the button to select audio files
     * true - display the button and play wave files
     * false - play microphone data
     */
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
        this.setLayout(new BorderLayout());

        // Add the Audio and Spectrogram panel - Only for the microphone data
        if(microphoneAudio != null){
            try{
                if(displayVisualAudio){
                    // Display the audio energy in a panel
                    createVisualAudioPanel(new AudioData(microphoneAudio));
                    audioWaveScrollPanel.setVisible(false);
                    // Event listeners
                    audioWaveCheckBox.addItemListener(new ItemListener() {
                        @Override
                        public void itemStateChanged(ItemEvent e) {
                            int state = e.getStateChange();
                            // Enable - Save audio file
                            if (state == 1) {
                                audioWaveAndCheckBoxPanel.setSize(getSize());
                                audioWaveScrollPanel.setVisible(true);
                            }
                            // Disable - Do not save audio file
                            if (state == 2) {
                                audioWaveScrollPanel.setVisible(false);
                            }
                            // TODO: Find out how to fix the window size
                            pack();
                            repaint();
                        }
                    });
                    // Add visual audio panel
                    audioWaveAndCheckBoxPanel.add(audioWaveCheckBox, BorderLayout.NORTH);
                    audioWaveAndCheckBoxPanel.add(audioWaveScrollPanel, BorderLayout.CENTER);
                    this.add(audioWaveAndCheckBoxPanel, BorderLayout.NORTH);
                }
            }catch (IOException ioe){
                ioe.printStackTrace();
            }
        }

        JPanel audioPlayerPanel = new JPanel(new GridBagLayout());
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
        audioPlayerPanel.add(labelFileName, constraints);

        constraints.anchor = GridBagConstraints.CENTER;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        audioPlayerPanel.add(labelTimeCounter, constraints);

        constraints.gridx = 1;
        audioPlayerPanel.add(sliderTime, constraints);

        constraints.gridx = 2;
        audioPlayerPanel.add(labelDuration, constraints);

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

        audioPlayerPanel.add(panelButtons, constraints);

        // Microphone data will be played
        if(!displayOpenButton){
            // Display the message whether there's data to be played or not
            labelFileName.setText("Playing File: " + audioFilePath);
            // Audio stream available
            if(microphoneAudio != null){
                buttonPlay.setEnabled(true);
                buttonPause.setEnabled(false);
            }
        }
        // Add Audio Player Panel to the main window
        this.add(audioPlayerPanel, BorderLayout.CENTER);
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

    /*
     * Set the microphone audio to play
     */
    public void setMicrophoneData(AudioInputStream microphoneAudio){
        this.microphoneAudio = microphoneAudio;
    }

    /*
     * Returns the input stream data
     */
    public AudioInputStream getMicrophoneData(){
        return this.microphoneAudio;
    }

    /*
     * Set the name of the path of the audio being played
     */
    public void setFullNameOfAudioToPlay(String audioToPlay){
        this.audioFilePath = audioToPlay;
    }

    /*
     * Set Sphinx PropertySheet in order to display the audio panel
     */
    public void setPropertySheet(PropertySheet propertySheet){
        this.propertySheet = propertySheet;
    }

    /*
     * True - Display the audio wave, false - otherwise
     */
    public void setDisplayVisualAudio(boolean displayVisualAudio){
        this.displayVisualAudio = displayVisualAudio;
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

                    // If the microphone stream is available, then use it
                    if(microphoneAudio != null){
                        // Reset the data before playing it to make sure that it is usable
                        microphoneAudio.reset();
                        player.load(microphoneAudio);
                    }
                    else{
					    player.load(audioFilePath);
                    }
					timer.setAudioClip(player.getAudioClip());
					labelFileName.setText("Playing File: " + audioFilePath);
					sliderTime.setMaximum((int) player.getClipSecondLength());
					
					labelDuration.setText(player.getClipLengthString());
                    // Play the audio at once
					player.play();
					// Reset the controls
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

    /*
     * Create the Audio panel and Spectrogram panel
     */
    private void createVisualAudioPanel(AudioData audioData){
        // Add audio and spectrogram panels
        float windowShiftInMs = propertySheet.getFloat(RaisedCosineWindower.PROP_WINDOW_SHIFT_MS);
        float windowShiftInSamples = windowShiftInMs * audioData.getAudioFormat().getSampleRate() / 1000.0f;
        AudioPanel audioPanel = new AudioPanel(audioData, 1.0f/windowShiftInSamples, 0.004f);
        audioWavePanel.add(audioPanel);
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