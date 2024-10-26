package com.example.labcampingv2;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class EquitationActivity extends AppCompatActivity {

    private int nombrePersonnes = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_equitation);

        // Boutons d'incrémentation et décrémentation
        Button btnIncrement = findViewById(R.id.btn_increment);
        Button btnDecrement = findViewById(R.id.btn_decrement);
        final TextView tvNumber = findViewById(R.id.tv_number);

        // Gérer l'incrémentation
        btnIncrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nombrePersonnes++;
                tvNumber.setText(String.valueOf(nombrePersonnes));
            }
        });

        // Gérer la décrémentation
        btnDecrement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (nombrePersonnes > 0) {
                    nombrePersonnes--;
                    tvNumber.setText(String.valueOf(nombrePersonnes));
                }
            }
        });

        // Gérer le bouton Valider
        Button btnValider = findViewById(R.id.btn_valider);
        btnValider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculerFraisEquitation();
            }
        });
    }

    // Méthode pour afficher le dialog personnalisé
    private void showEquitationDialog() {
        // Crée un AlertDialog avec un layout personnalisé
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_equitation, null);
        builder.setView(dialogView);

        // Créer le dialog
        AlertDialog dialog = builder.create();

        // Trouver les éléments du layout
        Button btnDecrement = dialogView.findViewById(R.id.btn_decrement);
        Button btnIncrement = dialogView.findViewById(R.id.btn_increment);
        TextView tvNumber = dialogView.findViewById(R.id.tv_number);

        // Ajouter la logique d'incrémentation/décrémentation
        final int[] counter = {0};  // Utilisation d'un tableau pour modifier la valeur à l'intérieur des lambdas
        btnDecrement.setOnClickListener(view -> {
            if (counter[0] > 0) {
                counter[0]--;
                tvNumber.setText(String.valueOf(counter[0]));
            }
        });

        btnIncrement.setOnClickListener(view -> {
            counter[0]++;
            tvNumber.setText(String.valueOf(counter[0]));
        });

        // Afficher le dialog
        dialog.show();
    }

    private void calculerFraisEquitation() {
        // Récupérer les choix de parcours et de plage
        RadioGroup radioGroupParcours = findViewById(R.id.radio_group_parcours);
        int selectedParcours = radioGroupParcours.getCheckedRadioButtonId();

        RadioGroup radioGroupPlages = findViewById(R.id.radio_group_plages);
        int selectedPlage = radioGroupPlages.getCheckedRadioButtonId();

        // Calcul du tarif
        double tarif = 0;
        if (selectedParcours == R.id.radio_parcours1) {
            tarif = (selectedPlage == R.id.radio_weekend) ? 18.25 : 15.25;
        } else if (selectedParcours == R.id.radio_parcours2) {
            tarif = (selectedPlage == R.id.radio_weekend) ? 25.00 : 22.75;
        }

        // Calcul du coût total
        double totalEquitation = nombrePersonnes * tarif;

        // Afficher le total dans un AlertDialog
        new AlertDialog.Builder(this)
                .setTitle("Total des frais d'Équitation")
                .setMessage("Le montant total à payer est de : $" + totalEquitation)
                .setPositiveButton("OK", null)
                .show();
    }
}