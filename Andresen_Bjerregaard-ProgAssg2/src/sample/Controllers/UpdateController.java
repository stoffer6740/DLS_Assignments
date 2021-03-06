package sample.Controllers;

import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import sample.Server.RmiServer;
import sample.Server.UpdaterObject;

import javax.management.remote.rmi.RMIServer;
import java.rmi.RemoteException;
import java.util.concurrent.TimeUnit;

/**
 * Created by prep on 09-03-2015.
 */
public class UpdateController {
    @FXML
    private TextField txtDelay;
    @FXML
    private TextField txtPeriod;
    @FXML
    private ComboBox<TimeUnit> cmbTimeUnit;
    @FXML
    private Button btnScheduleUpdater;
    @FXML
    private Label lblLastUpdated;
    private ObservableList<TimeUnit> myComboBoxData = FXCollections.observableArrayList();
    private RmiServer server;

    public void startupConfig(boolean serverAvailability) throws RemoteException {
        if (serverAvailability)  {
            btnScheduleUpdater.disableProperty().bind(Bindings.isEmpty(txtDelay.textProperty()).or(Bindings.isEmpty(txtPeriod.textProperty())));
            setMyComboBoxData();
            UpdaterObject updaterSettings = server.getUpdaterSettings();
            txtDelay.setText(String.valueOf(updaterSettings.getDelay()));
            txtPeriod.setText(String.valueOf(updaterSettings.getPeriod()));
            if (updaterSettings.getTimeUnit() == null)
                cmbTimeUnit.getSelectionModel().selectFirst();
            else
                cmbTimeUnit.getSelectionModel().select(updaterSettings.getTimeUnit());
            lblLastUpdated.setText(lblLastUpdated.getText() + updaterSettings.getLastUpdated());
        } else {
            btnScheduleUpdater.setDisable(true);
        }
    }

    private void setMyComboBoxData() {
        myComboBoxData.add(TimeUnit.SECONDS);
        myComboBoxData.add(TimeUnit.MINUTES);
        myComboBoxData.add(TimeUnit.HOURS);

        cmbTimeUnit.setItems(myComboBoxData);
    }

    @FXML
    public void initializeUpdater(ActionEvent actionEvent) throws RemoteException {
        int delay = Integer.parseInt(txtDelay.getText());
        int period = Integer.parseInt(txtPeriod.getText());
        TimeUnit timeUnit = cmbTimeUnit.getSelectionModel().getSelectedItem();

        server.setUpdaterSettings(delay, period, timeUnit);
    }

    private void allowOnlyNumbers(KeyEvent event) {
        String character = event.getCharacter();
        if (!"1234567890".contains(character))
            event.consume();
    }

    @FXML
    public void delayTyped(KeyEvent event) {
        allowOnlyNumbers(event);
    }

    @FXML
    public void periodTyped(KeyEvent event) {
        allowOnlyNumbers(event);
    }

    protected void setServer(RmiServer server) {
        this.server = server;
    }
}
