package org.example;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Scanner;

public class VokalUAPDarkFinal extends JFrame {

    CardLayout cardLayout = new CardLayout();
    JPanel mainPanel = new JPanel(cardLayout);

    JTextArea inputArea;
    JTextField searchField;
    JTable table;
    DefaultTableModel model;
    JLabel laporanLabel;
    TableRowSorter<TableModel> sorter;

    File file = new File("data_vokal.csv");

    int selectedRowModel = -1;
    boolean isUpdate = false;

    Color bgDark = new Color(140, 97, 100);
    Color bgCard = new Color(251, 251, 251);
    Color accent = new Color(200, 60, 60);
    Color textLight = Color.BLACK;

    public VokalUAPDarkFinal() {
        setTitle("UAP Pemrograman Lanjut - Statistik Teks");
        setSize(650, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainPanel.setBackground(bgDark);

        mainPanel.add(dashboard(), "dashboard");
        mainPanel.add(formInput(), "input");
        mainPanel.add(listData(), "list");
        mainPanel.add(laporan(), "laporan");

        add(mainPanel);
        loadData();
        updateLaporan();
        setVisible(true);
    }

    JPanel dashboard() {
        JPanel p = darkPanel(new GridLayout(5, 1, 15, 15));

        JLabel title = new JLabel("APLIKASI PENGHITUNG VOKAL & KONSONAN", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(textLight);

        p.add(new JLabel());
        p.add(title);
        p.add(navBtn("Input Data", "input"));
        p.add(navBtn("List Data", "list"));
        p.add(navBtn("Laporan", "laporan"));
        return p;
    }

    JPanel formInput() {
        JPanel p = darkPanel(new BorderLayout(15, 15));

        JLabel title = new JLabel("Form Input & Update", JLabel.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 22));
        title.setForeground(textLight);

        inputArea = new JTextArea();
        inputArea.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputArea.setBackground(bgCard);
        inputArea.setForeground(Color.BLACK);
        inputArea.setLineWrap(true);
        inputArea.setWrapStyleWord(true);
        inputArea.setMargin(new Insets(10, 10, 10, 10));

        JScrollPane sp = new JScrollPane(inputArea);

        JButton simpan = actionBtn("Simpan");
        simpan.addActionListener(e -> simpanAtauUpdate());

        JPanel bawah = new JPanel(new GridLayout(1, 2, 10, 10));
        bawah.setBackground(bgDark);
        bawah.add(backBtn());
        bawah.add(simpan);

        p.add(title, BorderLayout.NORTH);
        p.add(sp, BorderLayout.CENTER);
        p.add(bawah, BorderLayout.SOUTH);
        return p;
    }

    JPanel listData() {
        JPanel p = darkPanel(new BorderLayout(10, 10));

        model = new DefaultTableModel(
                new String[]{"Kalimat", "A", "I", "U", "E", "O", "Vokal", "Konsonan", "Kata"}, 0);

        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setSelectionBackground(accent);
        table.setShowGrid(true);

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }

        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        JScrollPane sp = new JScrollPane(table);

        searchField = new JTextField();
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                sorter.setRowFilter(RowFilter.regexFilter(searchField.getText()));
            }
        });

        JButton update = actionBtn("Update");
        update.addActionListener(e -> ambilUntukUpdate());

        JButton hapus = actionBtn("Hapus");
        hapus.addActionListener(e -> hapusData());

        JPanel bawah = new JPanel(new BorderLayout(10, 10));
        bawah.setBackground(bgDark);

        JPanel tombol = new JPanel(new GridLayout(1, 3, 10, 10));
        tombol.setBackground(bgDark);
        tombol.add(backBtn());
        tombol.add(update);
        tombol.add(hapus);

        bawah.add(searchField, BorderLayout.NORTH);
        bawah.add(tombol, BorderLayout.SOUTH);

        p.add(sp, BorderLayout.CENTER);
        p.add(bawah, BorderLayout.SOUTH);
        return p;
    }

    JPanel laporan() {
        JPanel p = darkPanel(new BorderLayout());
        laporanLabel = new JLabel("", JLabel.CENTER);
        laporanLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        laporanLabel.setForeground(Color.WHITE);
        p.add(laporanLabel, BorderLayout.CENTER);
        p.add(backBtn(), BorderLayout.SOUTH);
        return p;
    }

    void simpanAtauUpdate() {
        String text = inputArea.getText().trim();
        if (text.isEmpty()) return;

        Object[] d = hitung(text);

        if (!isUpdate) model.addRow(d);
        else {
            for (int i = 0; i < d.length; i++)
                model.setValueAt(d[i], selectedRowModel, i);
            isUpdate = false;
        }

        inputArea.setText("");
        saveFile();
        updateLaporan();
    }
    void ambilUntukUpdate() {
        int v = table.getSelectedRow();
        if(v<0) return;
        selectedRowModel = table.convertRowIndexToModel(v);
        inputArea.setText(model.getValueAt(selectedRowModel,0).toString());
        isUpdate = true;
        cardLayout.show(mainPanel,"input");
    }

    void hapusData() {
        int v = table.getSelectedRow();
        if(v>=0){
            model.removeRow(table.convertRowIndexToModel(v));
            saveFile();
            updateLaporan();
        }
    }

    Object[] hitung(String t) {
        t = t.toLowerCase();
        int a=0,i=0,u=0,e=0,o=0,k=0;
        for(char c:t.toCharArray()){
            if("aiueo".indexOf(c)>=0){
                if(c=='a')a++;
                if(c=='i')i++;
                if(c=='u')u++;
                if(c=='e')e++;
                if(c=='o')o++;
            } else if(Character.isLetter(c)) k++;
        }
        int kata = t.trim().isEmpty()?0:t.trim().split("\\s+").length;
        return new Object[]{t,a,i,u,e,o,a+i+u+e+o,k,kata};
    }

    void saveFile() {
        try (PrintWriter pw = new PrintWriter(file)) {

            // HEADER CSV
            pw.println("Kalimat,A,I,U,E,O,Total Vokal,Konsonan,Jumlah Kata");

            // DATA
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    pw.print(model.getValueAt(i, j));
                    if (j < model.getColumnCount() - 1) pw.print(",");
                }
                pw.println();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void loadData() {
        if(!file.exists()) return;
        try(Scanner sc=new Scanner(file)){
            while(sc.hasNextLine())
                model.addRow(sc.nextLine().split(","));
        } catch(Exception e){}
    }

    void updateLaporan() {
        laporanLabel.setText("Total Data Tersimpan: " + model.getRowCount());
    }

    //  UTIL
    JPanel darkPanel(LayoutManager l){
        JPanel p = new JPanel(l);
        p.setBackground(bgDark);
        p.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
        return p;
    }

    JButton navBtn(String t,String pg){
        JButton b=actionBtn(t);
        b.addActionListener(e->cardLayout.show(mainPanel,pg));
        return b;
    }

    JButton backBtn(){ return navBtn("Kembali","dashboard"); }

    JButton actionBtn(String t){
        JButton b=new JButton(t);
        b.setBackground(bgCard);
        b.setForeground(textLight);
        b.setFont(new Font("Segoe UI",Font.BOLD,14));
        b.setFocusPainted(false);
        return b;
    }

    public static void main(String[] args){
        SwingUtilities.invokeLater(VokalUAPDarkFinal::new);
    }
}

   
