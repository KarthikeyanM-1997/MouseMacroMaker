import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.dispatcher.SwingDispatchService;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

public class Recorder extends JFrame implements NativeKeyListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	Player macroPlayer;

	JButton btnStartRecording;
	volatile boolean recording = false;
	boolean running = false;

	ArrayList<Point> macroPoints;
	// Server myServer;

	public Recorder() {
		// Set the event dispatcher to a swing safe executor service.
		GlobalScreen.setEventDispatcher(new SwingDispatchService());

		setTitle("Mouse Macro Maker");
		setBounds(100, 100, 450, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		getContentPane().setLayout(null);

		btnStartRecording = new JButton("Start Recording");
		btnStartRecording.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (recording) {
					btnStartRecording.setText("Start Recording");
					recording = (false);

					// macroPoints = new ArrayList<Point>();

					for (Point p : macroPoints) {
						System.out.println(p.getX() + " " + p.getY());
					}

				} else {
					btnStartRecording.setText("Stop Recording");
					recording = (true);

					macroPoints = new ArrayList<Point>();
				}
				System.out.println(recording);
			}
		});
		btnStartRecording.setBounds(12, 163, 158, 25);
		getContentPane().add(btnStartRecording);

		JButton btnNewButton = new JButton("Load Macro");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (recording) {

				} else {
					try {
						String line;

						FileReader fileReader = new FileReader("Macro.txt");

						BufferedReader bufferedReader = new BufferedReader(fileReader);

						macroPoints = new ArrayList<Point>();
						StringTokenizer st;
						while ((line = bufferedReader.readLine()) != null) {
							st = new StringTokenizer(line, " ");
							String mBtn = st.nextToken();
							macroPoints.add(new Point((int) Double.parseDouble(st.nextToken()),
									(int) Double.parseDouble(st.nextToken())));
						}
						for (Point p : macroPoints) {
							System.out.println("L " + p.getX() + " " + p.getY());
						}

						bufferedReader.close();
					} catch (FileNotFoundException ex) {
						System.out.println("Unable to open file");
					} catch (IOException ex) {
						System.out.println("Error reading file");
					}
				}

			}

		});
		btnNewButton.setBounds(254, 201, 121, 25);
		getContentPane().add(btnNewButton);

		JButton btnSaveMacro = new JButton("Save Macro");
		btnSaveMacro.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (recording) {

				} else {
					PrintWriter writer = null;
					try {
						writer = new PrintWriter("Macro.txt", "UTF-8");
					} catch (FileNotFoundException | UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					for (Point p : macroPoints) {
						writer.println("L " + p.getX() + " " + p.getY());
					}
					writer.close();
				}

			}

		});
		btnSaveMacro.setBounds(254, 163, 121, 25);
		getContentPane().add(btnSaveMacro);

		JLabel lblMouseMacroMaker = new JLabel("Mouse Macro Maker V 1.0");
		lblMouseMacroMaker.setBounds(122, 13, 199, 16);
		getContentPane().add(lblMouseMacroMaker);

		JLabel lblPresspTo = new JLabel("Press the button to Start / Stop recording.");
		lblPresspTo.setBounds(12, 52, 408, 16);
		getContentPane().add(lblPresspTo);

		JLabel lblPresssTo = new JLabel("Press \"S\" to record a left mouse click.");
		lblPresssTo.setBounds(12, 81, 240, 16);
		getContentPane().add(lblPresssTo);

		JLabel lblPressdTo = new JLabel("Press \"P\" to start \\ stop a playing a loaded Macro.");
		lblPressdTo.setBounds(12, 111, 309, 16);
		getContentPane().add(lblPressdTo);

		addWindowListener(this);
		setVisible(true);
	}

	public void windowOpened(WindowEvent e) {
		// Initialze native hook.
		try {
			GlobalScreen.registerNativeHook();
		} catch (NativeHookException ex) {
			System.err.println("There was a problem registering the native hook.");
			System.err.println(ex.getMessage());
			ex.printStackTrace();

			System.exit(1);
		}

		GlobalScreen.addNativeKeyListener(this);

		Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
		logger.setLevel(Level.OFF);
		Handler[] handlers = Logger.getLogger("").getHandlers();
		for (int i = 0; i < handlers.length; i++) {
			handlers[i].setLevel(Level.OFF);
		}
	}

	public void windowClosed(WindowEvent e) {
		// Clean up the native hook.
		try {
			GlobalScreen.unregisterNativeHook();
		} catch (NativeHookException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.runFinalization();
		System.exit(0);
	}

	public void windowClosing(WindowEvent e) {
		/* Unimplemented */ }

	public void windowIconified(WindowEvent e) {
		/* Unimplemented */ }

	public void windowDeiconified(WindowEvent e) {
		/* Unimplemented */ }

	public void windowActivated(WindowEvent e) {
		/* Unimplemented */ }

	public void windowDeactivated(WindowEvent e) {
		/* Unimplemented */ }

	public void nativeKeyReleased(NativeKeyEvent e) {
		if (e.getKeyCode() == NativeKeyEvent.VC_S && recording && !running) {
			// JOptionPane.showMessageDialog(null, recording + " ");

			macroPoints.add(MouseInfo.getPointerInfo().getLocation());

			System.out.println(macroPoints.size());
		}

		if (e.getKeyCode() == NativeKeyEvent.VC_P && !recording) {
			running = !running;
			if (running) {
				macroPlayer = new Player(macroPoints);
				macroPlayer.running = true;

				Thread t1 = new Thread(macroPlayer, "T1");
				t1.start();

			} else {
				macroPlayer.stop();
			}
		}
	}

	public void nativeKeyPressed(NativeKeyEvent e) {
	}

	public void nativeKeyTyped(NativeKeyEvent e) {
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Recorder();
			}
		});
	}
}