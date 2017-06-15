package jett_apps.grouvie;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static jett_apps.grouvie.LandingPage.PLAN_MESSAGE;

public class CurrentPlanView extends AppCompatActivity {

    private ArrayList<Friend> chosenFriends;
    private String chosenDay;
    private String chosenTime;
    private String chosenCinema;
    private String chosenFilm;
    private Plan p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_plan_view);

        Intent intent = getIntent();
        p = (Plan) intent.getSerializableExtra(PLAN_MESSAGE);
        chosenFilm = p.getSuggestedFilm();
        chosenCinema = p.getSuggestedCinema();
        chosenTime = p.getSuggestedShowTime();
        chosenDay = p.getSuggestedDate();
        chosenFriends = p.getEventMembers();

        // Tapping on any text view takes you to a page where you can suggest
        // a change.
        final Intent changeIntent = new Intent(this, SuggestChange.class);
        changeIntent.putExtra(PLAN_MESSAGE, p);

        TextView film = (TextView) findViewById(R.id.SelectedFilm);
        film.setText(chosenFilm);
        film.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(changeIntent);
            }
        });

        TextView cinema = (TextView) findViewById(R.id.SelectedCinema);
        cinema.setText(chosenCinema);
        cinema.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(changeIntent);
            }
        });

        TextView time = (TextView) findViewById(R.id.SelectedShowtime);
        time.setText(chosenTime);
        time.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(changeIntent);
            }
        });

        TextView day = (TextView) findViewById(R.id.SelectedDay);
        day.setText(chosenDay);
        day.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(changeIntent);
            }
        });

    }

    public void viewGroupReplies(View view) {
        //TODO: Show activity with group replies and option to replan.
        Intent intent = new Intent(view.getContext(), GroupView.class);
        intent.putExtra(PLAN_MESSAGE, p);
        startActivity(intent);
    }

    public void cantGo(View view) {
        CurrentPlans.deletePlan(p, this);
        JSONObject json_data = new JSONObject();
        try {
            json_data.accumulate("phone_number", p.getLeaderPhoneNum());
            json_data.accumulate("leader", p.getLeaderPhoneNum());
            json_data.accumulate("showtime", p.getSuggestedShowTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent intent = new Intent(view.getContext(), LandingPage.class);
        startActivity(intent);
        //TODO: Show activity with group replies and option to replan.
    }

    public void cancelPlan(View view) {
        CurrentPlans.deletePlan(p, this);
        // Delete the plan on the server
        JSONObject json_data = new JSONObject();
        try {
            json_data.accumulate("leader", p.getLeaderPhoneNum());
            json_data.accumulate("showtime", p.getSuggestedShowTime());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new ServerContact().execute("delete_plan", json_data.toString());
        Intent intent = new Intent(view.getContext(), LandingPage.class);
        startActivity(intent);
        //TODO: Show activity with group replies and option to replan.

    }
}
