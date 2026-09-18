package queueless.app;

import queueless.model.*;
import queueless.service.*;
import queueless.store.QueueStore;
import queueless.util.Validator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class QueueLessApp extends JFrame {
    private final QueueManager manager = new QueueManager();
    private final QueueService service = new QueueService(manager);
    private final QueueStore store = new QueueStore("data/queue.csv");

    private final JComboBox<ServiceType> serviceBox = new JComboBox<>(ServiceType.values());
    private final JTextField idField = new JTextField();
    private final JTextField nameField = new JTextField();
    private final JCheckBox priorityBox = new JCheckBox("Priority assistance");
    private final JLabel tokenLabel = new JLabel("No active token");
    private final JLabel statusLabel = new JLabel("Join a queue to see your position.");
    private final DefaultTableModel tableModel = new DefaultTableModel(
            new Object[]{"Token", "Student", "Type", "Priority", "Status", "Time"}, 0) {
        @Override public boolean isCellEditable(int row, int column) { return false; }
    };
    private final JTable table = new JTable(tableModel);

    public QueueLessApp() {
        super("QueueLess | Campus Virtual Queue");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 620);
        setLocationRelativeTo(null);
        buildUi();
        refreshTable();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(12, 12));
        root.setBorder(new EmptyBorder(16, 16, 16, 16));
        setContentPane(root);

        JLabel title = new JLabel("QueueLess");
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        JLabel subtitle = new JLabel("Virtual campus queues without the standing-around part.");
        subtitle.setForeground(new Color(90, 90, 90));

        JPanel header = new JPanel(new BorderLayout());
        header.add(title, BorderLayout.NORTH);
        header.add(subtitle, BorderLayout.SOUTH);
        root.add(header, BorderLayout.NORTH);

        JPanel left = new JPanel();
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createTitledBorder("Get a token"));

        addField(left, "Student ID", idField);
        addField(left, "Name", nameField);
        addField(left, "Service", serviceBox);
        left.add(priorityBox);
        left.add(Box.createVerticalStrut(10));

        JButton join = new JButton("Join Queue");
        JButton cancel = new JButton("Cancel My Token");
        JButton refresh = new JButton("Refresh");

        join.addActionListener(e -> joinQueue());
        cancel.addActionListener(e -> cancelSelected());
        refresh.addActionListener(e -> refreshTable());

        left.add(join);
        left.add(Box.createVerticalStrut(6));
        left.add(cancel);
        left.add(Box.createVerticalStrut(6));
        left.add(refresh);
        left.add(Box.createVerticalGlue());

        JPanel right = new JPanel(new BorderLayout(8, 8));
        right.setBorder(BorderFactory.createTitledBorder("Live Queue"));

        table.setRowHeight(28);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        right.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel status = new JPanel(new GridLayout(2, 1));
        tokenLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        status.add(tokenLabel);
        status.add(statusLabel);
        right.add(status, BorderLayout.SOUTH);

        root.add(left, BorderLayout.WEST);
        root.add(right, BorderLayout.CENTER);

        JPanel admin = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton call = new JButton("Call Next");
        JButton complete = new JButton("Complete Selected");
        call.addActionListener(e -> callNext());
        complete.addActionListener(e -> completeSelected());
        admin.add(call);
        admin.add(complete);
        root.add(admin, BorderLayout.SOUTH);
    }

    private void addField(JPanel panel, String label, JComponent component) {
        panel.add(new JLabel(label));
        panel.add(component);
        component.setMaximumSize(new Dimension(230, 30));
        panel.add(Box.createVerticalStrut(7));
    }

    private void joinQueue() {
        if (!Validator.validStudentId(idField.getText())) {
            showError("Student ID must be 3-20 characters.");
            return;
        }
        if (!Validator.validName(nameField.getText())) {
            showError("Please enter a valid student name.");
            return;
        }

        QueueToken token = service.joinQueue(
                idField.getText(), nameField.getText(),
                (ServiceType) serviceBox.getSelectedItem(),
                priorityBox.isSelected()
        );

        store.save(manager.history());
        tokenLabel.setText("Your token: #" + token.getTokenNumber());
        statusLabel.setText(service.status(token));
        refreshTable();
    }

    private QueueToken selectedToken() {
        int row = table.getSelectedRow();
        if (row < 0) return null;
        int tokenNumber = (Integer) tableModel.getValueAt(row, 0);
        ServiceType selectedType = (ServiceType) serviceBox.getSelectedItem();
        for (QueueToken t : manager.history()) {
            if (t.getTokenNumber() == tokenNumber &&
                    t.getServiceType() == selectedType) {
                return t;
            }
        }
        return null;
    }

    private void cancelSelected() {
        QueueToken token = selectedToken();
        if (token == null) { showError("Select a waiting token first."); return; }
        if (manager.cancel(token)) {
            store.save(manager.history());
            refreshTable();
            statusLabel.setText("Token #" + token.getTokenNumber() + " cancelled.");
        } else {
            showError("Only waiting tokens can be cancelled.");
        }
    }

    private void callNext() {
        ServiceType type = (ServiceType) serviceBox.getSelectedItem();
        QueueToken token = manager.callNext(type);
        if (token == null) {
            showError("No one is waiting at " + type + ".");
            return;
        }
        store.save(manager.history());
        refreshTable();
        statusLabel.setText("Now serving #" + token.getTokenNumber() + " for " + token.getStudent().getName());
    }

    private void completeSelected() {
        QueueToken token = selectedToken();
        if (token == null) { showError("Select a serving token."); return; }
        if (manager.complete(token)) {
            store.save(manager.history());
            refreshTable();
            statusLabel.setText("Token #" + token.getTokenNumber() + " completed.");
        } else {
            showError("Only a serving token can be completed.");
        }
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        ServiceType type = (ServiceType) serviceBox.getSelectedItem();
        if (type == null) return;

        // Show every token that is relevant to this service, including
        // the token currently being served. This makes the table useful
        // for both students and the counter operator.
        for (QueueToken t : manager.history()) {
            if (t.getServiceType() != type) continue;
            if (t.getStatus() == TokenStatus.CANCELLED ||
                    t.getStatus() == TokenStatus.COMPLETED) continue;

            tableModel.addRow(new Object[]{
                    t.getTokenNumber(),
                    t.getStudent().getName(),
                    t.getServiceType(),
                    t.isPriority() ? "Yes" : "No",
                    t.getStatus(),
                    t.createdTime()
            });
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "QueueLess", JOptionPane.WARNING_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new QueueLessApp().setVisible(true));
    }
}
