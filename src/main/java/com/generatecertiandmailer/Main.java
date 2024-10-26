package com.generatecertiandmailer;

import com.generatecertiandmailer.models.UserInfo;

import javax.mail.AuthenticationFailedException;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        //Input Email panel
        String[] emailCredentials = showEmailInputPanel();
        if (emailCredentials == null) {
            System.out.println("Email sending cancelled.");
            return;
        }

        String email = emailCredentials[0];
        String password = emailCredentials[1];

        //Email subject & body input panel
        String[] emailContent = showEmailContentPanel();
        if (emailContent == null) {
            System.out.println("Email creation cancelled.");
            return;
        }

        String emailSubject = emailContent[0];
        String emailBody = emailContent[1];

        //To choose the Excel file
        String excelFilePath = showFileChooser("Select Excel File", JFileChooser.FILES_ONLY);
        if (excelFilePath == null) {
            System.out.println("Excel file selection cancelled.");
            return;
        }

        //To choose the output directory
        String certificateOutputDir = showFileChooser("Select Output Directory", JFileChooser.DIRECTORIES_ONLY);
        if (certificateOutputDir == null) {
            System.out.println("Output directory selection cancelled.");
            return;
        }

        ExcelReader excelReader = new ExcelReader();
        List<UserInfo> userInfoList = excelReader.readExcel(excelFilePath);

        CertificateGenerator certificateGenerator = new CertificateGenerator();
        EmailSender emailSender = new EmailSender();

        //Progress dialog
        JDialog progressDialog = new JDialog();
        progressDialog.setTitle("Sending Emails...");
        progressDialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        progressDialog.setSize(300, 100);
        progressDialog.setLocationRelativeTo(null);
        progressDialog.add(new JLabel("Sending emails, please wait..."), BorderLayout.CENTER);
        progressDialog.setVisible(true);

        new Thread(() -> {
            boolean workDone = true;
            for (UserInfo userInfo : userInfoList) {
                String certificatePath = certificateOutputDir + File.separator + userInfo.getCertificateId() + ".png";

                try {
                    certificateGenerator.generateCertificate(userInfo, certificatePath);

                    //Concatenating greeting with body
                    String personalizedBody = "Dear " + userInfo.getName() + ",\n\n" + emailBody;

                    emailSender.sendEmail(userInfo.getEmailId(), emailSubject, personalizedBody, certificatePath, email, password);

                } catch (AuthenticationFailedException e) {
                    JOptionPane.showMessageDialog(null, "Authentication failed: Invalid email or password. \n\nPlease check your credentials.", "Authentication Error", JOptionPane.ERROR_MESSAGE);
                    workDone = false;
                    System.exit(1);
                } catch (Exception e) {
                    System.err.println("Failed to process certificate for " + userInfo.getName() + ": " + e.getMessage());
                }
            }
            progressDialog.dispose();

            //Success msg
            if (workDone) {
                JOptionPane.showMessageDialog(null, "All emails sent successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }).start();
    }

    private static String showFileChooser(String dialogTitle, int selectionMode) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle(dialogTitle);
        fileChooser.setFileSelectionMode(selectionMode);
        int userSelection = fileChooser.showOpenDialog(null);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            return file.getAbsolutePath();
        } else {
            return null;
        }
    }

    //Email address and Password input field.
    private static String[] showEmailInputPanel() {
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Email Authentication", 0, 0, new Font("Arial", Font.BOLD, 16), Color.DARK_GRAY));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email Address:"), gbc);

        gbc.gridx = 1;
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("App Password:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel linkLabel = new JLabel("<html><a href=''>How to generate an app password</a></html>");
        linkLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        linkLabel.setForeground(Color.BLUE);
        linkLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        linkLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    Desktop.getDesktop().browse(new java.net.URI("https://support.google.com/mail/answer/185833?hl=en"));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        panel.add(linkLabel, gbc);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Email Authentication", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                return null;
            }

            String email = emailField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Both email and app password are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } else {
                return new String[]{email, password};
            }
        }
    }

    // Panel to get email subject and body input
    private static String[] showEmailContentPanel() {
        JTextField subjectField = new JTextField(20);
        JTextArea bodyArea = new JTextArea(10, 20);
        bodyArea.setLineWrap(true);
        bodyArea.setWrapStyleWord(true);
        JScrollPane bodyScrollPane = new JScrollPane(bodyArea, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Email Content", 0, 0, new Font("Arial", Font.BOLD, 16), Color.DARK_GRAY));

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Email Subject:"), gbc);

        gbc.gridx = 1;
        panel.add(subjectField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(new JLabel("Email Body:"), gbc);

        gbc.gridy = 2;
        panel.add(bodyScrollPane, gbc);

        while (true) {
            int result = JOptionPane.showConfirmDialog(null, panel, "Email Content", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (result == JOptionPane.CANCEL_OPTION) {
                return null;
            }

            String subject = subjectField.getText().trim();
            String body = bodyArea.getText().trim();

            if (subject.isEmpty() || body.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Both email subject and body are required.", "Input Error", JOptionPane.WARNING_MESSAGE);
            } else {
                return new String[]{subject, body};
            }
        }
    }
}