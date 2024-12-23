package view;

import javax.swing.*;
import model.DS;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.InvalidKeyException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

public class PanelSignature extends JPanel {
	private static final long serialVersionUID = 1L;
	private DS ds;
	private JTextArea textArea_orderHash;
	private JButton btn_sign, btn_loadOrderHash, btn_saveSignature;
	private JPanel panel_1;
	private JTextField tf_privateKey;
	private JButton btn_loadPrivateKey;
	private JPanel panel_sign;
	private JTextArea textArea_signature;
	private JPanel panel_hash;

	// Biến lưu đường dẫn thư mục cuối cùng
	private String lastDirectory = System.getProperty("user.home");

	public PanelSignature() {
		try {
			ds = new DS("SHA256withRSA");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Cấu hình giao diện chính
		setBackground(new Color(240, 248, 255)); // Xanh nhạt (AliceBlue)
		setLayout(new BorderLayout(10, 10));

		// Font mặc định
		Font defaultFont = new Font("Arial", Font.PLAIN, 14);
		UIManager.put("TextArea.font", defaultFont);
		UIManager.put("Button.font", new Font("Arial", Font.BOLD, 14));
		UIManager.put("TextField.font", defaultFont);

		// Panel chứa nội dung
		JPanel panel = new JPanel(new BorderLayout());
		panel.setOpaque(false);
		add(panel, BorderLayout.CENTER);

		// Panel chữ ký
		panel_sign = new JPanel();
		panel_sign.setBorder(new TitledBorder(new EtchedBorder(), "Chữ ký điện tử", TitledBorder.LEADING,
				TitledBorder.TOP, defaultFont, Color.BLUE));
		panel_sign.setLayout(new BorderLayout(10, 10));
		panel_sign.setBackground(new Color(245, 245, 245)); // Nền xám nhạt
		panel.add(panel_sign, BorderLayout.CENTER);

		textArea_signature = new JTextArea();
		textArea_signature.setEditable(false);
		textArea_signature.setTabSize(1);
		textArea_signature.setRows(3);
		textArea_signature.setColumns(15);
		textArea_signature.setLineWrap(true);
		textArea_signature.setWrapStyleWord(true);
		panel_sign.add(new JScrollPane(textArea_signature), BorderLayout.CENTER);

		// Panel thông tin đơn hàng
		panel_hash = new JPanel();
		panel_hash.setBorder(new TitledBorder(new EtchedBorder(), "Thông tin đơn hàng (đã mã hóa)",
				TitledBorder.LEADING, TitledBorder.TOP, defaultFont, Color.BLUE));
		panel_hash.setLayout(new BorderLayout(10, 10));
		panel_hash.setBackground(new Color(245, 245, 245));
		panel.add(panel_hash, BorderLayout.NORTH);

		textArea_orderHash = new JTextArea(3, 15);
		textArea_orderHash.setFont(new Font("Monospaced", Font.BOLD, 13));
		textArea_orderHash.setTabSize(1);
		textArea_orderHash.setLineWrap(true);
		textArea_orderHash.setWrapStyleWord(true);
		panel_hash.add(new JScrollPane(textArea_orderHash), BorderLayout.CENTER);

		// Panel phía dưới chứa các nút chức năng
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
		southPanel.setOpaque(false);
		btn_sign = new JButton("Tạo chữ ký");
		btn_saveSignature = new JButton("Lưu chữ ký");
		btn_loadOrderHash = new JButton("Tải thông tin đơn hàng");
		southPanel.add(btn_loadOrderHash);
		southPanel.add(btn_sign);
		southPanel.add(btn_saveSignature);
		add(southPanel, BorderLayout.SOUTH);

		// Panel phía trên chứa trường khóa riêng tư
		panel_1 = new JPanel();
		panel_1.setBorder(new EmptyBorder(10, 5, 0, 5));
		panel_1.setLayout(new BorderLayout(10, 10));
		panel_1.setOpaque(false);
		add(panel_1, BorderLayout.NORTH);

		tf_privateKey = new JTextField();
		tf_privateKey.setEditable(false);
		panel_1.add(tf_privateKey, BorderLayout.CENTER);

		btn_loadPrivateKey = new JButton("Tải khoá riêng tư của bạn");
		panel_1.add(btn_loadPrivateKey, BorderLayout.EAST);

		eventHandler();
	}

	private void eventHandler() {
		btn_loadPrivateKey.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(lastDirectory));
				fileChooser.setDialogTitle("Tải khóa riêng tư");

//					Khoá riêng tư lưu dưới dạng tệp .pem
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Khoá riêng tư PEM", "pem");
				fileChooser.setFileFilter(filter);



				int result = fileChooser.showOpenDialog(PanelSignature.this);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = fileChooser.getSelectedFile();
					lastDirectory = selectedFile.getParent();

					//Kiểm tra định dạng file
					if(!selectedFile.getName().endsWith(".pem")) {
						JOptionPane.showMessageDialog(PanelSignature.this, "Vui lòng chọn tệp .pem", "Lỗi", JOptionPane.ERROR_MESSAGE);
						return;
					}


					try {
						ds.privateKey = DS.loadPrivateKey(selectedFile.getAbsolutePath());
						JOptionPane.showMessageDialog(PanelSignature.this, "Đã tải khóa riêng tư!", "Thông báo",
								JOptionPane.INFORMATION_MESSAGE);
						tf_privateKey.setText(selectedFile.getAbsolutePath());
					} catch (Exception e1) {
						e1.printStackTrace();
						JOptionPane.showMessageDialog(PanelSignature.this, "Tải khóa riêng tư thất bại", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				}
			}
		});

		btn_loadOrderHash.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setCurrentDirectory(new File(lastDirectory));
				fileChooser.setDialogTitle("Tải thông tin đơn hàng");

				//Thêm bộ lọc để chỉ cho phép chọn file .txt
				FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files (*.txt)", "txt");
				fileChooser.setFileFilter(filter);



				int result = fileChooser.showOpenDialog(PanelSignature.this);
				if (result == JFileChooser.APPROVE_OPTION) {

					File selectedFile = fileChooser.getSelectedFile();
					lastDirectory = selectedFile.getParent();
					String hash = DS.loadOrderHashFromFile(selectedFile.getAbsolutePath());
					textArea_orderHash.setText(hash);
					textArea_orderHash.setEditable(false);
				}
			}
		});

		btn_sign.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (ds.privateKey == null) {
					JOptionPane.showMessageDialog(PanelSignature.this,
							"Không có khóa riêng tư! Vui lòng tải khóa riêng tư của bạn.", "Lỗi",
							JOptionPane.ERROR_MESSAGE);
				} else {
					String message = textArea_orderHash.getText();
					if (message == null || message.isEmpty()) {
						JOptionPane.showMessageDialog(PanelSignature.this, "Không có thông tin đơn hàng.", "Thông báo",
								JOptionPane.WARNING_MESSAGE);
					} else {
						try {
							String signature = ds.sign(message);
							textArea_signature.setText(signature);
						} catch (InvalidKeyException | SignatureException e1) {
							e1.printStackTrace();
							JOptionPane.showMessageDialog(PanelSignature.this, "Ký thất bại", "Lỗi",
									JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});

		btn_saveSignature.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String signature = textArea_signature.getText();
				if (signature != null && !signature.isEmpty()) {
					try {
						JFileChooser fileChooser = new JFileChooser();
						fileChooser.setCurrentDirectory(new File(lastDirectory));
						fileChooser.setDialogTitle("Lưu chữ ký");
						// Tạo them thời gian cho tệp.
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
						String timestamp = dateFormat.format(new Date());

						//Tạo tên tệp
						fileChooser.setSelectedFile(new File(fileChooser.getCurrentDirectory(), "chuky_"+ timestamp + ".sig"));




						int result = fileChooser.showSaveDialog(PanelSignature.this);
						if (result == JFileChooser.APPROVE_OPTION) {
							File selectedFile = fileChooser.getSelectedFile();

							//Kiểm tra ếu người dùng không nhập .sig.
							if(!selectedFile.getName().endsWith(".sig")) {
								selectedFile = new File(selectedFile.getParent(), selectedFile.getName() + ".sig");
							}


							lastDirectory = selectedFile.getParent();
							DS.saveSignatureToFile(signature, selectedFile.getAbsolutePath());
							JOptionPane.showMessageDialog(PanelSignature.this, "Đã lưu chữ ký!", "Thông báo",
									JOptionPane.INFORMATION_MESSAGE);
						}
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(PanelSignature.this, "Xảy ra lỗi, không thể lưu chữ ký", "Lỗi",
								JOptionPane.ERROR_MESSAGE);
					}
				} else {
					JOptionPane.showMessageDialog(PanelSignature.this, "Chữ ký không tồn tại!", "Cảnh báo",
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				JFrame frame = new JFrame("Tạo chữ ký điện tử - HandmadeStore");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.getContentPane().add(new PanelSignature());


				frame.setSize(800, 600);
				frame.setLocationRelativeTo(null);

				JMenuBar menuBar = new JMenuBar();
				JMenu helpMenu = new JMenu("Trợ giúp");
				JMenuItem guideMenuItem = new JMenuItem("Hướng dẫn sử dụng");
				guideMenuItem.addActionListener(e -> {
					// Tạo nội dung hướng dẫn với định dạng HTML
					String guideMessage = """
									<html>
									<body style='font-family: Arial, sans-serif; font-size: 12px; line-height: 1.6;'>
										<h2 style='color: #2a9d8f;'>HƯỚNG DẪN SỬ DỤNG</h2>
										<ol>
											<li><b>Tải khóa riêng tư:</b><br>
												Nhấn <i>"Tải Khóa Riêng Tư Của Bạn"</i> để chọn và tải lên file chứa khóa riêng tư.
												<span style='color: #e76f51;'><br>Lưu ý:</span> Đảm bảo file khóa riêng tư đã được tạo từ dịch vụ của website HandmadeStore để đảm bảo tính tương thích.
											</li>
											<li><b>Nhập thông tin đơn hàng (đã mã hóa):</b><br>
												Sao chép và dán trực tiếp thông tin đơn hàng đã được mã hóa từ website HandmadeStore vào ô tương ứng.<br>
												Hoặc, nhấn <i>"Tải Thông Tin Đơn Hàng"</i> để tải file chứa thông tin đơn hàng đã mã hóa.
											</li>
											<li><b>Tạo chữ ký:</b><br>
												Sau khi đã cung cấp đầy đủ khóa riêng tư và thông tin đơn hàng, nhấn nút <i>"Tạo Chữ Ký"</i> để tiến hành ký thông tin đơn hàng.
											</li>
											<li><b>Lưu chữ ký:</b><br>
												Nhấn nút <i>"Lưu Chữ Ký"</i> để lưu chữ ký thành file trên máy tính của bạn. File này dùng để gửi lên HandmadeStore.
											</li>
										</ol>
									</body>
									</html>
								""";

					JEditorPane editorPane = new JEditorPane("text/html", guideMessage);
					editorPane.setEditable(false);
					editorPane.setBackground(new Color(255, 255, 255));
					JScrollPane scrollPane = new JScrollPane(editorPane);

					JOptionPane.showMessageDialog(null, scrollPane, "Hướng dẫn sử dụng", JOptionPane.PLAIN_MESSAGE);
				});

				helpMenu.add(guideMenuItem);
				menuBar.add(helpMenu);
				frame.setJMenuBar(menuBar);

				try {
					ImageIcon logo = new ImageIcon(PanelSignature.class.getClassLoader().getResource("logo.jpg"));
					frame.setIconImage(logo.getImage());
				} catch (Exception e) {
					e.printStackTrace();
				}

				frame.setVisible(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
	}
}
