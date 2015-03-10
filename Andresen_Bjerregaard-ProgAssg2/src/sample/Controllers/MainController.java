package sample.Controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import sample.Server.CurrencyLoader;
import sample.Server.ExchangeRateTask;
import sample.Server.RmiServer;
import sample.Shared.RegistryConfig;
import sample.Shared.ServerConfig;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.stream.Collectors;

public class MainController {
    @FXML
    private Label lblTotalCurrency;
    @FXML
    private ComboBox<String> cmbSourceCurrency;
    @FXML
    private ComboBox<String> cmbTargetCurrency;
    @FXML
    private TextField txtAmount;
    @FXML
    private Label lblServerStatus;
    @FXML
    private Button btnSubmit;
    @FXML
    private BarChart chartCurrency;
    private static RmiServer server;
    private CurrencyLoader currencyCache = CurrencyLoader.INSTANCE;
    private ObservableList<String> myComboBoxData = FXCollections.observableArrayList();
    private String splitChar = "\\$";
    private int shortIndex = 0;
    private int longIndex = 1;
    private boolean serverStatus;
    private Random rnd = new Random();

    public void startUpConfig() {
        serverStatus = hostAvailabilityCheck();
        // if the server is online
        if (serverStatus) {
            // fill the combo boxes with data
            setMyComboBoxData();
            // disable submit button if the amount field is empty
            btnSubmit.disableProperty().bind(Bindings.isEmpty(txtAmount.textProperty()));
        } else
            // disable the submit button altogether if the server is offline
            btnSubmit.setDisable(false);
    }

    private boolean hostAvailabilityCheck() {
        lblServerStatus.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize()));

        // Socket is ignored
        try (Socket ignored = new Socket(ServerConfig.SERVER_IP, RegistryConfig.REGISTRY_PORT)) {
            try {
                server = (RmiServer) Naming.lookup(ServerConfig.SERVER_ADDRESS);
                lblServerStatus.setText("online");
                lblServerStatus.setTextFill(Color.GREEN);
                populateChart();
                return true;
            } catch (NotBoundException | MalformedURLException | RemoteException e) {
//                e.printStackTrace();
            }
        } catch (IOException ex) {
            lblServerStatus.setText("offline");
            lblServerStatus.setTextFill(Color.RED);
            return false;
        }
        return false;
    }

    private void setMyComboBoxData() {
        myComboBoxData.addAll(getCurrencies().stream().map(currency -> currency.split(splitChar)[longIndex]).collect(Collectors.toList()));

        cmbSourceCurrency.setItems(myComboBoxData);
        cmbSourceCurrency.getSelectionModel().selectFirst();
        cmbTargetCurrency.setItems(myComboBoxData);
        cmbTargetCurrency.getSelectionModel().selectFirst();
    }

    private List<String> getCurrencies() {
        return currencyCache.getCurrencyList()
                .stream()
                .sorted((s1, s2) -> s1.split(splitChar)[longIndex].compareTo(s2.split(splitChar)[longIndex]))
                .collect(Collectors.toList());
    }

    @FXML
    private void onSubmit(ActionEvent event) throws ParseException, RemoteException {
        int sourceCurrencyId = cmbSourceCurrency.getSelectionModel().getSelectedIndex();
        String selectedSourceCurrencyShort = getCurrencies().get(sourceCurrencyId).split(splitChar)[shortIndex];
        String selectedSourceCurrencyLong = getCurrencies().get(sourceCurrencyId).split(splitChar)[longIndex];
        int targetCurrencyId = cmbTargetCurrency.getSelectionModel().getSelectedIndex();
        String selectedTargetCurrencyShort = getCurrencies().get(targetCurrencyId).split(splitChar)[shortIndex];
        String selectedTargetCurrencyLong = getCurrencies().get(targetCurrencyId).split(splitChar)[longIndex];

        NumberFormat format = NumberFormat.getInstance(Locale.FRANCE);
        Number number = format.parse(txtAmount.getText().trim());
        double amount = number.doubleValue();

        ExchangeRateTask exchangeRateTask = new ExchangeRateTask(server, selectedSourceCurrencyShort, selectedTargetCurrencyShort, amount);
        Thread t = new Thread(exchangeRateTask);
        t.setDaemon(true);
        t.start();

        exchangeRateTask.setOnSucceeded(thread -> lblTotalCurrency.setText(formatThousands(amount) + " " + addCorrectEndLetter(selectedSourceCurrencyLong) + " = " + formatThousands(exchangeRateTask.getValue()) + " " + addCorrectEndLetter(selectedTargetCurrencyLong)));
    }

    private String formatThousands(double input) {
        DecimalFormat df = new DecimalFormat("###,###.##");

        return df.format(input);
    }

    @FXML
    private void amountTyped(KeyEvent event) {
        String character = event.getCharacter();
        if (!"1234567890,".contains(character))
            event.consume();
    }

    private String addCorrectEndLetter(String input) {
        char lastCharacter = input.charAt(input.length() - 1);

        if (isVowel(lastCharacter))
            return input + "r";
        else switch (lastCharacter) {
            case 'n':
                return input;
            default:
                return input + "s";
        }
    }

    private static boolean isVowel(char c) {
        return "EIUeiu".indexOf(c) != -1;
    }

    @FXML
    public void updaterSettings(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/sample/GUI/updateSettings.fxml"));
            Parent root = loader.load();

            UpdateController updateController = loader.getController();
            updateController.setServer(server);
            updateController.startupConfig(serverStatus);

            Stage primaryStage = new Stage();
            primaryStage.getIcons().add(new Image("coin.png"));
            primaryStage.setTitle("Updater settings");
            primaryStage.initModality(Modality.APPLICATION_MODAL);
            primaryStage.setScene(new Scene(root, primaryStage.getWidth(), primaryStage.getHeight()));
            primaryStage.setResizable(false);
            primaryStage.show();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void populateChart() throws RemoteException {
        chartCurrency.getXAxis().setLabel("Currencies");
        chartCurrency.getYAxis().setLabel("x per 1 EUR");
        XYChart.Series seriesDKK = new XYChart.Series();


        for (int i = 0; i < 5; i++) {
            int index = rnd.nextInt(getCurrencies().size());
            String targetCurrency = getCurrencies().get(index).split(splitChar)[shortIndex];
            double exchangeRate = server.exchangeRate("EUR", targetCurrency);
            seriesDKK.getData().add(new XYChart.Data<>(targetCurrency, exchangeRate));
        }

        chartCurrency.getData().addAll(seriesDKK);
    }
}