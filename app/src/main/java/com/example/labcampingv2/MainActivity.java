package com.example.labcampingv2;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText etNom, etPrenom;
    private Button btnDateArrivee, btnDateDepart, btnReserver, btnDecrement, btnIncrement;
    private TextView tvDateArrivee, tvDateDepart, tvNumber;
    private Calendar arriveeDate, departDate;
    private int counter = 0;  // Compteur pour le nombre de personnes
    private double totalFraisEquitation = 0;  // Variable globale pour stocker le total
    private double totalFraisCanot = 0;
    private double totalFraisEscalade = 0;
    private double fraisSejour = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeUI();
        setupListeners();

    }

    private void initializeUI() {
        etNom = findViewById(R.id.et_nom);
        etPrenom = findViewById(R.id.et_prenom);
        btnDateArrivee = findViewById(R.id.btn_date_arrivee);
        btnDateDepart = findViewById(R.id.btn_date_depart);
        tvDateArrivee = findViewById(R.id.tv_date_arrivee);
        tvDateDepart = findViewById(R.id.tv_date_depart);
        btnReserver = findViewById(R.id.btn_reserver);
        tvNumber = findViewById(R.id.tv_number);
        btnDecrement = findViewById(R.id.btn_decrement);
        btnIncrement = findViewById(R.id.btn_increment);

        arriveeDate = Calendar.getInstance();
        departDate = Calendar.getInstance();
    }

    private void setupListeners() {
        btnDecrement.setOnClickListener(view -> {
            if (counter > 0) {
                counter--;
                tvNumber.setText(String.valueOf(counter));
            }
        });

        btnIncrement.setOnClickListener(view -> {
            counter++;
            tvNumber.setText(String.valueOf(counter));
        });

        btnDateArrivee.setOnClickListener(view -> showDatePickerDialog(arriveeDate, tvDateArrivee));
        btnDateDepart.setOnClickListener(view -> showDatePickerDialog(departDate, tvDateDepart));
        btnReserver.setOnClickListener(view -> calculerEtAfficherFrais());
    }



    // Méthode pour afficher le DatePickerDialog
    private void showDatePickerDialog(Calendar date, TextView textView) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    date.set(Calendar.YEAR, year);
                    date.set(Calendar.MONTH, monthOfYear);
                    date.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel(textView, date);
                },
                date.get(Calendar.YEAR),
                date.get(Calendar.MONTH),
                date.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    // Mise à jour du TextView avec la date choisie
    private void updateLabel(TextView textView, Calendar date) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        textView.setText(sdf.format(date.getTime()));
    }


    private void calculerEtAfficherFrais() {
        if (!isInputValid()) return;

        // Vérification que les dates d'arrivée et de départ sont définies
        if (tvDateArrivee.getText().toString().isEmpty() || tvDateDepart.getText().toString().isEmpty()) {
            showErrorDialog("Erreur", "Veuillez sélectionner les dates d'arrivée et de départ.");
            return;
        }

        int nbPersonnes = counter;
        long nbJours = calculateNbJours();
        fraisSejour = calculerFraisSejour(nbJours, nbPersonnes);

        new AlertDialog.Builder(this)
                .setTitle("Réservation confirmée")
                .setMessage("Vous avez réservé pour " + nbJours + " jour(s) avec des frais de séjour de : $"
                        + String.format(Locale.getDefault(), "%.2f", fraisSejour))
                .setPositiveButton("OK", null)
                .show();
    }


    private boolean isInputValid() {
        if (etNom.getText().toString().trim().isEmpty()) {
            showErrorDialog("Erreur", "Veuillez entrer votre nom.");
            return false;
        }
        if (etPrenom.getText().toString().trim().isEmpty()) {
            showErrorDialog("Erreur", "Veuillez entrer votre prénom.");
            return false;
        }
        if (counter == 0) {
            showErrorDialog("Erreur", "Veuillez sélectionner au moins une personne.");
            return false;
        }
        if (departDate.before(arriveeDate)) {
            showErrorDialog("Erreur", "La date de départ ne peut pas être avant la date d'arrivée.");
            return false;
        }
        return true;
    }

    private double calculerFraisSejour(long nbJours, int nbPersonnes) {
        Calendar currentDate = (Calendar) arriveeDate.clone();
        double totalFrais = 0;

        for (int i = 0; i < nbJours; i++) {
            if (currentDate.before(createDate(31, Calendar.MAY))) {
                totalFrais += 18.90 * nbPersonnes;
            } else if (currentDate.after(createDate(31, Calendar.AUGUST))) {
                totalFrais += 20.25 * nbPersonnes;
            } else {
                totalFrais += 23.25 * nbPersonnes;
            }
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
        }
        return totalFrais;
    }

    // Crée une date spécifique pour comparer les périodes
    private Calendar createDate(int day, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month);
        return cal;
    }

    private long calculateNbJours() {
        LocalDate arriveeLocalDate = convertToLocalDate(arriveeDate);
        LocalDate departLocalDate = convertToLocalDate(departDate);
        return ChronoUnit.DAYS.between(arriveeLocalDate, departLocalDate);
    }

    private LocalDate convertToLocalDate(Calendar date) {
        return LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH));
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuItem m1 = menu.add(Menu.NONE,Menu.FIRST,0,"Equitation");
        MenuItem m2 = menu.add(Menu.NONE,Menu.FIRST +1,0,"Escalade");
        MenuItem m3 = menu.add(Menu.NONE,Menu.FIRST +2,0,"Cano");
        MenuItem m4 = menu.add(Menu.NONE,Menu.FIRST+3,0,"Facture");
        MenuItem m5 = menu.add(Menu.NONE,Menu.FIRST +4,4,"Quit");

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();  // Récupère l'ID de l'élément de menu cliqué

        if (id == Menu.FIRST) {  // L'ID de l'élément "Équitation"
            // Appeler la méthode qui affiche le Dialog d'équitation
            showEquitationDialog();

            // Lancer l'activité EquitationActivity avec un Intent
            //Intent intent = new Intent(MainActivity.this, EquitationActivity.class);
           // startActivity(intent);  // Démarrer l'activité Equitation
            return true;
        } else if (id == Menu.FIRST + 1) {
            // Gérer les autres clics sur les éléments du menu (Escalade, Cano, etc.)
            showEscaladeDialog();
            return true;

        }
        else if (id == Menu.FIRST + 2) {
            // Gérer les autres clics sur les éléments du menu (Escalade, Cano, etc.)
            showCanotDialog();
            return true;
        }

        else if (id == Menu.FIRST + 3) {
            afficherFacture();  // Appeler la méthode pour afficher la facture
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Méthode pour afficher le dialog personnalisé
    private void showEquitationDialog() {
        AlertDialog dialog = createEquitationDialog();
        dialog.show();
    }

    private AlertDialog createEquitationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_equitation, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();
        RadioGroup radioGroupParcours = dialogView.findViewById(R.id.radio_group_parcours);
        RadioGroup radioGroupPlages = dialogView.findViewById(R.id.radio_group_plages);
        Button btnValider = dialogView.findViewById(R.id.btn_valider);

        btnValider.setOnClickListener(view -> {
            if (counter == 0) {
                showErrorDialog("Erreur", "Veuillez sélectionner au moins une personne.");
                return;
            }

            totalFraisEquitation = calculateEquitationCost(radioGroupParcours, radioGroupPlages);
            showResultDialog("Total des frais d'Équitation", "Le montant total à payer est de : $" + totalFraisEquitation);
            dialog.dismiss();
        });
        return dialog;
    }

    private void handleEquitationValidation(AlertDialog dialog, RadioGroup radioGroupParcours, RadioGroup radioGroupPlages) {
        if (counter == 0) {
            showErrorDialog("Erreur", "Veuillez sélectionner au moins une personne.");
            return;
        }

        double totalEquitation = calculateEquitationCost(radioGroupParcours, radioGroupPlages);
        showResultDialog("Total des frais d'Équitation", "Le montant total à payer est de : $" + totalEquitation);
        totalFraisEquitation = totalEquitation;

        dialog.dismiss();
    }

    private double calculateEquitationCost(RadioGroup radioGroupParcours, RadioGroup radioGroupPlages) {
        long nbJours = calculateNbJours();
        int selectedParcours = radioGroupParcours.getCheckedRadioButtonId();
        int selectedPlage = radioGroupPlages.getCheckedRadioButtonId();

        double tarif = (selectedParcours == R.id.radio_parcours1)
                ? (selectedPlage == R.id.radio_weekend ? 18.25 : 15.25)
                : (selectedPlage == R.id.radio_weekend ? 25.00 : 22.75);

        return counter * tarif * nbJours;
    }

    /**Cano**/
    private void showCanotDialog() {
        // Crée un AlertDialog avec un layout personnalisé
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_canot, null);
        builder.setView(dialogView);

        // Créer le dialog
        AlertDialog dialog = builder.create();

        // Trouver les éléments du layout avec les bons identifiants
        RadioGroup radioGroupPlages = dialogView.findViewById(R.id.radio_group_plages);
        Button btnValider = dialogView.findViewById(R.id.btn_valider);
        TextView tvBlocsNumber = dialogView.findViewById(R.id.tv_blocs_number);; // Assure-toi que cet ID correspond
        Button btnDecrementBlocs = dialogView.findViewById(R.id.btn_decrement_blocs);
        Button btnIncrementBlocs = dialogView.findViewById(R.id.btn_increment_blocs);

        // Gestion de l'incrémentation et décrémentation du nombre de blocs de 2 heures
        final int[] blocsCounter = {0};
        btnDecrementBlocs.setOnClickListener(view -> {
            if (blocsCounter[0] > 0) {
                blocsCounter[0] -= 2;  // Décrémenter par 2 heures
                tvBlocsNumber.setText(String.valueOf(blocsCounter[0]));
            }
        });

        btnIncrementBlocs.setOnClickListener(view -> {
            blocsCounter[0] += 2;  // Incrémenter par 2 heures
            tvBlocsNumber.setText(String.valueOf(blocsCounter[0]));
        });

        // Gérer le clic sur le bouton Valider pour calculer les frais de canot
        btnValider.setOnClickListener(view -> {
            // Vérifier si le nombre de personnes est au moins de 1
            if (counter == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Erreur")
                        .setMessage("Veuillez sélectionner au moins une personne à l'accueil.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            // Vérifier si des blocs de 2 heures ont été sélectionnés
            if (blocsCounter[0] == 0) {
                new AlertDialog.Builder(this)
                        .setTitle("Erreur")
                        .setMessage("Veuillez sélectionner au moins 2 heures.")
                        .setPositiveButton("OK", null)
                        .show();
                return;
            }

            // Déterminer le tarif en fonction du choix de la plage (semaine ou week-end)
            int selectedPlage = radioGroupPlages.getCheckedRadioButtonId();
            double tarif = (selectedPlage == R.id.radio_weekend) ? 29.55 : 22.35;

            // Calcul du coût total : nombre de personnes * tarif * nombre de blocs de 2 heures
            totalFraisCanot = counter * tarif * (blocsCounter[0] / 2);

            // Afficher le résultat dans un AlertDialog
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Total des frais de Canot")
                    .setMessage("Le montant total à payer est de : $" + totalFraisCanot)
                    .setPositiveButton("OK", null)
                    .show();

            dialog.dismiss();
        });

        // Afficher le dialog
        dialog.show();
    }



/** Escalade**/
private void showEscaladeDialog() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    View dialogView = getLayoutInflater().inflate(R.layout.dialog_escalade, null);
    builder.setView(dialogView);

    AlertDialog dialog = builder.create();

    // Identifiants pour les éléments du dialogue
    TextView tvHoursNumber = dialogView.findViewById(R.id.tv_hours_number);
    Button btnDecrementHours = dialogView.findViewById(R.id.btn_decrement_hours);
    Button btnIncrementHours = dialogView.findViewById(R.id.btn_increment_hours);
    Button btnValider = dialogView.findViewById(R.id.btn_valider);

    final int[] hoursCounter = {0};
    btnDecrementHours.setOnClickListener(view -> {
        if (hoursCounter[0] > 0) {
            hoursCounter[0]--;
            tvHoursNumber.setText(String.valueOf(hoursCounter[0]));
        }
    });

    btnIncrementHours.setOnClickListener(view -> {
        hoursCounter[0]++;
        tvHoursNumber.setText(String.valueOf(hoursCounter[0]));
    });

    btnValider.setOnClickListener(view -> {
        if (counter == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage("Veuillez sélectionner au moins une personne à l'accueil.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        if (hoursCounter[0] == 0) {
            new AlertDialog.Builder(this)
                    .setTitle("Erreur")
                    .setMessage("Veuillez sélectionner au moins une heure.")
                    .setPositiveButton("OK", null)
                    .show();
            return;
        }

        double tarif = 10.00; // Tarif par heure et par personne pour l'escalade
        totalFraisEscalade = counter * tarif * hoursCounter[0];

        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Total des frais d'Escalade")
                .setMessage("Le montant total à payer est de : $" + totalFraisEscalade)
                .setPositiveButton("OK", null)
                .show();

        dialog.dismiss();
    });

    dialog.show();
}





    private void afficherFacture() {
        String nom = etNom.getText().toString();
        String prenom = etPrenom.getText().toString();
        int nbPersonnes = counter;
        long nbJours = calculateNbJours();
        double fraisSejour = calculerFraisSejour(nbJours, nbPersonnes);

        StringBuilder factureMessage = new StringBuilder();
        factureMessage.append("Nom : ").append(nom).append(" ").append(prenom).append("\n\n");
        factureMessage.append("Nombre de personnes : ").append(nbPersonnes).append("\n\n");
        factureMessage.append("Frais de séjour : $").append(String.format(Locale.getDefault(), "%.2f", fraisSejour)).append("\n");
        factureMessage.append("Équitation : $").append(String.format(Locale.getDefault(), "%.2f", totalFraisEquitation)).append("\n");
        factureMessage.append("Canot : $").append(String.format(Locale.getDefault(), "%.2f", totalFraisCanot)).append("\n");
        factureMessage.append("Escalade : $").append(String.format(Locale.getDefault(), "%.2f", totalFraisEscalade)).append("\n");

        new AlertDialog.Builder(this)
                .setTitle("Facture")
                .setMessage(factureMessage.toString())
                .setPositiveButton("OK", null)
                .show();
    }

    // Final functions
    private void showResultDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
}








