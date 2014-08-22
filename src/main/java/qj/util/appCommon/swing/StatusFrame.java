package qj.util.appCommon.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.util.HashMap;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;

import qj.util.funct.Fs;
import qj.util.funct.P1;

public class StatusFrame extends QFrame {
	private static final long serialVersionUID = 1L;

	private Container _contentPane = new JPanel();
	{
		_contentPane.setLayout(new BorderLayout());
	}

    private JLabel _statusLabel = new JLabel();

    private JPanel statusPane = new JPanel();

	private JPanel customStatusPane;

    private HashMap<String, JLabel> customStatusLabelMap = new HashMap<String, JLabel>();

    private TimerTask clearStatusTask;

    private java.util.Timer timer = new java.util.Timer(true);


    public StatusFrame() throws HeadlessException {
		this(null);
    }

    public StatusFrame(String title) throws HeadlessException {
        super(title);

		statusPane.setPreferredSize(new Dimension(0, 22));
		statusPane.setLayout(new BorderLayout());

		_statusLabel.setFont(new Font("Dialog", Font.PLAIN, 12));
		statusPane.add(_statusLabel, BorderLayout.CENTER);
        
        Container cp = super.getContentPane();
        cp.add(statusPane, BorderLayout.SOUTH);
        // contentPane.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        super.getContentPane().add(_contentPane, BorderLayout.CENTER);

    }


	public void addStatusPane(String title) {
		if (customStatusPane == null) {
			customStatusPane = new JPanel();
			customStatusPane.setLayout(new BoxLayout(customStatusPane, BoxLayout.LINE_AXIS));
			statusPane.add(customStatusPane, BorderLayout.EAST);
		}

		JLabel label = new JLabel(" ");
		customStatusLabelMap.put(title, label);
		customStatusPane.add(new JSeparator(JSeparator.VERTICAL));
		customStatusPane.add(new JLabel(" " + title + ": "));
		customStatusPane.add(label);
	}


    public Container getContentPane() {
        return _contentPane;
    }

    public void setContentPane(Container contentPane) {
        this._contentPane = contentPane;
    }


    public void setCustomStatus(String title, String value) {
        JLabel label = customStatusLabelMap.get(title);
        label.setText(value + " ");
    }

    public P1<String> setStatus = Fs.p1("setStatus", this);
    public P1<String> setStatusP = Fs.p1("setStatusP", this);
    public void setStatus(String status) {
        _statusLabel.setText(" " + status);
        if (clearStatusTask != null) {
			clearStatusTask.cancel();
		}
        clearStatusTask = new TimerTask() {public void run() {
            _statusLabel.setText("");
        }};
        timer.schedule(clearStatusTask, 4500);
    }
    public void setStatusP(String status) {
        _statusLabel.setText(" " + status);
    }

    
}
